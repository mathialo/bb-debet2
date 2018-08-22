/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.Kernel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class BackupRestore extends Applet {

    @FXML
    private ListView<String> backupListView;
    private ArrayList<Integer> backupTimestamps;

    private File backupDir;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Behandle backups", "BackupRestoreScreen");
    }


    @FXML
    private void deleteSelectedBackup(ActionEvent event) {
        // If no element is selected, stop
        if (backupListView.getSelectionModel().isEmpty()) return;

        File dir = new File(getSelectedBackupTimestamp());
        deleteDir(dir);

        backupTimestamps.remove(getSelectedIndex());
        backupListView.getItems().remove(getSelectedIndex());
    }


    @FXML
    private void restoreSelectedBackup(ActionEvent event) {
        // If no element is selected, stop
        if (backupListView.getSelectionModel().isEmpty()) return;

        // Get confirmation as we are about to do some drastic changes
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bekreft tilbakestilling");
        alert.setHeaderText("Er du sikker på at du vil tilbakestille?");
        alert.setContentText("Lagerstatus, brukerinfo og alle salgsdata vil bli skrevet over");
        alert.getDialogPane().setPrefHeight(170);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK){
            return;
        }

        // Reload all backuped files in dir into kernel
        try {
            kernel.getSalesHistory().resetToFile(new File(backupDir + getSelectedBackupTimestamp() + "/" + Kernel.SALESHISTORY_FILENAME));
            kernel.getUserList().resetToFile(new File(backupDir + getSelectedBackupTimestamp() + "/" + Kernel.USERLIST_FILENAME));
            kernel.getStorage().resetToFile(new File(backupDir + getSelectedBackupTimestamp() + "/" + Kernel.STORAGE_FILENAME));

        } catch (Exception e) {
            kernel.getLogger().log(e);
        }

        Main.getCurrentAdminController().repaintAll();
    }


    private void deleteDir(File file) {

        File[] contents = file.listFiles();

        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }

        file.delete();
    }

    private String getSelectedBackupTimestamp() {
        // Get selected backup
        int selectedIndex = backupListView.getSelectionModel().getSelectedIndices().get(0);

        return "/" + backupTimestamps.get(selectedIndex);
    }

    private int getSelectedIndex() {
        return backupListView.getSelectionModel().getSelectedIndices().get(0);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        backupDir = new File(Kernel.SAVE_DIR + "autosave/");
        File[] backups = backupDir.listFiles(File::isDirectory);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy - HH.mm");

        backupTimestamps = new ArrayList<Integer>(backups.length);
        for (File dir : backups) {
            try {
                // Hackyhacky. Sjekker om mappenavnet er et tall.
                int i = Integer.parseInt(dir.getName());
                backupTimestamps.add(i);
            } catch (NumberFormatException e) {
            }
        }

        Collections.sort(backupTimestamps);
        ArrayList<String> backupdates = new ArrayList<String>(backups.length);

        for (int i : backupTimestamps) {
            backupdates.add(dateFormat.format(new Date(i * 1000L)));
        }

        backupListView.setItems(FXCollections.observableArrayList(backupdates));
    }
}
