/*
 * Copyright (C) 2019  Mathias Lohne
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mathiaslohne.bbdebet2.gui.applets;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.gui.customelements.WaitingDialog;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.accounting.Account;
import com.mathiaslohne.bbdebet2.kernel.accounting.Expense;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import com.mathiaslohne.bbdebet2.kernel.mailing.InvalidEncryptionException;
import com.mathiaslohne.bbdebet2.kernel.mailing.TextTemplate;
import com.mathiaslohne.bbdebet2.kernel.mailing.TextTemplateLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddBalance extends Applet {

    @FXML
    private TextField userNameInput;
    @FXML
    private TextField moneyInput;
    @FXML
    private CheckBox sendEmailInput;
    @FXML
    private TextArea emailTextInput;
    @FXML
    private ChoiceBox<Account> insertMethodInput;

    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Legg til penger", "AddBalanceScreen");
    }


    @SuppressWarnings("unchecked")
    @FXML
    public void addMoney(ActionEvent event) {
        User u = kernel.getUserList().find(userNameInput.getText());

        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Finner ikke bruker");
            alert.showAndWait();
            return;
        }

        double amount = 0;

        try {
            amount = Double.parseDouble(moneyInput.getText().replaceAll(",", "."));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Skriv gyldig tall i beløpfeltet");
            alert.showAndWait();
            return;
        }

        if (insertMethodInput.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Velg en betalingsmåte");
            alert.showAndWait();
            return;
        }

        Account to = insertMethodInput.getSelectionModel().getSelectedItem();
        Account from = kernel.getAccounts().getUserAccount();
        kernel.getLedger().add(
            new Expense("Innskudd " + u)
                .addTransaction(new Expense.Transaction(to, amount, Expense.TransactionType.ADD))
                .addTransaction(new Expense.Transaction(from, amount, Expense.TransactionType.SUB))
        );

        kernel.getTransactionHandler().newMoneyInsert(u, amount);

        if (sendEmailInput.isSelected()) {
            Alert alert = new WaitingDialog("Sender epost...");
            alert.show();
            Task sendEmailTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        kernel.getEmailSender().sendMail(u, "Påfyll av penger", emailTextInput.getText());
                    } catch (MessagingException | InvalidEncryptionException e) {
                        Kernel.getLogger().log(e);
                    }
                    return null;
                }
            };

            sendEmailTask.setOnSucceeded(e -> alert.close());
            new Thread(sendEmailTask).start();
        }

        Main.getCurrentAdminController().repaintUserList();
    }


    @FXML
    public void newHandleXLSFileView(ActionEvent event) {
        HandleXLSFile.createAndDisplayDialog();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        emailTextInput.setText(TextTemplateLoader.getTemplate(TextTemplate.USERADDEDMONEY));
        insertMethodInput.getItems().addAll(kernel.getAccounts().getInsertOptions());
    }
}
