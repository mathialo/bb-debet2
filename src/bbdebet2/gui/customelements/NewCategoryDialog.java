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
import bbdebet2.kernel.datastructs.Product;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class NewCategoryDialog extends Dialog<CategoryDict.Category> {

    private TextField titleInput;
    private ColorPicker colorInput;


    public NewCategoryDialog() {
        setTitle("Ny kategori");
        setHeaderText("Spesifiser navn og farge:");

        ButtonType addCategoryButton = new ButtonType("Legg til", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addCategoryButton, ButtonType.CANCEL);

        titleInput = new TextField();
        titleInput.setPromptText("Navn");
        colorInput = new ColorPicker();
        colorInput.setPrefWidth(200);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(titleInput, colorInput);
        vBox.setPadding(new Insets(20));

        getDialogPane().setContent(vBox);
        getDialogPane().setMinSize(200, 150);

        setResultConverter(dialogButton -> {
            if (dialogButton == addCategoryButton) {
                if (Main.getKernel().getCategories().contains(titleInput.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Kategorien '" + titleInput.getText() + "' eksisterer allerede");
                    alert.setHeaderText("Gi kategorien et unikt navn");
                    alert.showAndWait();
                    return null;
                }

                return Main.getKernel().getCategories().newCategory(
                    titleInput.getText(),
                    colorInput.getValue()
                );
            }
            return null;
        });
    }
}
