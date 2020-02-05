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

package com.mathiaslohne.bbdebet2.gui.modelwrappers;

import com.mathiaslohne.bbdebet2.kernel.core.Product;
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
    private boolean hasPant;


    public ViewProductForAddition(String productName, double buyPrice, int quantity, double subTotal, double sellPrice, double markup, boolean hasPant) {
        this.productName = new SimpleStringProperty(productName);
        this.buyPrice = new SimpleDoubleProperty(buyPrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subTotal = new SimpleDoubleProperty(subTotal);
        this.saleTotal = new SimpleDoubleProperty(sellPrice * quantity);
        this.markup = new SimpleDoubleProperty(markup);
        this.sellPrice = sellPrice;
        this.hasPant = hasPant;
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

        if (hasPant) list.forEach(product -> product.setPant(2));

        return list;
    }
}
