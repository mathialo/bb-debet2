/*
 * Copyright (C) 2020  Mathias Lohne
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

package com.mathiaslohne.bbdebet2.gui.applets;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewUser;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class InactiveUsers extends Applet {

    @FXML
    private ListView<ViewUser> activeUsersView;
    @FXML
    private ListView<ViewUser> inactiveUsersView;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Inaktive brukere", "InactiveUsersView");
    }


    private void innerMakeInactive(User user) {
        kernel.getInactiveUserList().add(user);
        kernel.getUserList().remove(user);
        updateLists();
    }


    @FXML
    private void makeInactive(ActionEvent event) {
        ViewUser chosen = activeUsersView.getSelectionModel().getSelectedItem();

        if (chosen != null) {
            innerMakeInactive(chosen.getUserObject());
        }
    }


    private void innerMakeActive(User user) {
        if (kernel.getUserList().contains(user.getUserName())) {
            TextInputDialog newNameDialog = new TextInputDialog("nytt navn");
            newNameDialog.setHeaderText("En annen bruker har det navnet");
            Optional<String> result = newNameDialog.showAndWait();

            if (result.isEmpty()) return;

            user.setUserName(result.get());
        }

        kernel.getUserList().add(user);
        kernel.getInactiveUserList().remove(user);

        updateLists();
    }


    @FXML
    private void makeActive(ActionEvent event) {
        ViewUser chosen = inactiveUsersView.getSelectionModel().getSelectedItem();

        if (chosen != null) {
            innerMakeActive(chosen.getUserObject());
        }
    }


    private void updateLists() {
        activeUsersView.setItems(kernel.getUserList().toObservableList(true));
        inactiveUsersView.setItems(kernel.getInactiveUserList().toObservableList(true));
        Main.getCurrentAdminController().repaintUserList();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        updateLists();
    }
}
