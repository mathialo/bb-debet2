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

import java.util.Date;


public class Sale {

    private static long counter;

    private long id;
    private long timestamp;
    private String userName;
    private String productName;
    private double pricePayed;
    private double earnings;


    Sale(long id, long timestamp, String userName, String productName, double pricePayed, double earnings) {
        this.id = id;
        this.timestamp = timestamp;
        this.userName = userName;
        this.productName = productName;
        this.pricePayed = pricePayed;
        this.earnings = earnings;
        if (id > counter) counter = id + 1;
    }


    public Sale(User user, Product product) {
        timestamp = System.currentTimeMillis() / 1000L;
        id = counter++;

        userName = user.getUserName();
        productName = product.getName();
        pricePayed = product.getSellPrice();
        earnings = product.getMarkup();
    }


    public static long getCounter() {
        return counter;
    }


    public static void setCounter(long counter) {
        Sale.counter = counter;
    }


    public boolean madeByUser(User user) {
        return user.getUserName().equals(userName);
    }


    public long getTimestamp() {
        return timestamp;
    }


    public long getId() {
        return id;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getProductName() {
        return productName;
    }


    public Product getProduct() {
        return new Product(productName, pricePayed, pricePayed - earnings);
    }


    public double getPricePayed() {
        return pricePayed;
    }


    public double getEarnings() {
        return earnings;
    }


    public String getFormattedTimestamp() {
        return Kernel.dateTimeFormat.format(new Date(timestamp * 1000L));
    }


    @Override
    public String toString() {
        return getFormattedTimestamp() + ":    " + productName + " (" + CurrencyFormatter.format(
            pricePayed) + ")";
    }
}
