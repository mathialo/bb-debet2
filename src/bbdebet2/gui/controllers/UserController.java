/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.gui.applets.NewUserTransaction;
import bbdebet2.gui.customelements.MakeCustomProductDialog;
import bbdebet2.gui.customelements.StorageButton;
import bbdebet2.gui.modelwrappers.ViewProduct;
import bbdebet2.gui.modelwrappers.ViewSale;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ArrayList;
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

    private Kernel kernel;

    private Timeline logOutTimer;
    private Alert logOutAlert;

    private List<Alert> openAlertBoxes;

    private boolean isGlasUser = false;


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
        Set<Product> productSelection = kernel.getStorage().getProductSet();

        storageContainer.getChildren().clear();
        for (Product p : productSelection) {
            StorageButton button = new StorageButton(p);
            button.setOnAction(event -> addProductToCart(p));
            if (isGlasUser) button.setGlasUser(true);
            storageContainer.getChildren().add(button);
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
        updateShoppingCartTitleLabel();

        // Make sure alert list is cleared
        openAlertBoxes.clear();

        // New logout timer
        updateLogoutTimer();
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
        // Delete old timer
        deleteLogoutTimer();

        // make a new one
        logOutTimer = new Timeline(new KeyFrame(
            Duration.millis(kernel.getSettingsHolder().getMaxInactiveTime() * 1000),
            e -> warnLogout()
        ));
        logOutTimer.play();
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
            kernel.getLogger().log(Main.getActiveUser() + " forcibly logged out");
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
    }


    private void updateShoppingCartTitleLabel() {
        double total = 0;

        for (ViewProduct vp : shoppingCartView.getItems()) {
            total += convertPrice(vp.getProductObject().getSellPrice());
        }

        shoppingCartTitleLabel.setText(
            String.format("Handlekurv, total %s", CurrencyFormatter.format(total)));
    }


    @FXML
    public void handleResetCart(ActionEvent event) {
        // Re-add products to storage
        for (ViewProduct vp : shoppingCartView.getItems()) {
            if (!vp.getProductObject().isCustom()) {
                kernel.getStorage().add(vp.getProductObject());
            }
        }

        // Empty cart list
        shoppingCartView.getItems().clear();

        // Update gui
        updateStorageView();
        updateFavouritesView();
        updateShoppingCartTitleLabel();

        // Reset logout timer
        updateLogoutTimer();
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
                        kernel.getLogger().log(
                            Main.getActiveUser() + " just run out of money, notification sent"
                        );
                    } catch (MessagingException | InvalidEncryptionException e) {
                        kernel.getLogger().log(e);
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
        // Escape logs out
        Main.getUserScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ESCAPE) {
                    handleLogout(null);
                }
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
