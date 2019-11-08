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

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.gui.modelwrappers.ViewExpenceForAddition;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.accounting.Account;
import bbdebet2.kernel.accounting.Expence;
import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import bbdebet2.kernel.mailing.TextTemplate;
import bbdebet2.kernel.mailing.TextTemplateLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;


public class MakeExpence extends Applet {

    @FXML
    private ChoiceBox<Account> accountChoiceBox;
    @FXML
    private TextField amountTextField;
    @FXML
    private TextField commentTextField;

    @FXML
    private TableView<ViewExpenceForAddition> expenceTableView;
    @FXML
    private TableColumn<ViewExpenceForAddition, String> accountTableColumn;
    @FXML
    private TableColumn<ViewExpenceForAddition, String> amountTableColumn;
    @FXML
    private TableColumn<ViewExpenceForAddition, String> commentTableColumn;

    @FXML
    private ChoiceBox<Account> fromAccountChoiceBox;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Account userAccount;


    public static void createAndDisplayDialog(Expence initialExpence) {
        FXMLLoader loader = Applet.createAndDisplayDialog("Før utlegg", "MakeExpenceView");
        ((MakeExpence) loader.getController()).add(initialExpence);
    }


    protected void add(Expence expence) {
        if (expence != null) expenceTableView.getItems().add(new ViewExpenceForAddition(expence));
    }


    @FXML
    private void saveExpences(ActionEvent event) {
        if (fromAccountChoiceBox.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Du må velge hvilken konto som dekker utlegget");
            alert.setHeaderText("Ingen betalingsmåte");
            alert.showAndWait();
            return;
        }

        Account fromAccount = fromAccountChoiceBox.getSelectionModel().getSelectedItem();

        User user = null;

        if (fromAccount == userAccount) {
            String error = null;

            if (usernameTextField.getText().trim().equals(""))
                error = "Du må skrive et brukernavn for å velge " + fromAccount + " fom betalingsmåte";
            if (!kernel.getUserList().contains(usernameTextField.getText()))
                error = "Finner ikke bruker " + usernameTextField.getText();

            if (error != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, error);
                alert.setHeaderText("Feil i brukernavn");
                alert.showAndWait();
                return;
            }
            user = kernel.getUserList().find(usernameTextField.getText());
        }

        double totalAmount = 0;

        for (ViewExpenceForAddition viewExpenceForAddition : expenceTableView.getItems()) {
            Expence expence = viewExpenceForAddition.getExpenceObject().resolve(fromAccount);
            kernel.getLedger().add(expence);
            Kernel.getLogger().log("Tracking expence: " + expence);

            totalAmount += expence.getAmount();
        }

        Kernel.getLogger().log("Total expence: " + CurrencyFormatter.format(totalAmount) + ", paying from " + fromAccount);


        if (user != null) {
            kernel.getTransactionHandler().newMoneyInsert(user, totalAmount);

            User finalUser = user;
            Task sendEmailTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        kernel.getEmailSender().sendMail(finalUser, "Påfyll av penger", TextTemplateLoader.getTemplate(TextTemplate.USERADDEDMONEY));
                    } catch (MessagingException | InvalidEncryptionException e) {
                        Kernel.getLogger().log(e);
                    }
                    return null;
                }
            };
            new Thread(sendEmailTask).start();
        }

        Main.getCurrentAdminController().repaintUserList();
        Main.getCurrentAdminController().repaintAccounting();

        exit(event);
    }


    @FXML
    private void handleAddExpence(ActionEvent event) {
        Account account = accountChoiceBox.getSelectionModel().getSelectedItem();
        if (account == null) {
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountTextField.getText().replaceAll(",", "."));
        } catch (NumberFormatException e) {
            return;
        }

        add(new Expence(account, amount, commentTextField.getText()));
    }


    private void setupTableView() {
        accountTableColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        amountTableColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        commentTableColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }


    private void setupChoiceBoxes() {
        accountChoiceBox.getItems().addAll(kernel.getAccounts().getAll());
        fromAccountChoiceBox.getItems().addAll(kernel.getAccounts().getPaymentOptions());

        fromAccountChoiceBox.setOnAction(
            e -> usernameTextField.setDisable(fromAccountChoiceBox.getSelectionModel().getSelectedItem() != userAccount)
        );
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupTableView();
        setupChoiceBoxes();
        userAccount = kernel.getAccounts().fromAccountNumber(2000);
    }
}
