/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.transactions;

import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.Sale;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.logging.CsvLogger;

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
        kernel.getLogger().log("New purchase from " + user + ": " + product);
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
        kernel.getLogger().log("Refunded sale '" + sale + "' for " + sale.getUserName());
    }


    public void newMoneyInsert(User user, double amount) {
        user.addBalance(amount);
        kernel.getLogger().log("Added " + amount + kernel.getSettingsHolder().getCurrencySign() + " to user " + user);

        try {
            CsvLogger.addMoneyInserts(user, amount);
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }

    }


    public void newUserTransaction(User from, User to, double amount) {
        // process transaction
        to.addBalance(amount);
        from.subtractBalance(amount);

        // log transaction
        try {
            kernel.getLogger().log(
                "Transfering " + amount + kernel.getSettingsHolder().getCurrencySign() + " from " + from + " to " + to
            );
            CsvLogger.addUserTransaction(from, to, amount);
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }
    }
}
