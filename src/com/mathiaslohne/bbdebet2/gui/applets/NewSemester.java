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
import com.mathiaslohne.bbdebet2.kernel.logging.CsvLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewSemester extends Applet {

    @FXML
    private TextField currentSemesterNameInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Nytt semester", "NewSemesterScreen");
    }


    @FXML
    public void startNewSemester(ActionEvent event) {
        String currentSemesterName = currentSemesterNameInput.getText();

        // Check that name is present
        if (currentSemesterName == null || currentSemesterName.replaceAll("\\s+", "").equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Du må gi nåværende semester et navn!");
            alert.setHeaderText("Ingen navn");
            alert.setTitle("Ingen navn");
            alert.showAndWait();
            return;
        }

        // Check that name is unique
        File placeDir = new File(Kernel.SAVE_DIR + "history/" + currentSemesterName);

        if (placeDir.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Velg et unikt navn");
            alert.setHeaderText("'" + currentSemesterName + "' finnes allerede");
            alert.setTitle("Feil i navn");
            alert.showAndWait();
            return;
        }

        // Make directory (/ies)
        placeDir.mkdirs();

        try {
            // Save a copy of everything in folder
            kernel.getSalesHistory().saveFile(new File(placeDir.getAbsolutePath() + "/" + Kernel.SALESHISTORY_FILENAME));
            kernel.getStorage().saveFile(new File(placeDir.getAbsolutePath() + "/" + Kernel.STORAGE_FILENAME));
            kernel.getUserList().saveFile(new File(placeDir.getAbsolutePath() + "/" + Kernel.USERLIST_FILENAME));

            // Reset SalesHistory
            kernel.getSalesHistory().reset();

            // Move other semester-spesific csv-files
            for (String filename : CsvLogger.getHeaders().keySet()) {
                File file = new File(Kernel.SAVE_DIR + filename + ".csv");

                if (file.exists()) {
                    file.renameTo(new File(placeDir.getAbsolutePath() + "/" + filename + ".csv"));
                }
            }

            Kernel.getLogger().log("Beginning new semester");

        } catch (IOException e) {
            Kernel.getLogger().log(e);

            Alert alert = new Alert(Alert.AlertType.ERROR, "Uventet feil");
            alert.setHeaderText("Uventet feil under lagring");
            alert.setTitle("Sjekk log-filer for detaljer");
            alert.showAndWait();
            return;
        }

        Main.getCurrentAdminController().repaintAll();
        exit(event);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
}
