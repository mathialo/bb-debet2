/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.gui.modelwrappers.ViewUser;
import bbdebet2.kernel.Kernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewUserTransaction implements Initializable {

    private Kernel kernel;

    @FXML
    private TextField transferAmountInput;
    @FXML
    private ComboBox<ViewUser> userSelectionInput;


    public static void createAndDisplayDialog() {
        try {
            Stage stage = new Stage();

            Parent root = FXMLLoader.load(NewUserTransaction.class.getClassLoader().getResource("bbdebet2/gui/views/NewUserTransactionScreen.fxml"));

            Scene scene = new Scene(root);

            stage.setTitle("Overfør beløp");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Main.getKernel().getLogger().log(e);
        }
    }


    @FXML
    public void handleTransferMoney(ActionEvent event) {
        try {
            kernel.getTransactionHandler().newUserTransaction(
                Main.getActiveUser(),
                userSelectionInput.getSelectionModel().getSelectedItem().getUserObject(),
                Double.parseDouble(transferAmountInput.getText())
            );

            Main.getCurrentUserController().repaintGui();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Velg en bruker!");
            alert.setHeaderText(null);
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Skriv inn et gyldig pengebeløp!");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.kernel = Main.getKernel();

        userSelectionInput.setItems(kernel.getUserList().toObservableList());
    }
}
