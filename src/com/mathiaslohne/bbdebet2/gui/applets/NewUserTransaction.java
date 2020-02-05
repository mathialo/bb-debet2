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
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NewUserTransaction extends Applet implements Initializable {

    @FXML
    private TextField transferAmountInput;
    @FXML
    private ComboBox<ViewUser> userSelectionInput;

    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Overfør beløp", "NewUserTransactionScreen");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        userSelectionInput.setItems(kernel.getUserList().toObservableList());
    }


    @FXML
    public void handleTransferMoney(ActionEvent event) {
        try {
            kernel.getTransactionHandler().newUserTransaction(
                Main.getActiveUser(),
                userSelectionInput.getSelectionModel().getSelectedItem().getUserObject(),
                Double.parseDouble(transferAmountInput.getText())
            );

            Main.getCurrentUserController().repaintGui();
            exit(event);
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Velg en bruker!");
            alert.setHeaderText(null);
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Skriv inn et gyldig pengebeløp!");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }
}
