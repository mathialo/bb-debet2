/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sale {

    private static int counter;

    private static NumberFormat formatter = new DecimalFormat("#0.00");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy - HH:mm");

    private int id;
    private long timestamp;
    private String userName;
    private String productName;
    private double pricePayed;
    private double earnings;


    public Sale(int id, long timestamp, String userName, String productName, double pricePayed, double earnings) {
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


    public static int getCounter() {
        return counter;
    }


    public static void setCounter(int counter) {
        Sale.counter = counter;
    }


    public boolean madeByUser(User user) {
        return user.getUserName().equals(userName);
    }


    public long getTimestamp() {
        return timestamp;
    }


    public int getId() {
        return id;
    }


    public String getUserName() {
        return userName;
    }


    public String getProductName() {
        return productName;
    }


    public double getPricePayed() {
        return pricePayed;
    }


    public double getEarnings() {
        return earnings;
    }


    public String getFormattedTimestamp() {
        return dateFormat.format(new Date(timestamp * 1000L));
    }


    public String getFormattedPrice() {
        return formatter.format(pricePayed) + " kr";
    }


    @Override
    public String toString() {
        return getFormattedTimestamp() + ":    " + productName + " (" + getFormattedPrice() + ")";
    }
}
