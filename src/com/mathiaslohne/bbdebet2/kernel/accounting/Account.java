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

package com.mathiaslohne.bbdebet2.kernel.accounting;

import java.util.Objects;


public class Account implements Comparable<Account> {

    private String name;
    private int number;

    private boolean paymentOption;
    private boolean insertOption;


    public Account(String name, int number, boolean paymentOption, boolean insertOption) {
        this.name = name;
        this.number = number;
        this.paymentOption = paymentOption;
        this.insertOption = insertOption;
    }


    public Account(String name, int number) {
        this(name, number, false, false);
    }


    public boolean isInsertOption() {
        return insertOption;
    }


    public void setInsertOption(boolean insertOption) {
        this.insertOption = insertOption;
    }


    public boolean isPaymentOption() {
        return paymentOption;
    }


    public void setPaymentOption(boolean paymentOption) {
        this.paymentOption = paymentOption;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getNumber() {
        return number;
    }


    public void setNumber(int number) {
        this.number = number;
    }


    @Override
    public int compareTo(Account account) {
        return number - account.getNumber();
    }


    @Override
    public String toString() {
        return number + ": " + name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return number == account.number;
    }


    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
