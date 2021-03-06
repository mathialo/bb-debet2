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

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.accounting.Expense;
import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class ViewExpense {
    private final SimpleStringProperty from;
    private final SimpleStringProperty to;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty comment;
    private final SimpleStringProperty expenceDate;

    private final Expense expense;

    public ViewExpense(Expense expense) {
        this.expense = expense;
        this.from = new SimpleStringProperty(expense.getFrom().toString());
        this.to = new SimpleStringProperty(expense.getTo().toString());
        this.amount = new SimpleStringProperty(CurrencyFormatter.format(expense.getAmount()));
        this.comment = new SimpleStringProperty(expense.getComment());
        this.expenceDate = new SimpleStringProperty(Kernel.dateTimeFormat.format(new Date(expense.getTimestamp() * 1000L)));
    }


    public String getTo() {
        return to.get();
    }


    public SimpleStringProperty toProperty() {
        return to;
    }


    public String getExpenceDate() {
        return expenceDate.get();
    }


    public SimpleStringProperty expenceDateProperty() {
        return expenceDate;
    }


    public String getFrom() {
        return from.get();
    }


    public SimpleStringProperty fromProperty() {
        return from;
    }


    public String getAmount() {
        return amount.get();
    }


    public SimpleStringProperty amountProperty() {
        return amount;
    }


    public String getComment() {
        return comment.get();
    }


    public SimpleStringProperty commentProperty() {
        return comment;
    }


    public Expense getExpenceObject() {
        return expense;
    }
}
