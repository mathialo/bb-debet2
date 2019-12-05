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

import bbdebet2.gui.customelements.WaitingDialog;
import bbdebet2.kernel.Kernel;
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
import javafx.scene.control.CheckBox;
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
        INACTIVE("Inaktive nåværende periode"),
        POSITIVE("Positiv balanse"),
        NEGATIVE("Negativ balanse"),
        LESS_THAN_100("Minde enn 100 kr");

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

        pane.add(new Label("Velg regler"), 0, 0);

        CheckBox[] checkBoxes = new CheckBox[UserSelectionRule.values().length];
        int i = 0;
        for (i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox(UserSelectionRule.values()[i].toString());
            pane.add(checkBoxes[i], 1, i);
        }

        Button button = new Button("Legg til");
        button.setOnAction(e -> {
            List<UserSelectionRule> selectionRules = new LinkedList<>();
            for (int j = 0; j < checkBoxes.length; j++)
                if (checkBoxes[j].isSelected())
                    selectionRules.add(UserSelectionRule.values()[j]);
            handleAddUsers(selectionRules);
            dialog.close();
        });
        pane.add(button, 1, i);

        dialog.setScene(scene);
        dialog.setTitle("Velg brukere");
        dialog.show();
    }


    private void handleAddUsers(List<UserSelectionRule> selectionRules) {
        if (selectionRules.isEmpty()) return;

        if (!userNamesInput.getText().replaceAll("\\s+", "").equals("")) {
            userNamesInput.setText(userNamesInput.getText() + ", ");
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (User u : kernel.getUserList()) {
            boolean addThisUser = true;

            selectionSearch:
            for (UserSelectionRule selectionRule : selectionRules) {
                switch (selectionRule) {
                    case ALL:
                        break;

                    case ACTIVE:
                        if (kernel.getSalesHistory().filterLastSeconds(60 * 60 * 24 * 30 * 2).filterOnUser(u).isEmpty()) {
                            addThisUser = false;
                            break selectionSearch;
                        }
                        break;


                    case INACTIVE:
                        if (!kernel.getSalesHistory().filterOnUser(u).isEmpty()) {
                            addThisUser = false;
                            break selectionSearch;
                        }
                        break;

                    case NEGATIVE:
                        if (!(u.getBalance() < 0)) {
                            addThisUser = false;
                            break selectionSearch;
                        }
                        break;

                    case LESS_THAN_100:
                        if (!(u.getBalance() < 100)) {
                            addThisUser = false;
                            break selectionSearch;
                        }
                        break;

                    case POSITIVE:
                        if (!(u.getBalance() > 0)) {
                            addThisUser = false;
                            break selectionSearch;
                        }
                        break;
                }
            }

            if (addThisUser) {
                if (!first) stringBuilder.append(", ");
                stringBuilder.append(u.getUserName());
                first = false;
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
                        Kernel.getLogger().log(e);
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
