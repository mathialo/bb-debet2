/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.gui.applets.AddProducts;
import bbdebet2.gui.modelwrappers.ViewSale;
import bbdebet2.kernel.Kernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private Kernel kernel;




    @FXML
    public void newAddProductsWindow(ActionEvent event) {
        AddProducts.createAndDisplayDialog();
    }


    private void setupSaleHistoryView() {

    }


    public void repaintSaleHistory() {

    }


    private void setupStorageView() {

    }


    public void repaintStorage() {

    }


    private void setupUserListView() {

    }


    public void repaintUserList() {

    }


    public void repaintAll() {
        repaintSaleHistory();
        repaintStorage();
        repaintUserList();
    }


    @FXML
    public void logout(ActionEvent event) {
        Main.toLoginScreen();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentAdminController(this);
        setupSaleHistoryView();
        repaintUserList();
        setupStorageView();
        repaintStorage();
        setupUserListView();
        repaintUserList();
    }
}
