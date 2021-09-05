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

package com.mathiaslohne.bbdebet2.gui.applets;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.gui.customelements.ConfirmFuzzyProductFind;
import com.mathiaslohne.bbdebet2.gui.customelements.NewCategoryRulePrompt;
import com.mathiaslohne.bbdebet2.gui.customelements.SuggestionMenu;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewProduct;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewProductForAddition;
import com.mathiaslohne.bbdebet2.kernel.accounting.Expense;
import com.mathiaslohne.bbdebet2.kernel.core.CategoryDict;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.Listable;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;


public class AddProducts extends Applet {

    private static final double BOTTLE_LOSE_RATE = 0.15;

    @FXML
    private TextField productNameInput;
    @FXML
    private TextField buyPriceInput;
    @FXML
    private TextField packQuantityInput;
    @FXML
    private ChoiceBox<MarkupLevel> markupInput;
    @FXML
    private ToggleButton addPantInput;
    @FXML
    private TextField salePriceInput;
    @FXML
    private TextField quantityInput;
    @FXML
    private TableView<ViewProductForAddition> cartTableView;
    @FXML
    private TableColumn<ViewProductForAddition, String> productNameView;
    @FXML
    private TableColumn<ViewProductForAddition, Double> buyPriceView;
    @FXML
    private TableColumn<ViewProductForAddition, Integer> quantityView;
    @FXML
    private TableColumn<ViewProductForAddition, Double> subtotalView;
    @FXML
    private TableColumn<ViewProductForAddition, Double> saleTotalView;
    @FXML
    private TableColumn<ViewProductForAddition, Double> totalMarkupView;
    @FXML
    private CheckBox doAccountingInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Legg til varer", "AddProductsScreen");
    }


    @FXML
    public void submitNewProduct(ActionEvent event) {
        if (packQuantityInput.getText().replaceAll("\\s+", "").equals(""))
            packQuantityInput.setText("1");
        if (quantityInput.getText().replaceAll("\\s+", "").equals(""))
            quantityInput.setText("1");

        try {
            String name = productNameInput.getText().trim().replaceAll(",", ".");

            // If product exists in storage with a similar name, replace name
            Product searchForInStorage = kernel.getStorage().findIgnoreCase(name);
            if (searchForInStorage != null) {
                name = searchForInStorage.getName();
            }

            // Fuzzy search for possible duplicates
            Set<String> finds = kernel.getCategories().fuzzyFind(name);
            if (!finds.isEmpty()) {
                Optional<String> newName = new ConfirmFuzzyProductFind(name, finds).showAndWait();
                if (newName.isEmpty()) return;
                name = newName.get();
            }

            double buyPrice = Double.parseDouble(buyPriceInput.getText().replaceAll(",", "."));
            int packQuantity = packQuantityInput.getText().equals("") ? 1 : Integer.parseInt(packQuantityInput.getText());
            double salePrice = Double.parseDouble(salePriceInput.getText().replaceAll(",", "."));
            int numPacks = Integer.parseInt(quantityInput.getText());

            ViewProductForAddition p = new ViewProductForAddition(
                name,
                buyPrice / packQuantity,
                packQuantity * numPacks,
                buyPrice * numPacks,
                salePrice,
                (salePrice * packQuantity - buyPrice) * numPacks,
                addPantInput.isSelected()
            );

            if (!kernel.getCategories().containsRuleFor(name)) {
                Optional<CategoryDict.Category> category = new NewCategoryRulePrompt().showAndWait();
                if (category.isPresent()) {
                    kernel.getCategories().newCategoryRule(name, category.get());
                    Kernel.getLogger().log("New category rule added for " + name + " -> " + category.get());
                }
            }

            cartTableView.getItems().add(p);
        } catch (NumberFormatException e) {
            Kernel.getLogger().log(e);
        }
    }


    @FXML
    public void deleteSelectedItem(ActionEvent event) {
        try {
            cartTableView.getItems().remove(cartTableView.getSelectionModel().getSelectedItem());
        } catch (NullPointerException ignored) {
            // Happens only if no element is selected.
        }
    }


    @FXML
    public void processAllAndExit(ActionEvent event) {
        if (cartTableView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ingen produkter å legge til");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Har lagt til alle produktene?");
        confirmation.setHeaderText("Bekreft");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        // Add elements to storage
        int num = 0;
        double totalExpenseAmount = 0;
        double totalPantAmount = 0;

        for (ViewProductForAddition vp : cartTableView.getItems()) {
            for (Product p : vp.generateProducts()) {
                kernel.getStorage().add(p);
                num++;

                totalExpenseAmount += p.getBuyPrice();
                totalPantAmount += p.getPant();
            }
        }

        Kernel.getLogger().log(num + " products added to storage");
        Main.getCurrentAdminController().repaintStorage();
        exit(event);

        if (doAccountingInput.isSelected()) {
            List<Expense.Transaction> initialTransactions = new LinkedList<>();
            initialTransactions.add(new Expense.Transaction(
                    kernel.getAccounts().getStorageAccount(),
                    totalExpenseAmount,
                    Expense.TransactionType.ADD
                )
            );

            if (totalPantAmount > 1e-10) {
                initialTransactions.add(new Expense.Transaction(
                    kernel.getAccounts().getPantAccount(),
                    totalPantAmount,
                    Expense.TransactionType.ADD
                ));
            }

            MakeExpense.createAndDisplayDialog(initialTransactions);
        }
    }


    private void setupTableView() {
        productNameView.setCellValueFactory(
            new PropertyValueFactory<ViewProductForAddition, String>("productName")
        );
        buyPriceView.setCellValueFactory(
            new PropertyValueFactory<ViewProductForAddition, Double>("buyPrice")
        );
        quantityView.setCellValueFactory(
            new PropertyValueFactory<ViewProductForAddition, Integer>("quantity")
        );
        subtotalView.setCellValueFactory(
            new PropertyValueFactory<ViewProductForAddition, Double>("subTotal")
        );
        saleTotalView.setCellValueFactory(
            new PropertyValueFactory<ViewProductForAddition, Double>("saleTotal")
        );
        totalMarkupView.setCellValueFactory(
            new PropertyValueFactory<ViewProductForAddition, Double>("markup")
        );
    }


    private void setupMarkupChioceBox() {
        ArrayList<MarkupLevel> items = new ArrayList<>();
        items.add(new MarkupLevel(0));
        items.add(new MarkupLevel(5));
        items.add(new MarkupLevel(10));
        items.add(new MarkupLevel(15));
        items.add(new MarkupLevel(20));

        markupInput.setItems(FXCollections.observableArrayList(items));
        markupInput.getSelectionModel().select(1);
    }


    private void setupAutoMarkup() {
        markupInput.setOnAction(e -> updateAutoSalePrice());
    }


    public void updateAutoSalePrice() {
        if (addPantInput.isSelected()) {
            addPantInput.setStyle("-fx-base: #c0edcf;");
        } else {
            addPantInput.setStyle("");
        }

        try {
            double pantadd = 0;
            if (addPantInput.isSelected()) pantadd = 2 * BOTTLE_LOSE_RATE;

            int packsize = 1;
            try {
                String rawInput = packQuantityInput.getText();
                if (!rawInput.isEmpty()) packsize = Integer.parseInt(rawInput);
            } catch (NumberFormatException ignored) {
            }

            double singleprice = Double.parseDouble(buyPriceInput.getText()) / packsize;
            double markup = markupInput.getSelectionModel().getSelectedItem().getPercentage() / 100.0;

            salePriceInput.setText(singleprice + singleprice * markup + pantadd + "");
        } catch (Exception ignored) {
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setupMarkupChioceBox();
        setupAutoMarkup();
        setupTableView();

        List<Listable<ViewProduct>> searchHere = new LinkedList<>();
        searchHere.add(kernel.getCategories());
        SuggestionMenu<ViewProduct> suggestionMenu = new SuggestionMenu<>(productNameInput, searchHere);
    }


    private class MarkupLevel {

        private int percentage;


        public MarkupLevel(int percentage) {

            this.percentage = percentage;
        }


        @Override
        public String toString() {
            return percentage + " %";
        }


        public int getPercentage() {
            return percentage;
        }
    }
}
