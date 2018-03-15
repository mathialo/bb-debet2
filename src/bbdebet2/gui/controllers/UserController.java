/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.gui.applets.NewUserTransaction;
import bbdebet2.gui.customelements.StorageButton;
import bbdebet2.gui.modelwrappers.ViewProduct;
import bbdebet2.gui.modelwrappers.ViewSale;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

public class UserController implements Initializable {

    @FXML
    private Label loginNameView;
    @FXML
    private FlowPane favouritesContainer;
    @FXML
    private FlowPane storageContainer;
    @FXML
    private TableView<ViewSale> userSalesHistoryView;
    @FXML
    private TableColumn<ViewSale, String> userSalesHistorySaleDateCol;
    @FXML
    private TableColumn<ViewSale, String> userSalesHistoryProductCol;
    @FXML
    private TableColumn<ViewSale, String> userSalesHistoryPriceCol;
    @FXML
    private ListView<ViewProduct> shoppingCartView;
    @FXML
    private Label shoppingCartTitleLabel;

    private Kernel kernel;


    private static String formatTitleString(User user) {
        return String.format(
            "Logget inn som %s. Saldo: %s.", user.getUserName(), user.getFormatedBalance());
    }


    private void updateFavouritesView() {
        ArrayList<String> favourites = kernel.getSalesHistory().getFavourites(
            Main.getActiveUser(), 5);

        favouritesContainer.getChildren().clear();
        for (String s : favourites) {
            if (s == null) continue;

            Product p = kernel.getStorage().find(s);
            StorageButton button = null;
            if (p != null) {
                button = new StorageButton(p);
                button.setOnAction(event -> addProductToCart(p));
            } else {
                button = new StorageButton(s);
            }

            favouritesContainer.getChildren().add(button);
        }
    }


    private void updateStorageView() {
        Set<Product> productSelection = kernel.getStorage().getProductSet();

        storageContainer.getChildren().clear();
        for (Product p : productSelection) {
            StorageButton button = new StorageButton(p);
            button.setOnAction(event -> addProductToCart(p));
            storageContainer.getChildren().add(button);
        }
    }


    private void setUpSalesHistoryView() {
        userSalesHistorySaleDateCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("saleDate")
        );
        userSalesHistoryProductCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("productName")
        );
        userSalesHistoryPriceCol.setCellValueFactory(
            new PropertyValueFactory<ViewSale, String>("price")
        );

        userSalesHistorySaleDateCol.setReorderable(false);
        userSalesHistoryProductCol.setReorderable(false);
        userSalesHistoryPriceCol.setReorderable(false);

        userSalesHistorySaleDateCol.setReorderable(false);
        userSalesHistoryProductCol.setReorderable(false);
        userSalesHistoryPriceCol.setReorderable(false);
    }


    private void updateSalesHistoryView() {
        ObservableList<ViewSale> data = kernel.getSalesHistory().filterOnUser(Main.getActiveUser());
        userSalesHistoryView.setItems(data);
    }


    public void login(User user) {
        // Set active user
        Main.setActiveUser(user);

        // Update and populate GUI
        loginNameView.setText(formatTitleString(user));
        updateFavouritesView();
        updateStorageView();
        updateSalesHistoryView();
    }


    public void addProductToCart(Product product) {
        // Take product of of storage temporarily, add it to cart list
        shoppingCartView.getItems().add(new ViewProduct(kernel.getStorage().get(product)));

        // Update storage view as prices and availability might have changed
        updateStorageView();
        updateFavouritesView();
        updateShoppingCartTitleLabel();
    }


    private void updateShoppingCartTitleLabel() {
        double total = 0;

        for (ViewProduct vp : shoppingCartView.getItems()) {
            total += vp.getProductObject().getSellPrice();
        }

        shoppingCartTitleLabel.setText(String.format("Handlekurv, total %.2f kr", total));
    }


    @FXML
    public void handleResetCart(ActionEvent event) {
        // Re-add products to storage
        for (ViewProduct vp : shoppingCartView.getItems()) {
            kernel.getStorage().add(vp.getProductObject());
        }

        // Empty cart list
        shoppingCartView.getItems().clear();

        // Update gui
        updateStorageView();
        updateFavouritesView();
        updateShoppingCartTitleLabel();
    }


    @FXML
    public void handleConfirmPurchase(ActionEvent event) {
        // Process purchases
        for (ViewProduct vp : shoppingCartView.getItems()) {
            kernel.getTransactionHandler().newPurchase(Main.getActiveUser(), vp.getProductObject());
        }

        // Empty cart list
        shoppingCartView.getItems().clear();

        // Update gui
        updateFavouritesView();
        updateSalesHistoryView();
        updateShoppingCartTitleLabel();
    }


    public void repaintGui() {
        if (Main.getActiveUser() == null) return;

        updateFavouritesView();
        updateSalesHistoryView();
        updateStorageView();
        updateShoppingCartTitleLabel();
        loginNameView.setText(formatTitleString(Main.getActiveUser()));
    }


    public void logout() {
        // Set active user
        Main.setActiveUser(null);
    }


    @FXML
    public void handleLogout(ActionEvent event) {
        logout();
        Main.toLoginScreen();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentUserController(this);
        setUpSalesHistoryView();
    }


    @FXML
    public void openNewUserTransactionDialog(ActionEvent event) {
        NewUserTransaction.createAndDisplayDialog();
    }
}
