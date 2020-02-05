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

package com.mathiaslohne.bbdebet2.kernel.core;

public class ProductQuery {

    private boolean anyName = false;
    private boolean anyPrice = false;

    private boolean changeName = false;
    private boolean changePrice = false;

    private String name;
    private double price;


    public void setAnyName() {
        anyName = true;
    }


    public void setAnyPrice() {
        anyPrice = true;
    }


    public void setChangeName() {
        this.changeName = true;
    }


    public void setChangePrice() {
        this.changePrice = true;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setPrice(double price) {
        this.price = price;
    }


    private boolean doubleEqual(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-7;
    }


    public boolean match(Product product) {
        boolean[] results = new boolean[2];

        results[0] = anyName || product.getName().equals(name);
        results[1] = anyPrice || doubleEqual(product.getSellPrice(), price);

        return results[0] && results[1];
    }


    public boolean changeName() {
        return changeName;
    }


    public boolean changePrice() {
        return changePrice;
    }
}
