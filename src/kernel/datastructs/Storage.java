/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;


import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

public class Storage {

    private LinkedList<PriorityQueue<Product>> storage;

    public Storage() {
        storage = new LinkedList<PriorityQueue<Product>>();
    }

    public void add(Product p) {
        for (PriorityQueue<Product> q : storage) {
            // Search for other products with the same name
            if (q.peek().getName().equals(p.getName())) {

                // Add instance to product list
                q.add(p);
                return;
            }
        }

        // No matching product entry, create new p-queue for the new product
        PriorityQueue<Product> q = new PriorityQueue<Product>();
        q.add(p);
        storage.add(q);
    }

    public Product get(String productName) {
        for (PriorityQueue<Product> q : storage) {
            // Search for other products with the same name
            if (q.peek().getName().equals(productName)) {

                // Add instance to product list
                return q.poll();
            }
        }

        // No entry for requested product, return null
        return null;
    }

    public Set<Product> getProductSet() {
        TreeSet<Product> productSet = new TreeSet<Product>(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (PriorityQueue<Product> q : storage) {
            productSet.add(q.peek());
        }

        return productSet;
    }
}
