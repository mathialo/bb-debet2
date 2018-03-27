/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        updateStorageViewHolder();
    }
}