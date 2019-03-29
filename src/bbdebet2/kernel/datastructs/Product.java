/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

public class Product implements Comparable<Product> {

    private String name;
    private double sellPrice;
    private double buyPrice;

    private boolean custom;


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
        String prefix = "";
        if (custom) prefix = "Annet: ";
        return prefix + name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public double getSellPrice() {
        return sellPrice;
    }


    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
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

    public boolean equals(Product o) {
        return name.equals(o.getName());
    }

    @Override
    public int compareTo(Product o) {
        if (sellPrice >= o.getSellPrice()) return 1;
        else return -1;
    }


    @Override
    public String toString() {
        String prefix = "";
        if (custom) prefix = "Annet: ";
        return prefix + name + ", " + CurrencyFormatter.format(sellPrice);
    }
}
