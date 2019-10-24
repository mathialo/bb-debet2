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

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.gui.customelements.PasswordDialog;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField loginNameField;
    @FXML
    private Label errorOutput;

    private Kernel kernel;


    @FXML
    public void attemptLogin(ActionEvent event) {
        // Extract username
        String userName = loginNameField.getText().trim();

        // Check for glass user
        if (kernel.getSettingsHolder().isGlasUserActive() && userName.equalsIgnoreCase(kernel.getSettingsHolder().getGlasUserName())) {
            Kernel.getLogger().log("New login from glas user");
            Main.getCurrentUserController().loginGlass();
            Main.toUserScreen();
            return;
        }

        // Try to find user
        User user = kernel.getUserList().find(userName);

        if (user == null) {
            Kernel.getLogger().log("New failed login attemppt from '" + userName + "'");
            errorOutput.setText("Finner ikke bruker '" + userName + "'");
        } else {
            if (Main.getCurrentUserController().login(user)) {
                Kernel.getLogger().log("New login from " + user);
                Main.toUserScreen();
            } else {
                Kernel.getLogger().log("Failed login attempt from " + user + ". User did not accept EULA.");
            }
        }
    }


    public void resetLogin() {
        loginNameField.setText("");
        errorOutput.setText("");
    }


    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }


    @FXML
    public void attemptToAdminScreen(ActionEvent event) {
        PasswordDialog passwordDialog = new PasswordDialog();
        Optional<String> result = passwordDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get().equals(kernel.getSettingsHolder().getAdminPass())) {
                Main.toAdminScreen();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Prøv på nytt");
                alert.setHeaderText("Feil passord");
                alert.showAndWait();
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentLoginController(this);
    }


    public void postInitialize() {

    }
}
