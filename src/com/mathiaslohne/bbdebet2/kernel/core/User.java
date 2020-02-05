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

public class User {

    private static int counter;
    private String userName;
    private String mail;
    private int id;
    private double balance;
    private boolean acceptedEula;


    /**
     * Initializes new user with automatic id generation
     *
     * @param userName Username
     * @param mail     Email address
     */
    public User(String userName, String mail) {
        this(userName, mail, counter++);
    }


    /**
     * Initializes new user with specified id
     *
     * @param userName Username
     * @param mail     Email address
     * @param id       ID of user
     */
    public User(String userName, String mail, int id) {
        this.userName = userName;
        this.mail = mail;

        this.id = id;
        balance = 0;
    }


    protected static int getCounter() {
        return counter;
    }


    protected static void setCounter(int counter) {
        User.counter = counter;
    }


    public boolean hasAcceptedEula() {
        return acceptedEula;
    }


    public void acceptEula() {
        acceptedEula = true;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getMail() {
        return mail;
    }


    public void setMail(String mail) {
        this.mail = mail;
    }


    public int getId() {
        return id;
    }


    public double getBalance() {
        return balance;
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
