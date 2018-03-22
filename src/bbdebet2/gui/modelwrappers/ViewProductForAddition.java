/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.datastructs.Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.LinkedList;
import java.util.List;

public class ViewProductForAddition {

    private final SimpleStringProperty productName;
    private final SimpleDoubleProperty buyPrice;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty subTotal;
    private final SimpleDoubleProperty saleTotal;
    private final SimpleDoubleProperty markup;

    private double sellPrice;


    public ViewProductForAddition(String productName, double buyPrice, int quantity, double subTotal, double sellPrice, double markup) {
        this.productName = new SimpleStringProperty(productName);
        this.buyPrice = new SimpleDoubleProperty(buyPrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subTotal = new SimpleDoubleProperty(subTotal);
        this.saleTotal = new SimpleDoubleProperty(sellPrice * quantity);
        this.markup = new SimpleDoubleProperty(markup);
        this.sellPrice = sellPrice;
    }


    public String getProductName() {
        return productName.get();
    }


    public void setProductName(String productName) {
        this.productName.set(productName);
    }


    public SimpleStringProperty productNameProperty() {
        return productName;
    }


    public double getBuyPrice() {
        return buyPrice.get();
    }


    public void setBuyPrice(double buyPrice) {
        this.buyPrice.set(buyPrice);
    }


    public SimpleDoubleProperty buyPriceProperty() {
        return buyPrice;
    }


    public int getQuantity() {
        return quantity.get();
    }


    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }


    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }


    public double getSubTotal() {
        return subTotal.get();
    }


    public void setSubTotal(double subTotal) {
        this.subTotal.set(subTotal);
    }


    public SimpleDoubleProperty subTotalProperty() {
        return subTotal;
    }


    public double getSaleTotal() {
        return saleTotal.get();
    }


    public void setSaleTotal(double saleTotal) {
        this.saleTotal.set(saleTotal);
    }


    public SimpleDoubleProperty saleTotalProperty() {
        return saleTotal;
    }


    public double getMarkup() {
        return markup.get();
    }


    public void setMarkup(double markup) {
        this.markup.set(markup);
    }


    public SimpleDoubleProperty markupProperty() {
        return markup;
    }


    public List<Product> generateProducts() {
        LinkedList<Product> list = new LinkedList<>();

        for (int i = 0; i < getQuantity(); i++) {
            list.add(new Product(getProductName(), sellPrice, getBuyPrice()));
        }

        return list;
    }
}
