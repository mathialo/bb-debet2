/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.transactions;

import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.Sale;
import bbdebet2.kernel.datastructs.User;

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


    public void refund(Sale sale) {
        // Get user and product
        User user = kernel.getUserList().find(sale.getUserName());
        if (user == null) {
            throw new RuntimeException("User " + sale.getUserName() + " not found in UserList");
        }

        Product product = new Product(sale.getProductName(), sale.getPricePayed(), sale.getPricePayed() - sale.getEarnings());

        // Give user money back
        user.addBalance(sale.getPricePayed());

        // Add product back to storage
        kernel.getStorage().add(product);

        // Log transaction
        kernel.getLogger().log("Refunded sale '" + sale + "' for " + user);
    }

    public void newUserTransaction(User from, User to, double amount) {

    }
}
