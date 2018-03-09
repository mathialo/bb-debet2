/*
 * Copyright (c) 2018. Mathias Lohne
 */

package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import kernel.Kernel;
import kernel.datastructs.User;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private Label loginNameView;
    @FXML private FlowPane favouritesContainer;
    @FXML private FlowPane storageContainer;



    private Kernel kernel;


    private static String formatTitleString(User user) {
        return String.format("Logget inn som %s. Saldo: %s.", user.getUserName(), user.getFormatedBalance());
    }


    private void updateFavourites() {

    }


    private void updateStorage() {

    }


    public void login(User user) {
        // Set active user
        Main.setActiveUser(user);

        // Update and populate GUI
        loginNameView.setText(formatTitleString(user));
        updateFavourites();
        updateStorage();

        // Change scene to UserScreen
        Main.toUserScreen();
    }


    public void logout(User user) {
        // Set active user
        Main.setActiveUser(null);

        // Change scene to UserScreen
        Main.toLoginScreen();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentUserController(this);
    }
}
