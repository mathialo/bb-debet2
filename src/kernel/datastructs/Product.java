/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

public class Product implements Comparable<Product> {
    private String name;
    private double sellPrice;
    private double buyPrice;

    public Product(String name, double sellPrice, double buyPrice) {
        this.name = name;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
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

    @Override
    public int compareTo(Product o) {
        if (sellPrice >= o.getSellPrice()) return 1;
        else return -1;
    }
}
