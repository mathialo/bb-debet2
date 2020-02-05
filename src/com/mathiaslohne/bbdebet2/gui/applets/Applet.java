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

package com.mathiaslohne.bbdebet2.gui.applets;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Applet implements Initializable {

    protected Kernel kernel;


    protected static FXMLLoader createAndDisplayDialog(String title, String fxmlFileName) {
        try {
            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(NewUserTransaction.class.getClassLoader().getResource("com/mathiaslohne/bbdebet2/gui/views/" + fxmlFileName + ".fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            return fxmlLoader;
        } catch (IOException e) {
            Kernel.getLogger().log(e);
        }
        return null;
    }


    @FXML
    protected void exit(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    protected void exit(Node node) {
        ((Stage) node.getScene().getWindow()).close();
    }


    protected void exit(Scene scene) {
        ((Stage) scene.getWindow()).close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.kernel = Main.getKernel();
    }
}
