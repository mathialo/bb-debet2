/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.TextTemplate;
import bbdebet2.kernel.mailing.TextTemplateLoader;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

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
        if (kernel.getUserList().contains(userNameInput.getText())) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR, "Det finnes allerede en bruker med det navnet!");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        User newUser = new User(userNameInput.getText(), userEmailInput.getText());

        kernel.getUserList().add(newUser);

        try {
            kernel.getEmailSender().sendMail(
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        welcomeEmailTextInput.setText(TextTemplateLoader.getTemplate(TextTemplate.WELCOME));
    }
}
