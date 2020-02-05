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

package com.mathiaslohne.bbdebet2.kernel.core;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.CurrencyFormatter;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import com.mathiaslohne.bbdebet2.kernel.core.Sale;
import com.mathiaslohne.bbdebet2.kernel.core.User;
import com.mathiaslohne.bbdebet2.kernel.logging.CsvLogger;

import java.io.IOException;

public class TransactionHandler {

    private Kernel kernel;


    public TransactionHandler(Kernel kernel) {
        this.kernel = kernel;
    }


    public void newPurchase(User user, Product product) {
        // do transaction
        user.subtractBalance(product.getSellPrice());

        // log transaction
        Kernel.getLogger().log("New purchase from " + user + ": " + product);
        kernel.getSalesHistory().add(new Sale(user, product));
    }


    private void restock(Sale sale) {
        // Check if product is a custom one, and ad it back if it is not
        if (!sale.getProductName().startsWith("Annet:")) {
            Product product = new Product(
                sale.getProductName(), sale.getPricePayed(),
                sale.getPricePayed() - sale.getEarnings()
            );

            // Add product back to storage
            kernel.getStorage().add(product);
        }
    }

    private void giveMoneyBack(Sale sale) {
        // If sale was made by glass user, there's no user to refund money to
        if (sale.getUserName().equals(kernel.getSettingsHolder().getGlasUserName())) return;

        // Get user and product
        User user = kernel.getUserList().find(sale.getUserName());
        if (user == null) {
            throw new RuntimeException("User " + sale.getUserName() + " not found in UserList");
        }

        // Give user money back
        user.addBalance(sale.getPricePayed());
    }

    public void refund(Sale sale) {
        restock(sale);
        giveMoneyBack(sale);
        kernel.getSalesHistory().remove(sale.getId());

        // Log transaction
        Kernel.getLogger().log("Refunded sale '" + sale + "' for " + sale.getUserName());
    }


    public void newMoneyInsert(User user, double amount) {
        user.addBalance(amount);
        Kernel.getLogger().log("Added " + CurrencyFormatter.format(amount) + " to user " + user);

        try {
            CsvLogger.addMoneyInserts(user, amount);
        } catch (IOException e) {
            Kernel.getLogger().log(e);
        }

    }


    public void newUserTransaction(User from, User to, double amount) {
        // process transaction
        to.addBalance(amount);
        from.subtractBalance(amount);

        // log transaction
        try {
            Kernel.getLogger().log(
                "Transfering " + amount + kernel.getSettingsHolder().getCurrencySign() + " from " + from + " to " + to
            );
            CsvLogger.addUserTransaction(from, to, amount);
        } catch (IOException e) {
            Kernel.getLogger().log(e);
        }
    }
}
