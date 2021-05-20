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

import com.mathiaslohne.bbdebet2.kernel.core.Listable;
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

    private boolean active;
    private MatchType matchType = MatchType.CONTAINS;

    public enum MatchType {
        CONTAINS, STARTS_WITH
    }


    public SuggestionMenu(TextField inputField, Listable<T> backend) {
        this.inputField = inputField;
        backends = new LinkedList<Listable<T>>();
        backends.add(backend);

        previouslyAdded = new HashSet<>();

        inputField.setOnKeyPressed(this::updateContextMenuItems);
        active=true;
    }


    public SuggestionMenu(TextField inputField, List<Listable<T>> backends) {
        this.inputField = inputField;
        this.backends = backends;

        previouslyAdded = new HashSet<>();

        inputField.setOnKeyPressed(this::updateContextMenuItems);
        active=true;
    }


    public SuggestionMenu<T> setActive(boolean active) {
        this.active = active;
        if (active) updateContextMenuItems(null);
        else hide();
        return this;
    }


    public SuggestionMenu<T> setMatchType(MatchType matchType) {
        this.matchType = matchType;
        return this;
    }


    private boolean match(String haystack, String needle) {
        switch (matchType) {
            case CONTAINS:
                return haystack.contains(needle);

            case STARTS_WITH:
                return haystack.startsWith(needle);
        }
        return false;
    }


    public void updateContextMenuItems(KeyEvent event) {
        System.out.println("Clicky!");
        if (!active) {
            System.out.println("Not active");
            return;
        }

        if (event != null && !(event.getCode().isLetterKey() || event.getCode() == KeyCode.SPACE || event.getCode().isDigitKey())) {
            System.out.println("keycode " + event.getCode());
            return;
        }

        System.out.println("setting items");
        getItems().clear();
        previouslyAdded.clear();

        for (Listable<T> backend : backends) {
            List<T> list = backend.toList();

            for (T item : list) {
                if (!previouslyAdded.contains(item.toString()) && match(item.toString().toLowerCase(), inputField.getText().toLowerCase())) {
                    previouslyAdded.add(item.toString());

                    MenuItem menuItem = new MenuItem(item.toString());
                    menuItem.setOnAction(e -> {
                        inputField.setText(item.toString());
                        inputField.positionCaret(inputField.getText().length());
                    });
                    getItems().add(menuItem);
                    System.out.println(item);
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
