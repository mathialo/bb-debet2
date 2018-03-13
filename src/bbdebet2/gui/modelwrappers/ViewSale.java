/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.datastructs.Sale;
import javafx.beans.property.SimpleStringProperty;

public class ViewSale {
    public final SimpleStringProperty saleDate;
    public final SimpleStringProperty productName;
    public final SimpleStringProperty price;


    public ViewSale(Sale sale) {
        this.saleDate = new SimpleStringProperty(sale.getFormattedTimestamp());
        this.productName = new SimpleStringProperty(sale.getProductName());
        this.price = new SimpleStringProperty(sale.getFormattedPrice());
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
