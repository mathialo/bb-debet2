/*
 * Copyright (C) 2020  Mathias Lohne
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

import com.mathiaslohne.bbdebet2.kernel.core.Product;


public class LossEntry {

    private Long timestamp;
    private String productName;
    private int number;
    private double amount;


    public LossEntry(Product product, int number) {
        timestamp = System.currentTimeMillis() / 1000L;
        productName = product.getName();
        amount = product.getBuyPrice() * number;
        this.number = number;
    }


    LossEntry(Long timestamp, String productName, int number, double amount) {
        this.timestamp = timestamp;
        this.productName = productName;
        this.number = number;
        this.amount = amount;
    }


    public Long getTimestamp() {
        return timestamp;
    }


    public String getProductName() {
        return productName;
    }


    public int getNumber() {
        return number;
    }


    public double getAmount() {
        return amount;
    }
}
