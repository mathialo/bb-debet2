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

package bbdebet2.gui.controllers;

import bbdebet2.gui.Main;
import bbdebet2.gui.applets.AddBalance;
import bbdebet2.gui.applets.AddProducts;
import bbdebet2.gui.applets.BackupRestore;
import bbdebet2.gui.applets.CategoryManagement;
import bbdebet2.gui.applets.Console;
import bbdebet2.gui.applets.CsvViewer;
import bbdebet2.gui.applets.EditProducts;
import bbdebet2.gui.applets.EditUser;
import bbdebet2.gui.applets.NewSemester;
import bbdebet2.gui.applets.NewUser;
import bbdebet2.gui.applets.SendEmail;
import bbdebet2.gui.applets.Settings;
import bbdebet2.gui.applets.Stocktaking;
import bbdebet2.gui.modelwrappers.ViewProduct;
import bbdebet2.gui.modelwrappers.ViewSale;
import bbdebet2.gui.modelwrappers.ViewUser;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.plugins.Plugin;
import bbdebet2.kernel.plugins.PluginFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
    private Menu pluginMenu;


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


    private void createPluginMenu() {
        File file = new File("/usr/local/share/bbdebet2/plugins");

        // If plugin directory is not created, skip this
        if (!file.exists()) return;

        for (File pluginDir : file.listFiles()) {
            if (!pluginDir.isDirectory()) continue;

            try {
                Plugin plugin = PluginFactory.loadPlugin(pluginDir);
                MenuItem menuItem = new MenuItem(plugin.toString());

                menuItem.setOnAction(event -> {
                    try {
                        kernel.saveAll(); // save all in case the plugin depends on saved data
                        plugin.run();
                    } catch (Exception e) {
                        kernel.getLogger().log(e);
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                        alert.setHeaderText("Feil i kjøring av plugin");
                        alert.showAndWait();
                    }
                });

                pluginMenu.getItems().add(menuItem);
            } catch (FileNotFoundException e) {
                kernel.getLogger().log(e);
            } catch (Exception e) {
                kernel.getLogger().log(e);
            }
        }
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
    public void newEditUserWindow(ActionEvent event) {
        EditUser.createAndDisplayDialog();
    }


    @FXML
    public void newAddBalanceWindow(ActionEvent event) {
        AddBalance.createAndDisplayDialog();
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
    public void newNewSemesterWindow(ActionEvent event) {
        NewSemester.createAndDisplayDialog();
    }


    @FXML
    public void newBackupRestoreWindow(ActionEvent event) {
        BackupRestore.createAndDisplayDialog();
    }

    @FXML
    public void newCategoryManagementWindow(ActionEvent event) {
        CategoryManagement.createAndDisplayDialog();
    }


    @FXML
    public void viewMoneyInserts(ActionEvent event) {
        File file = new File(kernel.SAVE_DIR + "moneyinserts.csv");
        try {
            CsvViewer.createAndDisplayDialog(file, "Innskudd");
        } catch (IOException e) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR,
                "Kunne ikke finne fil for å vise innskud. Den kan være tom."
            );
            alert.getDialogPane().setPrefHeight(200);
            alert.showAndWait();
        }
    }


    @FXML
    public void viewLosses(ActionEvent event) {
        File file = new File(kernel.SAVE_DIR + "losses.csv");
        try {
            CsvViewer.createAndDisplayDialog(file, "Svinn");
        } catch (IOException e) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR,
                "Kunne ikke finne fil for å vise svinn. Den kan være tom."
            );
            alert.getDialogPane().setPrefHeight(200);
            alert.showAndWait();
        }
    }


    @FXML
    public void showVersionNumber(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, Main.FULL_VERSION);
        alert.setHeaderText(null);
        alert.show();
    }


    @FXML
    public void showUserManual(ActionEvent event) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load("file:/usr/local/share/bbdebet2/manual_bbdebet2.html");
        Stage stage = new Stage();
        Scene scene = new Scene(browser, 850, 650);

        stage.setScene(scene);
        stage.setTitle("Brukermanual");
        stage.show();
    }


    @FXML
    public void runKernelConsole(ActionEvent event) {
        Console.createAndDisplayDialog("Kernel", kernel);
    }


    @FXML
    public void newSendEmailWindow(ActionEvent event) {
        SendEmail.createAndDisplayDialog();
    }


    @FXML
    public void askForUserDeletion(ActionEvent event) {
        ViewUser selectedUser = userListView.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR, "Velg en bruker fra listen for å slettes");
            alert.showAndWait();
            return;
        }

        Alert askForConfirmation = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Dette vil fjerne " + selectedUser + " fra systemet. All saldo vil bli fjernet. Er du sikker?"
        );
        askForConfirmation.setHeaderText("Bekreft sletting");
        askForConfirmation.getDialogPane().setPrefHeight(200);

        Optional<ButtonType> result = askForConfirmation.showAndWait();

        if (result.get() == ButtonType.OK) {
            User user = selectedUser.getUserObject();
            kernel.getUserList().remove(user);
            repaintUserList();
        }
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
        createPluginMenu();
    }


    public void postInitialize() {
        // Escape logs out
        Main.getAdminScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ESCAPE) {
                    logout(null);
                }
            }
        });
    }
}
