/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

public class User {

    private static int counter;
    private String userName;
    private String mail;
    private int id;
    private double balance;


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
    User(String userName, String mail, int id) {
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
