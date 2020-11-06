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
import com.mathiaslohne.bbdebet2.gui.applets.AccountManagement;
import com.mathiaslohne.bbdebet2.gui.applets.AddBalance;
import com.mathiaslohne.bbdebet2.gui.applets.AddProducts;
import com.mathiaslohne.bbdebet2.gui.applets.BackupRestore;
import com.mathiaslohne.bbdebet2.gui.applets.CategoryManagement;
import com.mathiaslohne.bbdebet2.gui.applets.Console;
import com.mathiaslohne.bbdebet2.gui.applets.CsvViewer;
import com.mathiaslohne.bbdebet2.gui.applets.EditProducts;
import com.mathiaslohne.bbdebet2.gui.applets.EditUser;
import com.mathiaslohne.bbdebet2.gui.applets.InactiveUsers;
import com.mathiaslohne.bbdebet2.gui.applets.MakeExpense;
import com.mathiaslohne.bbdebet2.gui.applets.NewSemester;
import com.mathiaslohne.bbdebet2.gui.applets.NewUser;
import com.mathiaslohne.bbdebet2.gui.applets.SendEmail;
import com.mathiaslohne.bbdebet2.gui.applets.Settings;
import com.mathiaslohne.bbdebet2.gui.applets.Stocktaking;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewExpense;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewProduct;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewSale;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewUser;
import com.mathiaslohne.bbdebet2.kernel.accounting.SpreadSheetExporter;
import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import com.mathiaslohne.bbdebet2.plugins.Plugin;
import com.mathiaslohne.bbdebet2.plugins.PluginFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.mathiaslohne.bbdebet2.kernel.core.Kernel.SAVE_DIR;
import static com.mathiaslohne.bbdebet2.kernel.core.Kernel.getLogger;


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
    private TableView<ViewExpense> accountingView;
    @FXML
    private TableColumn<ViewExpense, String> accountingTimeCol;
    @FXML
    private TableColumn<ViewExpense, String> accountingFromCol;
    @FXML
    private TableColumn<ViewExpense, String> accountingToCol;
    @FXML
    private TableColumn<ViewExpense, String> accountingAmountCol;
    @FXML
    private TableColumn<ViewExpense, String> accountingCommentCol;
    @FXML
    private Menu pluginMenu;
    @FXML
    private Label totalSaleNumLabel;
    @FXML
    private Label totalCurrentSalePriceLabel;
    @FXML
    private Label totalCurrentMarkupLabel;
    @FXML
    private Label totalBuyPriceLabel;
    @FXML
    private Label totalFutureSalePriceLabel;
    @FXML
    private Label totalFutureMarkupLabel;
    @FXML
    private Label totalInsertsLabel;
    @FXML
    private Label totalUserDebtLabel;


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

        saleHistoryPriceCol.setComparator(((s1, s2) -> (int) Math.round(Double.parseDouble(s1.replaceAll(" kr" , ""))*100- Double.parseDouble(s2.replace(" kr", ""))*100)));
    }


    public void repaintSaleHistory() {
        saleHistoryView.setItems(kernel.getSalesHistory().toObservableList());

        totalSaleNumLabel.setText("Antall salg: " + kernel.getSalesHistory().size());
        totalCurrentSalePriceLabel.setText("Total salgspris: " + CurrencyFormatter.format(kernel.getSalesHistory().getTotalSellValue()));
        totalCurrentMarkupLabel.setText("Total avanse: " + CurrencyFormatter.format(kernel.getSalesHistory().getTotalEarnings()));
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

        storageBuyPriceCol.setComparator(((s1, s2) -> (int) Math.round(Double.parseDouble(s1.replaceAll(" kr" , ""))*100- Double.parseDouble(s2.replace(" kr", ""))*100)));
        storageSellPriceCol.setComparator(((s1, s2) -> (int) Math.round(Double.parseDouble(s1.replaceAll(" kr" , ""))*100- Double.parseDouble(s2.replace(" kr", ""))*100)));
    }


    public void repaintStorage() {
        storageView.setItems(kernel.getStorage().toObservableList());

        totalBuyPriceLabel.setText("Total innkjøpspris: " + CurrencyFormatter.format(kernel.getStorage().getTotalBuyValue()));
        totalFutureSalePriceLabel.setText("Total salgspris: " + CurrencyFormatter.format(kernel.getStorage().getTotalSellValue()));
        totalFutureMarkupLabel.setText("Total avanse: " + CurrencyFormatter.format(kernel.getStorage().getTotalMarkup()));
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

        userListBalanceCol.setComparator(((s1, s2) -> (int) Math.round(Double.parseDouble(s1.replaceAll(" kr", "")) * 100 - Double.parseDouble(s2.replace(" kr", "")) * 100)));
        userListIdCol.setComparator(Comparator.comparingInt(Integer::parseInt));
    }


    public void repaintUserList() {
        userListView.setItems(kernel.getUserList().toObservableList());

        totalInsertsLabel.setText("Totale innskudd: " + CurrencyFormatter.format(kernel.getUserList().getTotalBalance()));
        totalUserDebtLabel.setText("Total brukergjeld: " + CurrencyFormatter.format(kernel.getUserList().getTotalDebt()));
    }


    private void setupAccountingView() {
        accountingTimeCol.setCellValueFactory(
            new PropertyValueFactory<ViewExpense, String>("expenceDate")
        );
        accountingFromCol.setCellValueFactory(
            new PropertyValueFactory<ViewExpense, String>("from")
        );
        accountingToCol.setCellValueFactory(
            new PropertyValueFactory<ViewExpense, String>("to")
        );
        accountingAmountCol.setCellValueFactory(
            new PropertyValueFactory<ViewExpense, String>("amount")
        );
        accountingCommentCol.setCellValueFactory(
            new PropertyValueFactory<ViewExpense, String>("comment")
        );
    }


    public void repaintAccounting() {
        accountingView.setItems(kernel.getLedger().toObservableList());
    }


    public void repaintAll() {
        repaintSaleHistory();
        repaintStorage();
        repaintUserList();
        repaintAccounting();

        userListView.sort();
        storageView.sort();
        saleHistoryView.sort();
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
                        Kernel.getLogger().log(e);
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                        alert.setHeaderText("Feil i kjøring av plugin");
                        alert.showAndWait();
                    }
                });

                pluginMenu.getItems().add(menuItem);
            } catch (FileNotFoundException e) {
                Kernel.getLogger().log(e);
            } catch (Exception e) {
                Kernel.getLogger().log(e);
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
        File file = new File(SAVE_DIR + "moneyinserts.csv");
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
        File file = new File(SAVE_DIR + "losses.csv");
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
    public void newMakeExpenceWindow(ActionEvent event) {
        MakeExpense.createAndDisplayDialog(null);
    }


    @FXML
    public void deleteSelectedExpence(ActionEvent event) {
        kernel.getLedger().remove(accountingView.getSelectionModel().getSelectedItem().getExpenceObject());
        repaintAccounting();
    }


    @FXML
    public void exportLedger(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Velg filnavn");
        FileChooser.ExtensionFilter excelfiles = new FileChooser.ExtensionFilter("Excel regneark (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().addAll(excelfiles);
        fileChooser.setSelectedExtensionFilter(excelfiles);
        File xlsFile = fileChooser.showSaveDialog(null);

        try {
            new SpreadSheetExporter(kernel).writeToFile(xlsFile);
        } catch (IOException e) {
            Alert alert = new Alert(
                Alert.AlertType.ERROR,
                e.getMessage()
            );
            alert.setHeaderText("Feil i eksportering");
            alert.getDialogPane().setPrefHeight(200);
            alert.showAndWait();
        }
    }


    @FXML
    public void newAccountManagementWindow(ActionEvent event) {
        AccountManagement.createAndDisplayDialog();
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

        try {
            String installDir = Files.readString(Path.of(SAVE_DIR, ".installdir")).trim();
            webEngine.load("file://" + Path.of(installDir, "manual_bbdebet2.html").toString());

            Stage stage = new Stage();
            Scene scene = new Scene(browser, 850, 650);

            stage.setScene(scene);
            stage.setTitle("Brukermanual");
            stage.show();

        } catch (IOException e) {
            getLogger().log(e);
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Kan ikke vise manual");
            alert.show();
        }
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
    public void newInactiveUsersWindow(ActionEvent event) {
        InactiveUsers.createAndDisplayDialog();
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
        setupAccountingView();
        repaintAccounting();
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
