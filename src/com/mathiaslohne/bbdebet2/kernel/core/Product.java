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

package com.mathiaslohne.bbdebet2.kernel.core;

public class Product implements Comparable<Product> {

    private String name;
    private double sellPrice;
    private double buyPrice;

    private double pant;

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


    public double getPant() {
        return pant;
    }


    public void setPant(double pant) {
        this.pant = pant;
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
