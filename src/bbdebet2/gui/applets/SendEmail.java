/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class SendEmail extends Applet {

    @FXML
    private TextField userNamesInput;
    @FXML
    private TextField emailSubjectInput;
    @FXML
    private TextArea emailTextInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Send epost", "SendEmailScreen");
    }


    @FXML
    public void sendEmailsAndExit(ActionEvent event) {
        String[] usernames = userNamesInput.getText().split("\\s*,\\s*");

        List<String> failedNames = new LinkedList<>();

        Alert sendingAlert = new Alert(Alert.AlertType.NONE, "Sender eposter...");
        sendingAlert.show();

        Task<Void> sendEmailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (String username : usernames) {
                    User user = kernel.getUserList().find(username);
                    if (user == null) {
                        failedNames.add(username);
                        continue;
                    }

                    try {
                        kernel.getEmailSender().sendMail(user, emailSubjectInput.getText(), emailTextInput.getText());
                    } catch (MessagingException | InvalidEncryptionException e) {
                        kernel.getLogger().log(e);
                        failedNames.add(username);
                    }
                }
                return null;
            }
        };

        sendEmailTask.setOnSucceeded(e -> {
            sendingAlert.close();
            if (!failedNames.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Kunne ikke sende til " + failedNames.toString());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.NONE, "Eposter sendt!");
                alert.showAndWait();
                exit(event);
            }

        });

        new Thread(sendEmailTask).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
}
