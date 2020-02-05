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

package com.mathiaslohne.bbdebet2.gui.modelwrappers;

import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import javafx.beans.property.SimpleStringProperty;

public class ViewUser {

    private final SimpleStringProperty userId;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty mail;
    private final SimpleStringProperty balance;
    private final SimpleStringProperty acceptedEula;
    private User userObject;


    public ViewUser(User user) {
        this.userId = new SimpleStringProperty(user.getId() + "");
        this.userName = new SimpleStringProperty(user.getUserName());
        this.mail = new SimpleStringProperty(user.getMail());
        this.balance = new SimpleStringProperty(CurrencyFormatter.format(user.getBalance()));
        String acceptedEulaText = "Nei";
        if (user.hasAcceptedEula()) acceptedEulaText = "Ja";
        this.acceptedEula = new SimpleStringProperty(acceptedEulaText);

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


    public String getAcceptedEula() {
        return acceptedEula.get();
    }


    public SimpleStringProperty acceptedEulaProperty() {
        return acceptedEula;
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
