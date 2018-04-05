/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
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
        getButtonTypes().clear();

        getDialogPane().setPrefWidth(350);
        getDialogPane().setContent(pane);
    }

    public void updateMessage(String message) {
        setHeaderText(message);
    }
}
