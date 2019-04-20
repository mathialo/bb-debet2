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

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.Sale;
import javafx.beans.property.SimpleStringProperty;

public class ViewSale {

    private final SimpleStringProperty saleDate;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty price;
    private Sale saleObject;


    public ViewSale(Sale sale) {
        this.saleDate = new SimpleStringProperty(sale.getFormattedTimestamp());
        this.userName = new SimpleStringProperty(sale.getUserName());
        this.productName = new SimpleStringProperty(sale.getProductName());
        this.price = new SimpleStringProperty(CurrencyFormatter.format(sale.getPricePayed()));

        this.saleObject = sale;
    }


    public String getUserName() {
        return userName.get();
    }


    public SimpleStringProperty userNameProperty() {
        return userName;
    }


    public Sale getSaleObject() {
        return saleObject;
    }


    public String getSaleDate() {
        return saleDate.get();
    }


    public SimpleStringProperty saleDateProperty() {
        return saleDate;
    }


    public String getProductName() {
        return productName.get();
    }


    public SimpleStringProperty productNameProperty() {
        return productName;
    }


    public String getPrice() {
        return price.get();
    }


    public SimpleStringProperty priceProperty() {
        return price;
    }
}
