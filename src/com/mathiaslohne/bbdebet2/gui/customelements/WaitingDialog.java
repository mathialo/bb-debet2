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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class WaitingDialog extends Alert {
    public WaitingDialog(String message) {
        super(AlertType.INFORMATION);
        setTitle("Jobber...");
        setHeaderText(message);

        StackPane pane = new StackPane();
        pane.getChildren().add(new ProgressIndicator());
        pane.setPadding(new Insets(20));
        getDialogPane().lookupButton(ButtonType.OK).setVisible(false);

        getDialogPane().setPrefWidth(350);
        getDialogPane().setPrefHeight(200);
        getDialogPane().setContent(pane);
    }

    public void updateMessage(String message) {
        setHeaderText(message);
    }
}
