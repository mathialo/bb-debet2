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

import com.mathiaslohne.bbdebet2.kernel.accounting.Expense;
import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import javafx.beans.property.SimpleStringProperty;


public class ViewTransactionForAddition {

    private final SimpleStringProperty account;
    private final SimpleStringProperty amount;

    private final Expense.Transaction transaction;


    public ViewTransactionForAddition(Expense.Transaction transaction) {
        this.transaction = transaction;
        this.account = new SimpleStringProperty(transaction.getAccount().toString());
        this.amount = new SimpleStringProperty(CurrencyFormatter.format(transaction.getAmount()));
    }


    public String getAccount() {
        return account.get();
    }


    public SimpleStringProperty accountProperty() {
        return account;
    }


    public String getAmount() {
        return amount.get();
    }


    public SimpleStringProperty amountProperty() {
        return amount;
    }


    public Expense.Transaction getTransactionObject() {
        return transaction;
    }
}
