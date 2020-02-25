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
import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import com.mathiaslohne.bbdebet2.kernel.core.ProductQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditProducts extends Applet {

    @FXML
    private ChoiceBox<ProductNameView> productNameInput;
    @FXML
    private ChoiceBox<ProductPriceView> productPriceInput;
    @FXML
    private TextField newProductNameInput;
    @FXML
    private TextField newProductPriceInput;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Endre produkter", "EditProductsView");
    }


    @FXML
    public void editProductsAndClose(ActionEvent event) {
        // Build a query
        ProductQuery query = new ProductQuery();

        // Default new stuff. Will be overwritten if query matches with it.
        String newName = null;
        double newPrice = 0;

        ProductNameView selectedName = productNameInput.getSelectionModel().getSelectedItem();

        if (selectedName.getProduct() == null) {
            // match all
            query.setAnyName();
        } else {
            query.setName(selectedName.getProduct().getName());
        }

        if (!newProductNameInput.getText().equals("UENDRET")) {
            query.setChangeName();
            newName = newProductNameInput.getText().replaceAll(",", ".");
        }

        ProductPriceView selectedPrice = productPriceInput.getSelectionModel().getSelectedItem();

        if (selectedPrice.getProduct() == null) {
            // match all
            query.setAnyPrice();
        } else {
            query.setPrice(selectedPrice.getProduct().getSellPrice());
        }

        if (!newProductPriceInput.getText().equals("UENDRET")) {
            query.setChangePrice();
            newPrice = Double.parseDouble(newProductPriceInput.getText().replaceAll(",", "."));
        }

        kernel.getStorage().editProducts(query, newName, newPrice);
        if (query.changeName()) {
            String finalNewName = newName;
            kernel.getSalesHistory().forEach(sale -> sale.setProductName(finalNewName));
        }

        Main.getCurrentAdminController().repaintStorage();
        Main.getCurrentAdminController().repaintSaleHistory();
        exit(event);
    }


    private void initializeChoiceBoxes() {
        // Names
        // Add "all" option
        productNameInput.getItems().add(new ProductNameView(null));

        // Add all products
        for (Product p : kernel.getStorage().getProductSet()) {
            productNameInput.getItems().add(new ProductNameView(p));
        }

        // Select "all" by standard
        productNameInput.getSelectionModel().select(0);

        // Set salePriceInput to automatically update when a new name is selected
        productNameInput.setOnAction(this::updatePriceChoiceBox);

        // Prices
        // Add "all" option
        productPriceInput.getItems().add(new ProductPriceView(null));

        // Select "all" by standard
        productPriceInput.getSelectionModel().select(0);
    }


    private void updatePriceChoiceBox(ActionEvent event) {
        productPriceInput.getItems().clear();

        productPriceInput.getItems().add(new ProductPriceView(null));

        // If "all" is selected, stop
        if (productNameInput.getSelectionModel().getSelectedItem().getProduct() == null) return;

        for (Product p : kernel.getStorage().getSellPriceSet(
            productNameInput.getSelectionModel().getSelectedItem().getProduct())) {
            productPriceInput.getItems().add(new ProductPriceView(p));
        }

        productPriceInput.getSelectionModel().select(0);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeChoiceBoxes();
    }


    private abstract class ProductView {

        protected Product product;


        public ProductView(Product product) {
            this.product = product;
        }


        public Product getProduct() {
            return product;
        }


        protected abstract String getProperty();


        @Override
        public String toString() {
            if (product == null) {
                return "ALLE";
            } else {
                return getProperty();
            }
        }
    }


    private class ProductNameView extends ProductView {

        public ProductNameView(Product product) {
            super(product);
        }


        @Override
        protected String getProperty() {
            return product.getName();
        }
    }


    private class ProductPriceView extends ProductView {

        public ProductPriceView(Product product) {
            super(product);
        }


        @Override
        protected String getProperty() {
            return CurrencyFormatter.format(product.getSellPrice());
        }
    }
}
