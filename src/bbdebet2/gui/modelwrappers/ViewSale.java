/*
 * Copyright (c) 2018. Mathias Lohne
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
