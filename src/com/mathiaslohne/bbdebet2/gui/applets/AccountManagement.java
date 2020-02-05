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

import com.mathiaslohne.bbdebet2.gui.customelements.DeleteAccountDialog;
import com.mathiaslohne.bbdebet2.gui.customelements.NewAccountDialog;
import com.mathiaslohne.bbdebet2.kernel.accounting.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class AccountManagement extends Applet {

    @FXML
    private GridPane accountGridView;

    private List<TextField> accountNumberInputs;
    private List<TextField> accountNameInputs;
    private List<CheckBox> isPaymentOptionInputs;
    private List<CheckBox> isInsertOptionInputs;

    private List<Account> shownAccounts;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Kontoer", "AccountManagementView");
    }


    @FXML
    public void handleDeleteAccount(ActionEvent event) {
        Optional<Integer> optionalAccountNumber = new DeleteAccountDialog().showAndWait();
        optionalAccountNumber.ifPresent(integer -> {
            kernel.getAccounts().deleteAccount(integer);
            redrawGridView();
        });
    }


    @FXML
    public void handleNewAccount(ActionEvent event) {
        Optional<Account> optionalAccount = new NewAccountDialog().showAndWait();
        optionalAccount.ifPresent(account -> {
            kernel.getAccounts().add(optionalAccount.get());
            redrawGridView();
        });
    }


    @FXML
    public void handleChanges(ActionEvent event) {
        for (int i = 0; i < shownAccounts.size(); i++) {
            shownAccounts.get(i).setNumber(Integer.parseInt(accountNumberInputs.get(i).getText()));
            shownAccounts.get(i).setName(accountNameInputs.get(i).getText());
            shownAccounts.get(i).setPaymentOption(isPaymentOptionInputs.get(i).isSelected());
            shownAccounts.get(i).setInsertOption(isInsertOptionInputs.get(i).isSelected());
        }

        exit(event);
    }


    private void redrawGridView() {
        accountGridView.getChildren().removeAll(accountNumberInputs);
        accountGridView.getChildren().removeAll(accountNameInputs);
        accountGridView.getChildren().removeAll(isPaymentOptionInputs);
        accountGridView.getChildren().removeAll(isInsertOptionInputs);

        accountNumberInputs.clear();
        accountNameInputs.clear();
        isPaymentOptionInputs.clear();
        isInsertOptionInputs.clear();
        shownAccounts.clear();

        int i = 0;
        for (Account account : kernel.getAccounts()) {
            accountNumberInputs.add(new TextField());
            accountNumberInputs.get(i).setText(account.getNumber() + "");
            accountGridView.add(accountNumberInputs.get(i), 0, i + 1);

            accountNameInputs.add(new TextField());
            accountNameInputs.get(i).setText(account.getName() + "");
            accountGridView.add(accountNameInputs.get(i), 1, i + 1);

            isPaymentOptionInputs.add(new CheckBox());
            isPaymentOptionInputs.get(i).setSelected(account.isPaymentOption());
            accountGridView.add(isPaymentOptionInputs.get(i), 2, i + 1);

            isInsertOptionInputs.add(new CheckBox());
            isInsertOptionInputs.get(i).setSelected(account.isInsertOption());
            accountGridView.add(isInsertOptionInputs.get(i), 3, i + 1);

            shownAccounts.add(account);

            i++;
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        accountNumberInputs = new ArrayList<>();
        accountNameInputs = new ArrayList<>();
        isPaymentOptionInputs = new ArrayList<>();
        isInsertOptionInputs = new ArrayList<>();

        shownAccounts = new ArrayList<>();

        redrawGridView();
    }
}
