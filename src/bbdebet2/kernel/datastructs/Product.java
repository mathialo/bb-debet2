/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Product implements Comparable<Product> {

    private String name;
    private double sellPrice;
    private double buyPrice;

    private boolean custom;

    private static NumberFormat formatter = new DecimalFormat("#0.00");

    public Product(String name, double sellPrice, double buyPrice) {
        this.name = name;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
    }

    public Product(String name, double sellPrice, double buyPrice, boolean custom) {
        this(name, sellPrice, buyPrice);
        this.custom = custom;
    }


    public String getName() {
        return name;
    }


    public double getSellPrice() {
        return sellPrice;
    }


    public double getBuyPrice() {
        return buyPrice;
    }


    public double getMarkup() {
        return sellPrice - buyPrice;
    }


    public double getMarkupRate() {
        return sellPrice / buyPrice;
    }

    public boolean isCustom() {
        return custom;
    }


    public String getFormattedSellPrice() {
        return formatter.format(sellPrice) + " kr";
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;

    }


    public String getFormattedBuyPrice() {
        return formatter.format(buyPrice)+ " kr";
    }


    @Override
    public int compareTo(Product o) {
        if (sellPrice >= o.getSellPrice()) return 1;
        else return -1;
    }

    @Override
    public String toString() {
        return name + ", " + getFormattedSellPrice();
    }
}
