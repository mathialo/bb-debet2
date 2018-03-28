/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.gui.applets.AddProducts;
import bbdebet2.gui.applets.Console;
import bbdebet2.gui.applets.EditProducts;
import bbdebet2.gui.applets.NewUser;
import bbdebet2.gui.applets.Settings;
import bbdebet2.gui.applets.Stocktaking;
import bbdebet2.gui.modelwrappers.ViewProduct;
import bbdebet2.gui.modelwrappers.ViewSale;
import bbdebet2.gui.modelwrappers.ViewUser;
import bbdebet2.kernel.Kernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private Kernel kernel;

    @FXML
    private TableView<ViewSale> saleHistoryView;
    @FXML
    private TableColumn<ViewSale, String> saleHistoryTimeCol;
    @FXML
    private TableColumn<ViewSale, String> saleHistoryUserCol;
    @FXML
    private TableColumn<ViewSale, String> saleHistoryProductCol;
    @FXML
    private TableColumn<ViewSale, String> saleHistoryPriceCol;
    @FXML
    private TableView<ViewProduct> storageView;
    @FXML
    private TableColumn<ViewProduct, String> storageProductCol;
    @FXML
    private TableColumn<ViewProduct, String> storageBuyPriceCol;
    @FXML
    private TableColumn<ViewProduct, String> storageSellPriceCol;
    @FXML
    private TableView<ViewUser> userListView;
    @FXML
    private TableColumn<ViewUser, String> userListIdCol;
    @FXML
    private TableColumn<ViewUser, String> userListUserNameCol;
    @FXML
    private TableColumn<ViewUser, String> userListEmailCol;
    @FXML
    private TableColumn<ViewUser, String> userListBalanceCol;


    @FXML
    public void newAddProductsWindow(ActionEvent event) {
        AddProducts.createAndDisplayDialog();
    }


    private void setupSaleHistoryView() {
        saleHistoryTimeCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("saleDate")
        );
        saleHistoryUserCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("userName")
        );
        saleHistoryProductCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("productName")
        );
        saleHistoryPriceCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("price")
        );
    }


    public void repaintSaleHistory() {
        saleHistoryView.setItems(kernel.getSalesHistory().toObservableList());
    }


    private void setupStorageView() {
        storageProductCol.setCellValueFactory(
            new PropertyValueFactory<ViewProduct, String>("productName")
        );
        storageBuyPriceCol.setCellValueFactory(
            new PropertyValueFactory<ViewProduct, String>("buyPrice")
        );
        storageSellPriceCol.setCellValueFactory(
            new PropertyValueFactory<ViewProduct, String>("sellPrice")
        );
    }


    public void repaintStorage() {
        storageView.setItems(kernel.getStorage().toObservableList());
    }


    private void setupUserListView() {
        userListIdCol.setCellValueFactory(
            new PropertyValueFactory<ViewUser, String>("userId")
        );
        userListUserNameCol.setCellValueFactory(
            new PropertyValueFactory<ViewUser, String>("userName")
        );
        userListEmailCol.setCellValueFactory(
            new PropertyValueFactory<ViewUser, String>("mail")
        );
        userListBalanceCol.setCellValueFactory(
            new PropertyValueFactory<ViewUser, String>("balance")
        );
    }


    public void repaintUserList() {
        userListView.setItems(kernel.getUserList().toObservableList());
    }


    public void repaintAll() {
        repaintSaleHistory();
        repaintStorage();
        repaintUserList();
    }


    @FXML
    public void deleteSelectedSale() {
        ViewSale selected = saleHistoryView.getSelectionModel().getSelectedItem();

        // Make sure a sale is selected
        if (selected == null) return;

        kernel.getTransactionHandler().refund(selected.getSaleObject());
        repaintAll();
    }


    @FXML
    public void newStocktakingWindow(ActionEvent event) {
        Stocktaking.createAndDisplayDialog();
    }


    @FXML
    public void logout(ActionEvent event) {
        Main.toLoginScreen();
    }


    @FXML
    public void newNewUserWindow(ActionEvent event) {
        NewUser.createAndDisplayDialog();
    }


    @FXML
    public void newEditProductsWindow(ActionEvent event) {
        EditProducts.createAndDisplayDialog();
    }


    @FXML
    public void newSettingsWindow(ActionEvent event) {
        Settings.createAndDisplayDialog();
    }


    @FXML
    public void runKernelConsole(ActionEvent event) {
        Console.createAndDisplayDialog("Kernel", kernel);
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
