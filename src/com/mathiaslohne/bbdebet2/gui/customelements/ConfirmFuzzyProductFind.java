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

import com.mathiaslohne.bbdebet2.kernel.core.Product;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;


public class ConfirmFuzzyProductFind extends Dialog<String> {

    public ConfirmFuzzyProductFind(String original, Set<String> finds) {
        setTitle("Bekreft navn");
        if (finds.isEmpty()) {
            setResult(original);
            Platform.runLater(this::close);
        }
        else if (finds.size() == 1) {
            setUpSingleConfirm(original, finds.iterator().next());
        } else {
            setUpMultiConfirm(original, finds);
        }
    }


    private void setUpSingleConfirm(String original, String find) {
        setHeaderText("Mente du " + find + "?");
        getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.YES) {
                return find;
            }
            if (dialogButton == ButtonType.NO) {
                return original;
            }
            return null;
        });
    }


    private void setUpMultiConfirm(String original, Set<String> finds) {
        setHeaderText("Mente du en av disse?");

        setWidth(200);

        VBox vBox = new VBox(10);
        for (String find : finds) {
            Button confirm = new Button(find);
            confirm.setPrefWidth(160);
            confirm.setOnAction(event -> {
                setResult(find);
                close();
            });
            vBox.getChildren().add(confirm);
        }
        vBox.setPadding(new Insets(20));

        getDialogPane().setContent(vBox);
        ButtonType newProduct = new ButtonType("Nei, lag ny", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(newProduct);
        setResultConverter(dialogButton -> {
            if (dialogButton == newProduct) {
                return original;
            }
            return null;
        });
    }
}
