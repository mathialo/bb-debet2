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

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class DeleteAccountDialog extends Dialog<Integer> {
    public DeleteAccountDialog() {
        setTitle("Slett konto");
        setHeaderText("Skriv inn nummer");

        ButtonType addCategoryButton = new ButtonType("Slett", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addCategoryButton, ButtonType.CANCEL);

        TextField numberInput = new TextField();
        numberInput.setPromptText("Nummer");

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(numberInput);
        vBox.setPadding(new Insets(20));

        getDialogPane().setContent(vBox);
        getDialogPane().setMinSize(160, 150);

        setResultConverter(buttonType -> Integer.parseInt(numberInput.getText()));
    }
}
