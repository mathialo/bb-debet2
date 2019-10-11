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

import bbdebet2.kernel.datastructs.CurrencyFormatter;


public class Expence implements Comparable<Expence> {

    static int counter;

    private long timestamp;
    private Account to;
    private Account from;
    private double amount;
    private String comment;
    private int id;


    Expence(Account to, double amount, String comment, long timestamp, int id) {
        this.to = to;
        this.amount = amount;
        this.comment = comment != null ? comment.replaceAll(",", "") : "";
        this.timestamp = timestamp;
        this.id = id;
    }


    public Expence(Account to, double amount, String comment) {
        this(to, amount, comment, System.currentTimeMillis() / 1000, counter++);
    }


    public Expence(Account to, double amount, Expence sibling) {
        this(to, amount, sibling.getComment(), sibling.getTimestamp(), sibling.getId());
    }


    public Expence resolve(Account from) {
        this.from = from;
        return this;
    }


    public Account getTo() {
        return to;
    }


    public Account getFrom() {
        return from;
    }


    public double getAmount() {
        return amount;
    }


    public String getComment() {
        return comment;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return String.format("(%s)->(%s) : %s", from, to, CurrencyFormatter.format(amount));
    }


    @Override
    public int compareTo(Expence expence) {
        return (int) (timestamp - expence.getTimestamp());
    }
}
