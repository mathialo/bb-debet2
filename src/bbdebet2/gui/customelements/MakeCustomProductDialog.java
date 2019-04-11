/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import bbdebet2.kernel.datastructs.Product;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MakeCustomProductDialog extends Dialog<Product> {
    private TextField titleInput;
    private TextField priceInput;

    public MakeCustomProductDialog() {
        setTitle("Legg til egendefinert produkt");
        setHeaderText("Spesifiser navn og pris:");

        ButtonType productButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(productButtonType, ButtonType.CANCEL);

        titleInput = new TextField();
        titleInput.setPromptText("Navn");
        priceInput = new TextField();
        priceInput.setPromptText("Pris");

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(titleInput, priceInput);
        vBox.setPadding(new Insets(20));

        getDialogPane().setContent(vBox);

        Platform.runLater(() -> titleInput.requestFocus());

        setResultConverter(dialogButton -> {
            if (dialogButton == productButtonType) {
                double price = Double.parseDouble(priceInput.getText());

                if (price < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Du kan selvfølgelig ikke kjøpe noe til en negativ pris. Tulling.");
                    alert.getDialogPane().setPrefHeight(200);
                    alert.setHeaderText("Din slask");
                    alert.showAndWait();
                    return null;
                }
                return new Product(
                    titleInput.getText(),
                    Double.parseDouble(priceInput.getText()),
                    0,
                    true
                );
            }
            return null;
        });
    }
}
