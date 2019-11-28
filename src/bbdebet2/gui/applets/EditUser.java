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
import bbdebet2.gui.customelements.SuggestionMenu;
import bbdebet2.gui.modelwrappers.ViewUser;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;


public class EditUser extends Applet {

    @FXML
    private TextField userNameInput;
    @FXML
    private TextField newUserNameInput;
    @FXML
    private TextField newEmailInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Endre bruker", "EditUserScreen");
    }


    @FXML
    public void saveAndExit(ActionEvent event) {
        User u = kernel.getUserList().find(userNameInput.getText().replaceAll(",", "."));

        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fant ikke bruker");
            alert.showAndWait();
            return;
        }

        if (!newUserNameInput.getText().equals(u.getUserName().replaceAll(",", "."))) {
            if (kernel.getUserList().contains(newUserNameInput.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Brukernavn allerede tatt");
                alert.showAndWait();
                return;
            } else {
                Kernel.getLogger().log("Changing name of user: " + u.getUserName() + " -> " + newUserNameInput.getText().replaceAll(",", "."));
                u.setUserName(newUserNameInput.getText().replaceAll(",", "."));
            }
        }

        Main.getCurrentAdminController().repaintUserList();
        exit(event);
    }


    private void updateCurrentContent(KeyEvent event) {
        User u = kernel.getUserList().find(userNameInput.getText().replaceAll(",", "."));

        if (u == null) return;

        newUserNameInput.setText(u.getUserName());
        newEmailInput.setText(u.getMail());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        SuggestionMenu<ViewUser> suggestionMenu = new SuggestionMenu<>(
            userNameInput, kernel.getUserList());
        userNameInput.setOnKeyPressed(event -> {
            suggestionMenu.updateContextMenuItems(event);
            updateCurrentContent(event);
        });
        userNameInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) updateCurrentContent(null);
        });
    }
}
