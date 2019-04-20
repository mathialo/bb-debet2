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

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.logging.CsvLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Stocktaking extends Applet {

    @FXML
    private GridPane storageViewHolder;
    private int rownum;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Varetelling", "StocktakingView");
    }


    private void updateStorageViewHolder() {
        rownum = 1;

        for (Product p : kernel.getStorage().getProductSet()) {
            storageViewHolder.add(new Label(p.getName()), 0, rownum);
            storageViewHolder.add(new TextField(kernel.getStorage().getNum(p) + ""), 1, rownum);
            rownum++;
        }
    }


    private Node getNodeFromStorageViewHolder(int col, int row) {
        return (Node) storageViewHolder.getChildren().get(row * 2 + col);
    }


    @FXML
    public void handleStocktaking(ActionEvent event) {
        updateStorageNumbers();
        Main.getCurrentAdminController().repaintAll();
        exit(event);
    }


    private void updateStorageNumbers() {
        for (int i = 1; i < rownum; i++) {
            Label productNameView = (Label) getNodeFromStorageViewHolder(0, i);
            TextField productNumInput = (TextField) getNodeFromStorageViewHolder(1, i);

            int diff = kernel.getStorage().updateStorageNum(
                productNameView.getText(),
                Integer.parseInt(productNumInput.getText())
            );

            if (diff != 0) {
                kernel.getLogger().log(
                    "Stocktaking: " + productNameView.getText() + " changed by " + diff
                );

                try {
                    CsvLogger.addProductLoss(
                        kernel.getStorage().find(productNameView.getText()),
                        -diff
                    );
                } catch (IOException e) {
                    kernel.getLogger().log(e);
                }
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        updateStorageViewHolder();
    }
}
