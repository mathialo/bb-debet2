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

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.accounting.Expence;
import bbdebet2.kernel.datastructs.CurrencyFormatter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class ViewExpence {

    private final SimpleStringProperty account;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty comment;

    private final Expence expence;

    public ViewExpence(Expence expence) {
        this.expence = expence;
        this.account = new SimpleStringProperty(expence.getTo().toString());
        this.amount = new SimpleStringProperty(CurrencyFormatter.format(expence.getAmount()));
        this.comment = new SimpleStringProperty(expence.getComment());
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


    public String getComment() {
        return comment.get();
    }


    public SimpleStringProperty commentProperty() {
        return comment;
    }


    public Expence getExpenceObject() {
        return expence;
    }
}
