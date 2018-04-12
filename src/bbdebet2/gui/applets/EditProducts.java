/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.ProductQuery;
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
            newName = newProductNameInput.getText();
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
            newPrice = Double.parseDouble(newProductPriceInput.getText());
        }

        kernel.getStorage().editProducts(query, newName, newPrice);
        Main.getCurrentAdminController().repaintStorage();
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
