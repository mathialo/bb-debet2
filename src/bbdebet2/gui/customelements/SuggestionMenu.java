/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import bbdebet2.kernel.datastructs.IsListable;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class SuggestionMenu<T> extends ContextMenu {

    private TextField inputField;
    private IsListable<T> backend;


    public SuggestionMenu(TextField inputField, IsListable<T> backend) {
        this.inputField = inputField;
        this.backend = backend;

        inputField.setOnKeyPressed(this::updateContextMenuItems);
    }


    private void updateContextMenuItems(KeyEvent event) {
        if (! (event.getCode().isLetterKey() || event.getCode().isWhitespaceKey() || event.getCode().isDigitKey())) return;

        List<T> list = backend.toList();
        getItems().clear();

        for (T item : list) {
            if (item.toString().toLowerCase().contains(inputField.getText().toLowerCase())) {
                MenuItem menuItem = new MenuItem(item.toString());
                menuItem.setOnAction(e -> inputField.setText(item.toString()));
                getItems().add(menuItem);
            }
        }

        Bounds bounds = inputField.getBoundsInLocal();
        Bounds screenBounds = inputField.localToScreen(bounds);
        double x = screenBounds.getMinX();
        double y = screenBounds.getMinY() + inputField.getHeight();

        show(inputField, x, y);
    }
}
