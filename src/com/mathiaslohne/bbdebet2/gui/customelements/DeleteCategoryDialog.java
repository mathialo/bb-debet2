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

package com.mathiaslohne.bbdebet2.gui.customelements;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.kernel.core.CategoryDict;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.util.Optional;


public class DeleteCategoryDialog extends Dialog<Void> {

    public DeleteCategoryDialog() {
        setTitle("Slett kategori");
        setHeaderText("Velg kategori");

        ButtonType addCategoryButton = new ButtonType("Endre", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addCategoryButton, ButtonType.CANCEL);

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
                if (categoryChoiceBox.getSelectionModel().isEmpty()) return null;

                Alert question = new Alert(Alert.AlertType.CONFIRMATION, "Dette vil slette alle regler for valgt kategori");
                question.setHeaderText("Er du sikker?");
                Optional<ButtonType> answer =  question.showAndWait();

                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    Main.getKernel().getCategories().deleteCategory(categoryChoiceBox.getSelectionModel().getSelectedItem());
                }

                return null;
            }
            return null;
        });
    }
}
