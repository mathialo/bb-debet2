/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    public TextField loginNameField;

    private Kernel kernel;


    @FXML
    public void attemptLogin(ActionEvent event) {
        // Extract username
        String userName = loginNameField.getText();

        // Try to find user
        User user = kernel.getUserList().find(userName);

        if (user == null) {
            kernel.getLogger().log("New failed login attemppt from '" + userName + "'");
        } else {
            kernel.getLogger().log("New login from " + user);
            Main.getCurrentUserController().login(user);
            Main.toUserScreen();
        }
    }


    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }


    @FXML
    public void attemptToAdminScreen(ActionEvent event) {
        Main.toAdminScreen();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentLoginController(this);
    }
}
