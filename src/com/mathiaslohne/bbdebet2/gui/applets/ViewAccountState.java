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
import com.mathiaslohne.bbdebet2.kernel.accounting.Account;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import com.mathiaslohne.bbdebet2.kernel.logging.CsvLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ViewAccountState extends Applet {

    @FXML
    private GridPane accountViewHolder;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Kontostatus", "ViewAccountStateView");
    }


    private void populate() {
        for (Account account : kernel.getAccounts()) {

        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

}
