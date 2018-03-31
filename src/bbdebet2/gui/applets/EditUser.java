/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.gui.customelements.SuggestionMenu;
import bbdebet2.gui.modelwrappers.ViewUser;
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
        User u = kernel.getUserList().find(userNameInput.getText());

        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fant ikke bruker");
            alert.showAndWait();
            return;
        }

        if (!newUserNameInput.getText().equals(u.getUserName())) {
            if (kernel.getUserList().contains(newUserNameInput.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Brukernavn allerede tatt");
                alert.showAndWait();
                return;
            } else {
                u.setUserName(newUserNameInput.getText());
            }
        }

        u.setMail(newEmailInput.getText());

        Main.getCurrentAdminController().repaintUserList();
        exit(event);
    }


    private void updateCurrentContent(KeyEvent event) {
        User u = kernel.getUserList().find(userNameInput.getText());

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
