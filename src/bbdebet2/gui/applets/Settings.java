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

import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.SettingsHolder;
import bbdebet2.kernel.mailing.TextTemplate;
import bbdebet2.kernel.mailing.TextTemplateLoader;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Settings extends Applet {

    @FXML
    private TextField numOfFavouritesInput;
    @FXML
    private TextField maxInactiveTimeInput;
    @FXML
    private PasswordField adminPassInput;
    @FXML
    private ChoiceBox<SettingsHolder.SortingOrder> sortingOrderInput;
    @FXML
    private CheckBox requireEulaInput;

    @FXML
    private TextField autoSaveIntervalInput;
    @FXML
    private TextField backupIntervalInput;

    @FXML
    private TextField emailAddrInput;
    @FXML
    private TextField emailUserInput;
    @FXML
    private TextField emailServerInput;
    @FXML
    private TextField emailPortInput;
    @FXML
    private ChoiceBox<String> emailEncryptionInput;
    @FXML
    private PasswordField emailPassInput;
    @FXML
    private TextField emailReplyToInput;

    @FXML
    private CheckBox autoNagUserInput;
    @FXML
    private CheckBox sendShoppingListInput;
    @FXML
    private CheckBox sendReportsInput;

    @FXML
    private ChoiceBox<TextTemplate> templateChooser;
    @FXML
    private TextArea templateInput;
    private TextTemplate selectedTemplate;

    @FXML
    private TextField accountNumberInput;


    @FXML
    private CheckBox activateGlasUserInput;
    @FXML
    private TextField glasUserNameInput;
    @FXML
    private TextField glasUserRoundToInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Innstillinger", "SettingsView");
    }


    private void setupChoiceBoxes() {
        ArrayList<String> encryptions = new ArrayList<>();
        encryptions.add("SSL");
        encryptions.add("STARTTLS");
        emailEncryptionInput.setItems(FXCollections.observableArrayList(encryptions));

        templateChooser.setItems(FXCollections.observableArrayList(TextTemplate.values()));
        templateChooser.setOnAction(this::updateSelectedTemplate);

        sortingOrderInput.setItems(FXCollections.observableArrayList(SettingsHolder.SortingOrder.values()));
    }


    private void resetSettingsFromKernel() {
        numOfFavouritesInput.setText(kernel.getSettingsHolder().getNumOfFavourites() + "");
        maxInactiveTimeInput.setText(kernel.getSettingsHolder().getMaxInactiveTime() + "");
        adminPassInput.setText(kernel.getSettingsHolder().getAdminPass());
        sortingOrderInput.getSelectionModel().select(kernel.getSettingsHolder().getSortingOrder());
        requireEulaInput.setSelected(kernel.getSettingsHolder().isRequireEula());

        autoSaveIntervalInput.setText(kernel.getSettingsHolder().getAutoSaveInterval() + "");
        backupIntervalInput.setText(kernel.getSettingsHolder().getBackupInterval() + "");

        emailAddrInput.setText(kernel.getSettingsHolder().getEmailAddr());
        emailUserInput.setText(kernel.getSettingsHolder().getEmailUser());
        emailServerInput.setText(kernel.getSettingsHolder().getEmailServer());
        emailPortInput.setText(kernel.getSettingsHolder().getEmailPort());
        emailEncryptionInput.getSelectionModel().select(
            kernel.getSettingsHolder().getEmailEncryption());
        emailPassInput.setText(kernel.getSettingsHolder().getEmailPass());
        emailReplyToInput.setText(kernel.getSettingsHolder().getEmailReplyTo());

        accountNumberInput.setText(kernel.getSettingsHolder().getAccountNumber());

        autoNagUserInput.selectedProperty().set(kernel.getSettingsHolder().isAutoNagUser());
        sendShoppingListInput.selectedProperty().set(
            kernel.getSettingsHolder().isSendShoppingList());
        sendReportsInput.selectedProperty().set(kernel.getSettingsHolder().isSendReports());

        activateGlasUserInput.setSelected(kernel.getSettingsHolder().isGlasUserActive());
        glasUserNameInput.setText(kernel.getSettingsHolder().getGlasUserName());
        glasUserRoundToInput.setText(kernel.getSettingsHolder().getGlasUserRoundTo() + "");
    }


    private void saveCurrentToFile() throws Exception {
        kernel.getSettingsHolder().setNumOfFavourites(
            Integer.parseInt(numOfFavouritesInput.getText()));
        kernel.getSettingsHolder().setMaxInactiveTime(
            Integer.parseInt(maxInactiveTimeInput.getText()));
        kernel.getSettingsHolder().setAdminPass(adminPassInput.getText());
        kernel.getSettingsHolder().setSortingOrder(sortingOrderInput.getSelectionModel().getSelectedItem());
        kernel.getSettingsHolder().setRequireEula(requireEulaInput.isSelected());

        kernel.getSettingsHolder().setAutoSaveInterval(Integer.parseInt(autoSaveIntervalInput.getText()));
        kernel.getSettingsHolder().setBackupInterval(Integer.parseInt(backupIntervalInput.getText()));

        kernel.getSettingsHolder().setEmailAddr(emailAddrInput.getText());
        kernel.getSettingsHolder().setEmailUser(emailUserInput.getText());
        kernel.getSettingsHolder().setEmailServer(emailServerInput.getText());
        kernel.getSettingsHolder().setEmailPort(emailPortInput.getText());
        kernel.getSettingsHolder().setEmailEncryption(
            emailEncryptionInput.getSelectionModel().getSelectedItem());
        kernel.getSettingsHolder().setEmailPass(emailPassInput.getText());
        kernel.getSettingsHolder().setEmailReplyTo(emailReplyToInput.getText());

        kernel.getSettingsHolder().setAccountNumber(accountNumberInput.getText());

        kernel.getSettingsHolder().setAutoNagUser(autoNagUserInput.isSelected());
        kernel.getSettingsHolder().setSendShoppingList(sendShoppingListInput.isSelected());
        kernel.getSettingsHolder().setSendReports(sendReportsInput.isSelected());

        kernel.getSettingsHolder().setGlasUserActive(activateGlasUserInput.isSelected());
        kernel.getSettingsHolder().setGlasUserName(glasUserNameInput.getText());
        kernel.getSettingsHolder().setGlasUserRoundTo(Integer.parseInt(glasUserRoundToInput.getText()));

        saveSelectedTemplate();
    }


    private void saveSelectedTemplate() throws IOException {
        if (selectedTemplate == null) return;

        TextTemplateLoader.saveTemplate(selectedTemplate, templateInput.getText());
    }


    @FXML
    private void updateSelectedTemplate(ActionEvent event) {
        try {
            saveSelectedTemplate();
        } catch (IOException e) {
            Kernel.getLogger().log(e);

            Alert alert = new Alert(Alert.AlertType.ERROR, "Kunne ikke lagre mal");
            alert.setHeaderText(null);
            alert.showAndWait();
        }

        selectedTemplate = templateChooser.getSelectionModel().getSelectedItem();
        templateInput.setText(TextTemplateLoader.getTemplate(selectedTemplate));
    }


    @FXML
    private void handleSaveButton(ActionEvent event) {
        try {
            saveCurrentToFile();
            saveSelectedTemplate();
            exit(event);
        } catch (IOException e) {
            Kernel.getLogger().log(e);

            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Feil i lagring");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Kernel.getLogger().log(e);

            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Feil i type-konvertering");
            alert.showAndWait();
        } catch (Exception e) {
            Kernel.getLogger().log(e);

            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Uventet feil");
            alert.showAndWait();
        }
    }


    @FXML
    private void handleExitButton(ActionEvent event) {
        exit(event);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setupChoiceBoxes();
        resetSettingsFromKernel();
    }
}

