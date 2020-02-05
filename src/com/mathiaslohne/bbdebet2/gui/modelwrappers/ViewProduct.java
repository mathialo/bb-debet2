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

import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
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
