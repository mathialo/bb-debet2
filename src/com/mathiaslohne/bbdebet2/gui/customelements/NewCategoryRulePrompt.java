/*
 * Copyright (C) 2021  Mathias Lohne
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

package com.mathiaslohne.bbdebet2.gui.customelements;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.kernel.core.CategoryDict;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;


public class NewCategoryRulePrompt extends Dialog<CategoryDict.Category> {
    public NewCategoryRulePrompt() {
        setTitle("Spesifiser kategori");
        setHeaderText("Produktet ser ut til å være nytt, vil du legge de til i en kategori?");

        ButtonType addCategoryButton = new ButtonType("Legg til", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hopp over", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(addCategoryButton, cancelButton);

        ChoiceBox<CategoryDict.Category> categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.getItems().setAll(Main.getKernel().getCategories().getCategories());
        categoryChoiceBox.setPrefWidth(200);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(categoryChoiceBox);
        vBox.setPadding(new Insets(20));

        getDialogPane().setContent(vBox);
        getDialogPane().setMinSize(200, 150);

        setResultConverter(dialogButton -> {
            if (dialogButton == addCategoryButton) {
                return categoryChoiceBox.getSelectionModel().getSelectedItem();
            }

            return null;
        });
    }
}
