/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;

public class NewUser extends Applet {

    @FXML
    private TextField userNameInput;
    @FXML
    private TextField userEmailInput;
    @FXML
    private TextArea welcomeEmailTextInput;


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

        User newUser = new User(userNameInput.getText(), userEmailInput.getText());

        Main.getKernel().getUserList().add(newUser);

        try {
            Main.getKernel().getEmailSender().sendMail(
                newUser,
                "Velkommen til debetboka!",
                welcomeEmailTextInput.getText()
            );
        } catch (InvalidEncryptionException | MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Kunne ikke sende epost");
            alert.showAndWait();
        }

        Main.getCurrentAdminController().repaintUserList();
        exit(event);
    }
}
