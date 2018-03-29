/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

import bbdebet2.gui.modelwrappers.ViewProduct;
import bbdebet2.kernel.Kernel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Storage implements Exportable, Listable<ViewProduct> {

    private LinkedList<PriorityQueue<Product>> storage;


    public Storage() {
        storage = new LinkedList<>();
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

                add(new Product(line[0], Double.parseDouble(line[2]), Double.parseDouble(line[1])));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new ErrorInFileException("Error in storage file on line " + linenum);
            }
        }

        sc.close();
    }


    public void saveFile(String filepath) throws IOException {
        saveFile(new File(filepath));
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
        PriorityQueue<Product> q = new PriorityQueue<>();
        q.add(p);
        storage.add(q);
    }


    public Product get(String productName) {
        for (PriorityQueue<Product> q : storage) {
            // Search for other products with the same name
            if (q.peek().getName().equals(productName)) {

                // Remove from storage
                Product p = q.poll();

                // Remove type if there are no more instances of this type
                if (q.size() == 0) storage.remove(q);

                return p;
            }
        }

        // No entry for requested product, return null
        return null;
    }


    public Product get(Product product) {
        return get(product.getName());
    }


    public Product find(String productName) {
        for (PriorityQueue<Product> q : storage) {
            // Search for other products with the same name
            if (q.peek().getName().equals(productName)) {

                // Add instance to product list
                return q.peek();
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


    public ObservableList<ViewProduct> toObservableList() {
        ArrayList<ViewProduct> list = new ArrayList<>();

        for (PriorityQueue<Product> queue : storage) {
            for (Product p : queue) {
                list.add(new ViewProduct(p));
            }
        }

        return FXCollections.observableArrayList(list);
    }


    public List<ViewProduct> toList() {
        LinkedList<ViewProduct> list = new LinkedList<>();

        for (PriorityQueue<Product> queue : storage) {
            list.add(new ViewProduct(queue.peek()));
        }

        return list;
    }


    public Set<Product> getProductSet() {
        TreeSet<Product> productSet = new TreeSet<>(Comparator.comparing(Product::getName));

        for (PriorityQueue<Product> q : storage) {
            productSet.add(q.peek());
        }

        return productSet;
    }


    public int updateStorageNum(String productName, int newNum) {
        int oldNum = getNum(productName);

        if (oldNum == 0) throw new IllegalArgumentException(
            "Cannot update number of products, when no products are present!"
        );

        if (newNum > oldNum) {
            // New number is larger, add copies of most expensive product
            Product copyThis = null;
            PriorityQueue<Product> storageQueue = null;

            for (PriorityQueue<Product> q : storage) {
                // Is this the product we're looking for?
                if (q.peek().getName().equals(productName)) {
                    storageQueue = q;

                    // Find smallest instance
                    copyThis = q.peek();
                    for (Product p : q) {
                        if (p.getSellPrice() > copyThis.getSellPrice()) {
                            copyThis = p;
                        }
                    }
                }
            }

            for (int i = 0; i < newNum - oldNum; i++) {
                storageQueue.add(new Product(
                    copyThis.getName(),
                    copyThis.getSellPrice(),
                    copyThis.getBuyPrice()
                ));
            }
        } else if (newNum < oldNum) {
            // New number is smaller, remove the cheapest entries

            for (PriorityQueue<Product> q : storage) {
                // Is this the product we're looking for?
                if (q.peek().getName().equals(productName)) {

                    // poll from queue (remove from storage)
                    for (int i = 0; i < oldNum - newNum; i++) {
                        q.poll();
                    }

                    if (q.size() == 0) storage.remove(q);
                }
            }
        }

        return newNum - oldNum;
    }


    public void editProducts(ProductQuery query, String newName, double newSellPrice) {
        double tolerance = 1e-7;

        for (PriorityQueue<Product> q : storage) {
            for (Product p : q) {
                if (query.match(p)) {
                    if (query.changeName()) p.setName(newName);
                    if (query.changePrice()) p.setSellPrice(newSellPrice);
                }
            }
        }
    }


    public Set<Product> getSellPriceSet() {
        Set<Double> priceSet = new HashSet<>();
        Set<Product> productSet = new HashSet<>();

        for (PriorityQueue<Product> q : storage) {
            for (Product p : q) {
                if (!priceSet.contains(p.getSellPrice())) {
                    priceSet.add(p.getSellPrice());
                    productSet.add(p);
                }
            }
        }

        return productSet;
    }


    public Set<Product> getSellPriceSet(String productName) {
        Set<Double> priceSet = new HashSet<>();
        Set<Product> productSet = new HashSet<>();

        for (PriorityQueue<Product> q : storage) {
            if (q.peek().getName().equals(productName)) {
                for (Product p : q) {
                    if (!priceSet.contains(p.getSellPrice())) {
                        priceSet.add(p.getSellPrice());
                        productSet.add(p);
                    }
                }
            }
        }

        return productSet;
    }


    public Set<Product> getSellPriceSet(Product product) {
        return getSellPriceSet(product.getName());
    }


    public void updateStorageNum(Product product, int newNum) {
        updateStorageNum(product.getName(), newNum);
    }
}
