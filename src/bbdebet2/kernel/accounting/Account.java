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

package bbdebet2.kernel.accounting;

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

    public boolean isPaymentOption() {
        return paymentOption;
    }


    public String getName() {
        return name;
    }


    public int getNumber() {
        return number;
    }


    @Override
    public int compareTo(Account account) {
        return number - account.getNumber();
    }


    @Override
    public String toString() {
        return number + ": " + name;
    }
}
