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

import java.util.LinkedList;
import java.util.List;


public class Expense implements Comparable<Expense> {

    static int counter;

    private long timestamp;
    private String comment;
    private int id;

    private List<Transaction> from;
    private List<Transaction> to;


    Expense(String comment, long timestamp, int id) {
        this.comment = comment != null ? comment.replaceAll(",", "") : "";
        this.timestamp = timestamp;
        this.id = id;

        from = new LinkedList<>();
        to = new LinkedList<>();
    }


    public Expense(String comment) {
        this(comment, System.currentTimeMillis() / 1000, counter++);
    }


    public Expense addTransaction(Transaction transaction) {
        if (transaction.getType() == TransactionType.ADD) {
            to.add(transaction);
        } else {
            from.add(transaction);
        }
        return this;
    }


    public List<Transaction> getFrom() {
        return from;
    }


    public List<Transaction> getTo() {
        return to;
    }

    public double getAmount() {
        return getFrom().stream().mapToDouble(Transaction::getAmount).sum();
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
        return String.format("Expense %d", id);
    }


    @Override
    public int compareTo(Expense expense) {
        return (int) (timestamp - expense.getTimestamp());
    }


    public enum TransactionType {
        ADD("ADD"), SUB("SUB");

        private String typeString;


        TransactionType(String typeString) {
            this.typeString = typeString;
        }


        @Override
        public String toString() {
            return typeString;
        }
    }


    public static class Transaction {

        private Account account;
        private double amount;
        private TransactionType type;


        public Transaction(Account account, double amount, TransactionType type) {
            this.account = account;
            this.amount = amount;
            this.type = type;
        }


        public Account getAccount() {
            return account;
        }


        public double getAmount() {
            return amount;
        }


        public TransactionType getType() {
            return type;
        }

        @Override
        public String toString() {
            return account.toString();
        }
    }
}
