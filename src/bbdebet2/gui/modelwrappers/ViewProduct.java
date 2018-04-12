/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.Product;
import javafx.beans.property.SimpleStringProperty;

public class ViewProduct {

    private final SimpleStringProperty productName;
    private final SimpleStringProperty sellPrice;
    private final SimpleStringProperty buyPrice;

    private Product productObject;


    public ViewProduct(Product product) {
        this.productName = new SimpleStringProperty(product.getName());
        this.sellPrice = new SimpleStringProperty(CurrencyFormatter.format(product.getSellPrice()));
        this.buyPrice = new SimpleStringProperty(CurrencyFormatter.format(product.getBuyPrice()));

        this.productObject = product;
    }


    public String getProductName() {
        return productName.get();
    }


    public SimpleStringProperty productNameProperty() {
        return productName;
    }


    public String getSellPrice() {
        return sellPrice.get();
    }


    public SimpleStringProperty sellPriceProperty() {
        return sellPrice;
    }


    public String getBuyPrice() {
        return buyPrice.get();
    }


    public SimpleStringProperty buyPriceProperty() {
        return buyPrice;
    }


    public Product getProductObject() {
        return productObject;
    }


    @Override
    public String toString() {
        return productName.get();
    }
}
