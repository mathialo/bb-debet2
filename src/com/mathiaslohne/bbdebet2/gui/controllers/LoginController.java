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

package com.mathiaslohne.bbdebet2.gui.controllers;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.gui.customelements.PasswordDialog;
import com.mathiaslohne.bbdebet2.gui.customelements.SuggestionMenu;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewUser;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class LoginController implements Initializable {

    @FXML
    private TextField loginNameField;
    @FXML
    private Label errorOutput;

    private Kernel kernel;

    private SuggestionMenu<ViewUser> suggestionMenu;


    @FXML
    public void attemptLogin(ActionEvent event) {
        suggestionMenu.setActive(false);

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
        suggestionMenu.setActive(false);
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
                Kernel.getLogger().log("New admin login");
                Main.toAdminScreen();
            } else {
                Kernel.getLogger().log("Failed admin login");
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
        Main.getLoginScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.BACK_SPACE || loginNameField.getText().isBlank()) {
                suggestionMenu.setActive(false);

            } else if (ke.getCode() == KeyCode.TAB) {
                List<User> users = StreamSupport.stream(kernel.getUserList().spliterator(), false)
                    .filter(user -> user.getUserName().startsWith(loginNameField.getText()))
                    .collect(Collectors.toList());

                if (users.size() == 1) {
                    loginNameField.setText(users.get(0).getUserName());
                    loginNameField.positionCaret(users.get(0).getUserName().length());
                } else {
                    suggestionMenu.setActive(true);
                }
            } else if (ke.getCode().isLetterKey()) {
                suggestionMenu.updateContextMenuItems(ke);
            }


        });

        suggestionMenu = new SuggestionMenu<>(loginNameField, kernel.getUserList())
            .setActive(false)
            .setMatchType(SuggestionMenu.MatchType.STARTS_WITH);

        Platform.runLater(() -> loginNameField.requestFocus());
    }
}
