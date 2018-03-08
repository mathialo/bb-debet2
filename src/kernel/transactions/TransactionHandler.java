/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.transactions;

import kernel.Kernel;
import kernel.datastructs.Product;
import kernel.datastructs.Sale;
import kernel.datastructs.User;

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
}
