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

import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
        readFile(file);
    }


    private void readFile(File file) throws IOException, ErrorInFileException {
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

                Product product = new Product(line[0], Double.parseDouble(line[2]), Double.parseDouble(line[1]));

                if (line.length == 4)
                    product.setPant(Double.parseDouble(line[3]));

                add(product);

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

        pw.println("Product,BuyPrice,SellPrice,Pant");

        for (PriorityQueue<Product> q : storage) {
            for (Product p : q) {
                pw.println(p.getName() + "," + p.getBuyPrice() + "," + p.getSellPrice() + "," + p.getPant());
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


    public Product findIgnoreCase(String productName) {
        for (PriorityQueue<Product> q : storage) {
            if (q.peek().getName().equalsIgnoreCase(productName)) {
                return q.peek();
            }
        }

        return null;
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


    public double getTotalSellValue() {
        double sum = 0;

        for (PriorityQueue<Product> pq : storage) {
            for (Product p : pq) {
                sum += p.getSellPrice();
            }
        }

        return sum;
    }


    public double getTotalBuyValue() {
        double sum = 0;

        for (PriorityQueue<Product> pq : storage) {
            for (Product p : pq) {
                sum += p.getBuyPrice();
            }
        }

        return sum;
    }


    public double getTotalMarkup() {
        double sum = 0;

        for (PriorityQueue<Product> pq : storage) {
            for (Product p : pq) {
                sum += p.getMarkup();
            }
        }

        return sum;
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

            Iterator<PriorityQueue<Product>> storageIterator = storage.iterator();
            while (storageIterator.hasNext()) {
                PriorityQueue<Product> q = storageIterator.next();

                // Is this the product we're looking for?
                if (q.peek().getName().equals(productName)) {

                    // poll from queue (remove from storage)
                    for (int i = 0; i < oldNum - newNum; i++) {
                        q.poll();
                    }
                    System.out.println(q.size());

                    if (q.size() == 0) storageIterator.remove();
                }
            }
        }

        return newNum - oldNum;
    }


    public void editProducts(ProductQuery query, String newName, double newSellPrice) {
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


    public void resetToFile(File file) throws IOException, ErrorInFileException {
        storage = new LinkedList<>();
        readFile(file);
    }
}
