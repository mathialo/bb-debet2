/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class NewUser extends Applet {

    @FXML
    private TextField userNameInput;
    @FXML
    private TextField userEmailInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Ny bruker", "NewUserScreen");
    }


    @FXML
    public void addUserAndExit(ActionEvent event) {
        // Check validity
        if (Main.getKernel().getUserList().contains(userNameInput.getText())) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR, "Det finnes allerede en bruker med det navnet!");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        Main.getKernel().getUserList().add(
            new User(
                userNameInput.getText(),
                userEmailInput.getText()
            )
        );

        Main.getCurrentAdminController().repaintUserList();
        exit(event);
    }
}
