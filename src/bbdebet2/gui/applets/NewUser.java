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

        User newUser = new User(
            userNameInput.getText().replaceAll("\\s+", "").toLowerCase(),
            userEmailInput.getText().replaceAll("\\s+", "").toLowerCase()
        );

        kernel.getUserList().add(newUser);
        kernel.getLogger().log("New user added: " + newUser);

        try {
            kernel.getEmailSender().sendMail(
                newUser,
                "Velkommen til debetboka!",
                welcomeEmailTextInput.getText()
            );
        } catch (InvalidEncryptionException | MessagingException e) {
            kernel.getLogger().log(e);
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
