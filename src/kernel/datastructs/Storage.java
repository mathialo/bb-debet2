/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;


import kernel.Kernel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Storage implements Exportable {

    private LinkedList<PriorityQueue<Product>> storage;


    public Storage() {
        storage = new LinkedList<PriorityQueue<Product>>();
    }


    public Storage(File file) throws IOException, ErrorInFileException {
        this();

        Scanner sc = new Scanner(file);

        // Skip header line
        sc.nextLine();

        int linenum = 2;
        while (sc.hasNextLine()) {
            try {
                String rawLine = sc.nextLine();

                // skip empty lines
                if (rawLine.equals("")) continue;

                String[] line = rawLine.split("\\s*,\\s*");

                add(new Product(line[0], Double.parseDouble(line[1]), Double.parseDouble(line[2])));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new ErrorInFileException("Error in storage file on line " + linenum);
            }
        }

        sc.close();
    }


    @Override
    public void saveFile(File file) throws IOException {
        PrintWriter pw = new PrintWriter(file);

        pw.println("Product,BuyPrice,SellPrice");

        for (PriorityQueue<Product> q : storage) {
            for (Product p : q) {
                pw.println(p.getName() + "," + p.getBuyPrice() + "," + p.getSellPrice());
            }
        }

        pw.close();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.STORAGE_FILEPATH));
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


    public int getNum(Product product) {
        return getNum(product.getName());
    }


    public int getNum(String productName) {
        for (PriorityQueue<Product> q : storage) {
            if (q.peek().getName().equals(productName)) {
                return q.size();
            }
        }

        return 0;
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
