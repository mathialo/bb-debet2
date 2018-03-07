/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sale {

    private static int counter;
    private long timestamp;
    private int id;
    private String userName;
    private String productName;

    private double pricePayed;
    private double earnings;

    private static NumberFormat formatter = new DecimalFormat("#0.00");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy - HH:mm");


    public Sale(long timestamp, int id, String userName, String productName, double pricePayed, double earnings) {
        this.timestamp = timestamp;
        this.id = id;
        this.userName = userName;
        this.productName = productName;
        this.pricePayed = pricePayed;
        this.earnings = earnings;
        if (id > counter) counter = id+1;
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


    @Override
    public String toString() {
        return dateFormat.format(new Date(timestamp*1000L)) + ":    " + productName + " (" + formatter.format(pricePayed) + " kr)";
    }

    public String toLongString() {
        return  "(" + dateFormat.format(new Date(timestamp*1000L)) + ") " + userName + " kjopte " + productName + " for " + formatter.format(pricePayed) + " kr";
    }
}
