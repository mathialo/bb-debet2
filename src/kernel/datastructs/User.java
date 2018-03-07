/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class User {
    private String fullName;
    private String userName;
    private String mail;

    private int id;
    private static int counter;

    private double balance;

    private NumberFormat formatter;

    /**
     * Initializes new user with automatic id generation
     * @param fullName  User's real name
     * @param userName  Username
     * @param mail      Email address
     */
    public User(String fullName, String userName, String mail) {
        this(fullName, userName, mail, counter++);
    }

    /**
     * Initializes new user with specified id
     * @param fullName  User's real name
     * @param userName  Username
     * @param mail      Email address
     * @param id        ID of user
     */
    User(String fullName, String userName, String mail, int id) {
        this.fullName = fullName;
        this.userName = userName;
        this.mail = mail;

        this.id = id;
        balance = 0;

        formatter = new DecimalFormat("#0.00");
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getMail() {
        return mail;
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public String getFormatedBalance() {
        return formatter.format(balance) + " kr";
    }

    public double subtractBalance(double amount) {
        balance -= amount;
        return balance;
    }

    public double addBalance(double amount) {
        balance += amount;
        return balance;
    }

    @Override
    public String toString() {
        return userName + "(" + id + ")";

    }
}
