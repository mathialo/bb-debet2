/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CsvViewer extends Applet {

    private File csvFile;
    private String title;

    @FXML
    private GridPane centerGridPane;


    public static void createAndDisplayDialog(File csvFile, String title) throws IOException {
        FXMLLoader loader = Applet.createAndDisplayDialog(title, "CsvViewerScreen");
        ((CsvViewer) loader.getController()).setCsvFile(csvFile);
        ((CsvViewer) loader.getController()).setTitle(title);
        ((CsvViewer) loader.getController()).readFile();
    }


    private void readFile() throws IOException {
        // Open file
        Scanner scanner = new Scanner(csvFile);

        // Some random stuff that needs declaring
        String line;
        String[] lineArr;
        int rownum = 0;

        while (scanner.hasNextLine()) {
            // Read and parse line, and add each cell as a label in the GridPane
            line = scanner.nextLine();
            lineArr = line.split("\\s*,\\s*");

            for (int colnum = 0; colnum < lineArr.length; colnum++) {
                centerGridPane.add(new Label(lineArr[colnum]), colnum, rownum);
            }

            rownum++;
        }

        // Make sure columns is evenly spaced
        int numCols = centerGridPane.getColumnCount();
        for (int col = 0; col < numCols; col++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / numCols);
            centerGridPane.getColumnConstraints().add(columnConstraints);
        }

        scanner.close();
    }


    private void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }


    private void setTitle(String title) {
        this.title = title;
    }
}
