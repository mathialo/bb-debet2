/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import bbdebet2.kernel.datastructs.Listable;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class SuggestionMenu<T> extends ContextMenu {

    private TextField inputField;
    private List<Listable<T>> backends;
    private HashSet<String> previouslyAdded;


    public SuggestionMenu(TextField inputField, Listable<T> backend) {
        this.inputField = inputField;
        backends = new LinkedList<Listable<T>>();
        backends.add(backend);

        previouslyAdded = new HashSet<>();

        inputField.setOnKeyPressed(this::updateContextMenuItems);
    }


    public SuggestionMenu(TextField inputField, List<Listable<T>> backends) {
        this.inputField = inputField;
        this.backends = backends;

        previouslyAdded = new HashSet<>();

        inputField.setOnKeyPressed(this::updateContextMenuItems);
    }


    public void updateContextMenuItems(KeyEvent event) {
        if (! (event.getCode().isLetterKey() || event.getCode() == KeyCode.SPACE || event.getCode().isDigitKey())) return;

        getItems().clear();
        previouslyAdded.clear();

        for (Listable<T> backend: backends) {
            List<T> list = backend.toList();

            for (T item : list) {
                if (!previouslyAdded.contains(item.toString()) && item.toString().toLowerCase().contains(inputField.getText().toLowerCase())) {
                    previouslyAdded.add(item.toString());

                    MenuItem menuItem = new MenuItem(item.toString());
                    menuItem.setOnAction(e -> inputField.setText(item.toString()));
                    getItems().add(menuItem);
                }
            }
        }

        Bounds bounds = inputField.getBoundsInLocal();
        Bounds screenBounds = inputField.localToScreen(bounds);
        double x = screenBounds.getMinX();
        double y = screenBounds.getMinY() + inputField.getHeight();

        show(inputField, x, y);
    }
}
