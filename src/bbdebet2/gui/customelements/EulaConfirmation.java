/*
 * Copyright (c) 2019. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import bbdebet2.kernel.mailing.TextTemplate;
import bbdebet2.kernel.mailing.TextTemplateLoader;
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
