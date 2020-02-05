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

import com.mathiaslohne.bbdebet2.kernel.mailing.TextTemplate;
import com.mathiaslohne.bbdebet2.kernel.mailing.TextTemplateLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EulaConfirmation extends Alert {

    public EulaConfirmation() {
        super(AlertType.CONFIRMATION);

        setTitle("Bekreft EULA");
        setHeaderText("Administratoren ber deg godta følgende før bruk");

        TextArea textArea = new TextArea(TextTemplateLoader.getTemplate(TextTemplate.EULA));
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        getDialogPane().setPrefHeight(500);


        ((Button) getDialogPane().lookupButton(ButtonType.OK)).setText("Godta");
        ((Button) getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Avbryt");

        getDialogPane().setContent(textArea);
    }

}
