/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.customelements.WaitingDialog;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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


    private enum UserSelectionRule {
        ALL("Alle brukere"),
        ACTIVE("Aktive siste 2 mnd"),
        POSITIVE("Positiv balanse"),
        NEGATIVE("Negativ balanse");

        private String showName;


        UserSelectionRule(String showName) {
            this.showName = showName;
        }


        @Override
        public String toString() {
            return showName;
        }
    }


    @FXML
    public void openAddUsersDialog(ActionEvent event) {
        Stage dialog = new Stage();
        GridPane pane = new GridPane();
        pane.setHgap(20);
        pane.setVgap(20);
        pane.setPadding(new Insets(20));

        Scene scene = new Scene(pane);

        pane.add(new Label("Velg regel:"), 0, 0);

        ComboBox<UserSelectionRule> selectionRuleComboBox = new ComboBox<>();
        selectionRuleComboBox.getItems().addAll(UserSelectionRule.values());
        pane.add(selectionRuleComboBox, 1, 0);

        Button button = new Button("Legg til");
        button.setOnAction(e -> {
            handleAddUsers(selectionRuleComboBox.getSelectionModel().getSelectedItem());
            dialog.close();
        });
        pane.add(button, 1, 1);

        dialog.setScene(scene);
        dialog.setTitle("Velg brukere");
        dialog.show();
    }


    private void handleAddUsers(UserSelectionRule selectionRule) {
        if (selectionRule == null) return;

        // Add trailing comma if to-field is non-empty
        System.out.println("'" + userNamesInput.getText().replaceAll("\\s+", "") + "'");

        if (! userNamesInput.getText().replaceAll("\\s+", "").equals("")) {
            userNamesInput.setText(userNamesInput.getText() + ", ");
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (User u : kernel.getUserList()) {
            switch (selectionRule) {
                case ALL:
                    if (!first) stringBuilder.append(", ");
                    stringBuilder.append(u.getUserName());
                    first = false;
                    break;

                case ACTIVE:
                    if (!kernel.getSalesHistory().filterLast(60 * 60 * 24 * 30 * 2).filterOnUser(
                        u).isEmpty()) {
                        if (!first) stringBuilder.append(", ");
                        stringBuilder.append(u.getUserName());
                        first = false;
                    }
                    break;

                case NEGATIVE:
                    if (u.getBalance() < 0) {
                        if (!first) stringBuilder.append(", ");
                        stringBuilder.append(u.getUserName());
                        first = false;
                    }
                    break;

                case POSITIVE:
                    if (u.getBalance() > 0) {
                        if (!first) stringBuilder.append(", ");
                        stringBuilder.append(u.getUserName());
                        first = false;
                    }
                    break;
            }
        }

        userNamesInput.setText(userNamesInput.getText() + stringBuilder.toString());
    }


    @FXML
    public void sendEmailsAndExit(ActionEvent event) {
        String[] usernames = userNamesInput.getText().split("\\s*,\\s*");

        List<String> failedNames = new LinkedList<>();

        Alert sendingAlert = new WaitingDialog("Sender eposter...");
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
                        kernel.getEmailSender().sendMail(
                            user, emailSubjectInput.getText(), emailTextInput.getText());
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
            Platform.runLater(() -> {

                if (!failedNames.isEmpty()) {
                    Alert alert = new Alert(
                        Alert.AlertType.ERROR, "Kunne ikke sende til " + failedNames.toString());
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Eposter sendt!");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    exit(event);
                }
            });
        });

        new Thread(sendEmailTask).start();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
}
