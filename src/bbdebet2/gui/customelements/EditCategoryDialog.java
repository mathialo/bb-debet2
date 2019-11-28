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

package bbdebet2.gui.customelements;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.CategoryDict;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class EditCategoryDialog extends Dialog<Void> {

    private TextField titleInput;
    private ColorPicker colorInput;


    public EditCategoryDialog() {
        setTitle("Endre kategori");
        setHeaderText("Spesifiser nytt navn og farge:");

        ButtonType addCategoryButton = new ButtonType("Endre", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addCategoryButton, ButtonType.CANCEL);

        ChoiceBox<CategoryDict.Category> categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.getItems().setAll(Main.getKernel().getCategories().getCategories());
        categoryChoiceBox.setPrefWidth(200);

        Label spacer = new Label(" ");


        titleInput = new TextField();
        titleInput.setPromptText("Nytt navn");
        colorInput = new ColorPicker();
        colorInput.setPrefWidth(200);

        categoryChoiceBox.setOnAction(e -> {
            if (!categoryChoiceBox.getSelectionModel().isEmpty()) {
                CategoryDict.Category selected = categoryChoiceBox.getSelectionModel().getSelectedItem();
                titleInput.setText(selected.getName());
                colorInput.setValue(selected.getColor());
            }
        });

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(categoryChoiceBox, spacer, titleInput, colorInput);
        vBox.setPadding(new Insets(20));

        getDialogPane().setContent(vBox);
        getDialogPane().setMinSize(200, 150);

        setResultConverter(dialogButton -> {
            if (dialogButton == addCategoryButton) {
                if (categoryChoiceBox.getSelectionModel().isEmpty()) return null;

                if (Main.getKernel().getCategories().contains(titleInput.getText())
                    && !categoryChoiceBox.getSelectionModel().getSelectedItem().getName().equals(titleInput.getText().replaceAll(",", "."))) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Kategorien '" + titleInput.getText().replaceAll(",", ".") + "' eksisterer allerede");
                    alert.setHeaderText("Gi kategorien et unikt navn");
                    alert.showAndWait();
                    return null;
                }

                Main.getKernel().getCategories().updateCategory(
                    categoryChoiceBox.getSelectionModel().getSelectedItem().getName(),
                    titleInput.getText(),
                    colorInput.getValue()
                );

                return null;
            }
            return null;
        });
    }
}
