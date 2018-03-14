/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.datastructs.User;
import javafx.beans.property.SimpleStringProperty;

public class ViewUser {

    private final SimpleStringProperty userId;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty mail;
    private final SimpleStringProperty balance;

    private User userObject;


    public ViewUser(User user) {
        this.userId = new SimpleStringProperty(user.getId() + "");
        this.userName = new SimpleStringProperty(user.getUserName());
        this.mail = new SimpleStringProperty(user.getMail());
        this.balance = new SimpleStringProperty(user.getFormatedBalance());

        this.userObject = user;
    }


    @Override
    public String toString() {
        return userName.get();
    }


    public String getUserId() {
        return userId.get();
    }


    public SimpleStringProperty userIdProperty() {
        return userId;
    }


    public String getUserName() {
        return userName.get();
    }


    public SimpleStringProperty userNameProperty() {
        return userName;
    }


    public String getMail() {
        return mail.get();
    }


    public SimpleStringProperty mailProperty() {
        return mail;
    }


    public String getBalance() {
        return balance.get();
    }


    public SimpleStringProperty balanceProperty() {
        return balance;
    }


    public User getUserObject() {
        return userObject;
    }
}
