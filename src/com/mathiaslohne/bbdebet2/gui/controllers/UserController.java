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

package com.mathiaslohne.bbdebet2.gui.controllers;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.gui.applets.NewUserTransaction;
import com.mathiaslohne.bbdebet2.gui.customelements.EulaConfirmation;
import com.mathiaslohne.bbdebet2.gui.customelements.MakeCustomProductDialog;
import com.mathiaslohne.bbdebet2.gui.customelements.StorageButton;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewProduct;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewSale;
import com.mathiaslohne.bbdebet2.kernel.core.CategoryDict;
import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import com.mathiaslohne.bbdebet2.kernel.core.SettingsHolder;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import com.mathiaslohne.bbdebet2.kernel.mailing.InvalidEncryptionException;
import com.mathiaslohne.bbdebet2.kernel.search.ProductSearchEngine;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

    @FXML
    private ScrollPane storageScrollPane;

    @FXML
    private TextField searchProductInput;

    private Kernel kernel;

    private Timeline logOutTimer;
    private Alert logOutAlert;

    private List<Alert> openAlertBoxes;

    private boolean isGlasUser = false;
    private boolean searchActive = false;


    private String formatTitleString(User user) {
        if (isGlasUser) {
            return String.format(
                "Logget inn som %s.", user.getUserName()
            );
        } else {
            return String.format(
                "Logget inn som %s. Saldo: %s.", user.getUserName(),
                CurrencyFormatter.format(user.getBalance())
            );
        }
    }


    private void updateFavouritesView() {
        if (isGlasUser) {
            favouritesContainer.getChildren().clear();
            return;
        }

        ArrayList<String> favourites = kernel.getSalesHistory().getFavourites(
            Main.getActiveUser(), kernel.getSettingsHolder().getNumOfFavourites());

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
        searchProductInput.setText("");

        Set<Product> productSelection = kernel.getStorage().getProductSet();

        storageContainer.getChildren().clear();

        if (kernel.getSettingsHolder().getSortingOrder() == SettingsHolder.SortingOrder.ALPHABETIC) {
            for (Product p : productSelection) {
                StorageButton button = new StorageButton(p);
                button.setOnAction(event -> addProductToCart(p));
                if (isGlasUser) button.setGlasUser(true);
                storageContainer.getChildren().add(button);
            }

        } else if (kernel.getSettingsHolder().getSortingOrder() == SettingsHolder.SortingOrder.CATEGORICAL) {
            Set<Product> added = new HashSet<>();

            for (CategoryDict.Category c : kernel.getCategories().getCategories()) {
                for (Product p : productSelection) {
                    if (kernel.getCategories().getProductCategory(p) == c) {
                        StorageButton button = new StorageButton(p);
                        button.setOnAction(event -> addProductToCart(p));
                        if (isGlasUser) button.setGlasUser(true);
                        storageContainer.getChildren().add(button);

                        added.add(p);
                    }
                }
            }

            // Add products with no category
            if (added.size() != productSelection.size()) {
                for (Product p : productSelection) {
                    if (!added.contains(p)) {
                        StorageButton button = new StorageButton(p);
                        button.setOnAction(event -> addProductToCart(p));
                        if (isGlasUser) button.setGlasUser(true);
                        storageContainer.getChildren().add(button);
                    }
                }
            }
        }

        // Add "custom" option
        StorageButton button = new StorageButton();
        button.setOnAction(event -> {
            MakeCustomProductDialog dialog = new MakeCustomProductDialog();
            Optional<Product> result = dialog.showAndWait();
            if (result.isPresent()) addProductToCart(result.get());
        });
        storageContainer.getChildren().add(button);
    }


    @FXML
    private void newSearch() {
        if (searchProductInput.getText().isEmpty()) {
            updateFavouritesView();
            updateStorageView();

        } else {
            storageContainer.getChildren().clear();
            favouritesContainer.getChildren().clear();

            List<Product> productSelection = ProductSearchEngine.search(kernel, searchProductInput.getText(), Main.getActiveUser());

            for (Product p : productSelection) {
                StorageButton button = new StorageButton(p);
                button.setOnAction(e -> addProductToCart(p));
                storageContainer.getChildren().add(button);

                if (isGlasUser) button.setGlasUser(true);
            }
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
        ObservableList<ViewSale> data = kernel.getSalesHistory().filterOnUser(Main.getActiveUser()).toObservableList();
        userSalesHistoryView.setItems(data);
    }


    public boolean login(User user) {
        Main.setActiveUser(user);

        storageScrollPane.setVvalue(0);

        // Clear searching and request focus
        searchProductInput.setText("");
        Platform.runLater(() -> searchProductInput.requestFocus());

        // Update and populate GUI
        loginNameView.setText(formatTitleString(user));
        updateFavouritesView();
        updateStorageView();
        updateSalesHistoryView();
        updateShoppingCartTitleLabel();

        // Make sure alert list is cleared
        openAlertBoxes.clear();

        // Present EULA if requested and not previously shown
        if (kernel.getSettingsHolder().isRequireEula() && !user.hasAcceptedEula() && !isGlasUser) {
            EulaConfirmation eulaConfirmation = new EulaConfirmation();
            Optional<ButtonType> result = eulaConfirmation.showAndWait();

            if (result.get() == ButtonType.OK) {
                user.acceptEula();
            } else {
                logout();
                return false;
            }
        }

        // New logout timer
        updateLogoutTimer();
        return true;
    }


    public void loginGlass() {
        isGlasUser = true;
        login(new User(kernel.getSettingsHolder().getGlasUserName(), "", -1));
    }


    private void deleteLogoutTimer() {
        if (logOutTimer != null) {
            logOutTimer.stop();
            logOutTimer = null;
        }
    }


    private void updateLogoutTimer() {
        if (Main.getActiveUser() != null) {
            // Delete old timer
            deleteLogoutTimer();

            // make a new one
            logOutTimer = new Timeline(new KeyFrame(
                Duration.millis(kernel.getSettingsHolder().getMaxInactiveTime() * 1000),
                e -> warnLogout()
            ));
            logOutTimer.play();
        }
    }


    private void warnLogout() {
        deleteLogoutTimer();

        ButtonType waitButton = new ButtonType(
            "Utsett " + kernel.getSettingsHolder().getMaxInactiveTime() + " sek",
            ButtonBar.ButtonData.OK_DONE
        );
        logOutAlert = new Alert(
            Alert.AlertType.WARNING,
            "Du blir logget ut om 5 sekunder. Alt du har i handlekurven vil bli kjøpt.",
            waitButton
        );
        logOutAlert.getDialogPane().setPrefHeight(200);
        logOutAlert.setHeaderText("Automatisk utlogging.");
        openAlertBoxes.add(logOutAlert);
        logOutAlert.show();

        // make a new timer
        logOutTimer = new Timeline(new KeyFrame(
            Duration.millis(7 * 1000),
            e -> forceLogout(waitButton)
        ));
        logOutTimer.play();
    }


    private void forceLogout(ButtonType waitButton) {
        if (logOutAlert.getResult() == waitButton) {
            updateLogoutTimer();
        } else {
            Kernel.getLogger().log(Main.getActiveUser() + " timed out");
            logout();
            Main.toLoginScreen();
        }
    }


    public void addProductToCart(Product product) {
        // Take product of of storage temporarily (if not custom), add it to cart list
        if (product.isCustom()) {
            shoppingCartView.getItems().add(new ViewProduct(product));
        } else {
            shoppingCartView.getItems().add(new ViewProduct(kernel.getStorage().get(product)));
        }

        // Update storage view as prices and availability might have changed
        updateStorageView();
        updateFavouritesView();
        updateShoppingCartTitleLabel();

        // Reset logout timer
        updateLogoutTimer();

        // Re-focus search
        Platform.runLater(() -> searchProductInput.requestFocus());

        searchActive = false;
    }


    private void updateShoppingCartTitleLabel() {
        double total = 0;

        for (ViewProduct vp : shoppingCartView.getItems()) {
            total += convertPrice(vp.getProductObject().getSellPrice());
        }

        shoppingCartTitleLabel.setText(
            String.format("Handlekurv, total %s", CurrencyFormatter.format(total)));
    }


    public void reAddCartToStorage() {
        for (ViewProduct vp : shoppingCartView.getItems()) {
            if (!vp.getProductObject().isCustom()) {
                kernel.getStorage().add(vp.getProductObject());
            }
        }
    }


    @FXML
    public void handleResetCart(ActionEvent event) {
        // Re-add products to storage
        reAddCartToStorage();

        // Empty cart list
        shoppingCartView.getItems().clear();

        // Update gui
        updateStorageView();
        updateFavouritesView();
        updateShoppingCartTitleLabel();

        // Reset logout timer
        updateLogoutTimer();

        // Re-focus search
        Platform.runLater(() -> searchProductInput.requestFocus());
    }


    public void removeLastAdded() {
        if (shoppingCartView.getItems().isEmpty()) return;
        if (searchActive) {
            if (searchProductInput.getText().isBlank()) searchActive = false;
            return;
        }

        ViewProduct vp = shoppingCartView.getItems().remove(shoppingCartView.getItems().size() - 1);
        kernel.getStorage().add(vp.getProductObject());

        // Update storage view as prices and availability might have changed
        updateStorageView();
        updateFavouritesView();
        updateShoppingCartTitleLabel();

        // Reset logout timer
        updateLogoutTimer();

        // Re-focus search
        Platform.runLater(() -> searchProductInput.requestFocus());
    }


    @FXML
    public void handleConfirmPurchase(ActionEvent event) {
        double preBalance = Main.getActiveUser().getBalance();

        // Process purchases
        for (ViewProduct vp : shoppingCartView.getItems()) {
            if (isGlasUser)
                vp.getProductObject().setSellPrice(convertPrice(vp.getProductObject().getSellPrice()));

            kernel.getTransactionHandler().newPurchase(Main.getActiveUser(), vp.getProductObject());
        }

        if (preBalance > 0 && Main.getActiveUser().getBalance() < 0) {
            // User just went out of money, send notification
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        kernel.getEmailSender().sendOutOfMoneyNotification(Main.getActiveUser());
                        Kernel.getLogger().log(
                            Main.getActiveUser() + " just run out of money, notification sent"
                        );
                    } catch (MessagingException | InvalidEncryptionException e) {
                        Kernel.getLogger().log(e);
                    }
                    return null;
                }
            };

            new Thread(task).start();
        }

        // Empty cart list
        shoppingCartView.getItems().clear();

        // Update gui
        loginNameView.setText(formatTitleString(Main.getActiveUser()));
        updateFavouritesView();
        updateSalesHistoryView();
        updateShoppingCartTitleLabel();

        // Reset logout timer
        updateLogoutTimer();

        // Re-focus search
        Platform.runLater(() -> searchProductInput.requestFocus());
    }


    public void repaintGui() {
        if (Main.getActiveUser() == null) return;

        updateFavouritesView();
        updateSalesHistoryView();
        updateStorageView();
        updateShoppingCartTitleLabel();
        loginNameView.setText(formatTitleString(Main.getActiveUser()));
    }


    public double convertPrice(double oldPrice) {
        if (isGlasUser) {
            double newPrice = 0;
            while (newPrice < oldPrice) {
                newPrice += kernel.getSettingsHolder().getGlasUserRoundTo();
            }
            return newPrice;
        } else {
            return oldPrice;
        }
    }


    private void logout() {
        Kernel.getLogger().log("Logging out " + Main.getActiveUser());
        if (Main.getActiveUser() == null) return;

        // Force through purchase of all goods in cart
        handleConfirmPurchase(null);

        // Remove logout timer
        deleteLogoutTimer();

        // Close any open alert boxes
        for (Alert alert : openAlertBoxes) {
            alert.close();
        }
        openAlertBoxes.clear();

        // Set active user
        Main.setActiveUser(null);
        isGlasUser = false;
    }


    @FXML
    public void handleLogout(ActionEvent event) {
        if (!shoppingCartView.getItems().isEmpty()) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR,
                "Vennligst bekreft eller tilbakestill handlekurven før du logger ut"
            );
            alert.setHeaderText("Du har varer i handlekurven");
            alert.getDialogPane().setPrefHeight(200);
            openAlertBoxes.add(alert);
            alert.showAndWait();

            // Reset logout timer
            updateLogoutTimer();
            return;
        }

        logout();
        Main.toLoginScreen();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentUserController(this);
        setUpSalesHistoryView();

        openAlertBoxes = new LinkedList<>();
    }


    public void postInitialize() {
        // Capture key presses
        Main.getUserScene().addEventFilter(KeyEvent.KEY_RELEASED, ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                handleLogout(null);
            } else if (ke.getCode() == KeyCode.ENTER) {
                if (searchProductInput.getText().isEmpty()) {
                    handleConfirmPurchase(null);
                } else if (!storageContainer.getChildren().isEmpty()) {
                    addProductToCart(((StorageButton) storageContainer.getChildren().get(0)).getProduct());
                }
            } else if (ke.getCode() == KeyCode.BACK_SPACE) {
                removeLastAdded();
            }
        });

        Main.getUserScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().isLetterKey() && !searchProductInput.focusedProperty().get()) {
                Platform.runLater(() -> {
                    searchProductInput.requestFocus();
                    searchProductInput.setText(searchProductInput.getText()+ke.getText());
                    searchProductInput.positionCaret(1);
                    newSearch();
                });

            }

            if (!searchProductInput.getText().isBlank()) {
                searchActive = true;
            }
        });
    }


    @FXML
    public void openNewUserTransactionDialog(ActionEvent event) {
        NewUserTransaction.createAndDisplayDialog();

        // Reset logout timer
        updateLogoutTimer();
    }
}
