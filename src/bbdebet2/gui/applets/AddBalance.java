/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.EmailTemplate;
import bbdebet2.kernel.mailing.EmailTemplateLoader;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddBalance extends Applet {

    @FXML
    private TextField userNameInput;
    @FXML
    private TextField moneyInput;
    @FXML
    private CheckBox sendEmailInput;
    @FXML
    private TextArea emailTextInput;

    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Legg til penger", "AddBalanceScreen");
    }


    @FXML
    public void addMoney(ActionEvent event) {
        User u = kernel.getUserList().find(userNameInput.getText());

        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Finner ikke bruker");
            alert.showAndWait();
            return;
        }

        double amount = 0;

        try {
            amount = Double.parseDouble(moneyInput.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Skriv gyldig tall i beløpfeltet");
            alert.showAndWait();
            return;
        }

        u.addBalance(amount);

        if (sendEmailInput.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sender epost...");
            alert.show();
            Task sendEmailTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        kernel.getEmailSender().sendMail(u, "Påfyll av penger", emailTextInput.getText());
                    } catch (MessagingException | InvalidEncryptionException e) {
                        kernel.getLogger().log(e);
                    }
                    return null;
                }
            };

            sendEmailTask.setOnSucceeded(e -> alert.close());
            new Thread(sendEmailTask).start();
        }

        Main.getCurrentAdminController().repaintUserList();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        emailTextInput.setText(EmailTemplateLoader.getTemplate(EmailTemplate.USERADDEDMONEY));
    }
}
