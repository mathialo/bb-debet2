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

package bbdebet2.kernel.datastructs;

import bbdebet2.gui.modelwrappers.ViewProduct;
import bbdebet2.gui.modelwrappers.ViewSale;
import bbdebet2.kernel.Kernel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class SalesHistory implements Iterable<Sale>, Listable<ViewProduct>, Exportable {

    private LinkedList<Sale> history;

    private int counter;


    /**
     * creates empty sales history
     */
    public SalesHistory() {
        history = new LinkedList<Sale>();

        counter = 0;
    }


    /**
     * creates a new sales history and initalizes from file
     *
     * @param csvFile file to read
     *
     * @throws IOException          if file could not be read
     * @throws ErrorInFileException if there is an error in the file format
     */
    public SalesHistory(File csvFile) throws IOException, ErrorInFileException {
        // create new list
        history = new LinkedList<Sale>();

        // set counter
        counter = 0;

        // read file
        readCsv(csvFile);
    }


    private SalesHistory(LinkedList<Sale> history) {
        this.history = history;
    }


    public void resetToFile(File csvFile) throws IOException, ErrorInFileException {
        history = new LinkedList<Sale>();
        counter = 0;
        readCsv(csvFile);
    }


    private void readCsv(File csvFile) throws IOException, ErrorInFileException {
        // we'll skip first line, hence the counter can begin on line 2.
        int lineNum = 2;

        try {
            // new stream
            Scanner sc = new Scanner(csvFile);

            // skip first row (table headers)
            sc.nextLine();

            // read rest as csv
            while (sc.hasNextLine()) {
                // read line, split on comma
                String[] line = sc.nextLine().split("\\s*,\\s*");

                // make new Sale and add it to list
                history.add(
                    new Sale(
                        Integer.parseInt(line[0]),
                        Long.parseLong(line[1]),
                        line[2],
                        line[3],
                        Double.parseDouble(line[4]),
                        Double.parseDouble(line[5])
                    )
                );

                // update line num
                lineNum++;
            }

            // close in stream
            sc.close();
        } catch (NumberFormatException e) {
            throw new ErrorInFileException(
                "Feil i inputfil: Linje " + lineNum + " i " + csvFile + " inneholder et element av feil type.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ErrorInFileException(
                "Feil i inputfil: Linje " + lineNum + " i " + csvFile + " inneholder for få elementer.");
        } catch (NoSuchElementException e) {
            throw new ErrorInFileException(
                "Feil i inputfil: " + csvFile + " inneholder for få linjer.");
        }
    }


    /**
     * add new sale to list
     *
     * @param sale sale to add
     */
    public void add(Sale sale) {
        history.add(sale);
        counter++;
    }


    /**
     * save history as a csv-file. format specified in format definitions
     *
     * @param file file to save as
     *
     * @throws IOException if file could not be written
     */
    public void saveFile(File file) throws IOException {
        if (file.isDirectory()) {
            saveFile(new File(file.getAbsolutePath() + "/saleshistory.csv"));
            return;
        }

        // new output stream
        PrintWriter pw = new PrintWriter(file);

        // write csv-headers
        pw.println("ID,Timestamp,UserName,Product,Price,Earnings");

        // sale num counter
        int i = 0;
        for (Sale s : history) {
            // write sale as csv, with sale counter as first col
            pw.println(
                s.getId() + "," + s.getTimestamp() + "," + s.getUserName() + "," + s.getProductName() + "," + s.getPricePayed() + "," + s.getEarnings());
            i++;
        }

        // close output stream
        pw.close();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(Kernel.SALESHISTORY_FILEPATH);
    }


    /**
     * save history as a csv-file. format specified in format definitions
     *
     * @param filepath path to file to save as
     *
     * @throws IOException if file could not be written
     */
    public void saveFile(String filepath) throws IOException {
        saveFile(new File(filepath));
    }


    /**
     * make sale history iterable
     *
     * @return iterator of sales
     */
    public Iterator<Sale> iterator() {
        // just wrap iterator from linkedlist
        return history.iterator();
    }


    /**
     * return a sale history as a obserable list of Sales
     *
     * @return history as an observable list
     */
    public ObservableList<ViewSale> toObservableList() {
        // add all elements to arraylist
        ArrayList<ViewSale> listOfTransactions = new ArrayList<>();

        for (Sale s : history) {
            listOfTransactions.add(new ViewSale(s));
        }

        // reverse order, so that newest is on top
        Collections.reverse(listOfTransactions);

        // convert arraylist to obserablelist
        return FXCollections.observableArrayList(listOfTransactions);
    }


    /**
     * returns an ObservableList of all the sales made by given user
     *
     * @param user User to compare to
     *
     * @return all sales made by given user
     */
    public ObservableList<ViewSale> filterOnUser(User user) {
        // add all elements to arraylist
        ArrayList<ViewSale> listOfTransactions = new ArrayList<>();

        for (Sale s : history) {
            if (s.madeByUser(user)) {
                listOfTransactions.add(new ViewSale(s));
            }
        }

        // reverse order, so that newest is on top
        Collections.reverse(listOfTransactions);

        // convert arraylist to obserablelist
        return FXCollections.observableArrayList(listOfTransactions);
    }


    /**
     * returns an array of the given user's most buyed items
     *
     * @param user User to compare to
     * @param num  Number of faovurites
     *
     * @return all sales made by given user
     */
    public ArrayList<String> getFavourites(User user, int num) {
        HashMap<String, Integer> dict = new HashMap<String, Integer>();

        for (Sale s : history) {
            if (s.madeByUser(user)) {
                if (!dict.containsKey(s.getProductName())) {
                    dict.put(s.getProductName(), 1);
                } else {
                    int previous = dict.get(s.getProductName());
                    dict.remove(s.getProductName());
                    dict.put(s.getProductName(), previous + 1);
                }
            }
        }

        Sieve<Integer, String> sieve = new SortedSieve<Integer, String>(num);

        for (String key : dict.keySet()) {
            sieve.put(dict.get(key), key);
        }

        return sieve.getMaxValues();
    }


    /**
     * delete a sale from history
     *
     * @param id sale id
     */
    public void remove(int id) {
        Iterator<Sale> it = history.iterator();
        while (it.hasNext()) {
            Sale temp = it.next();
            if (temp.getId() == id) {
                it.remove();
                return;
            }
        }
    }


    /**
     * get sale object on given index
     *
     * @param id sale num
     *
     * @return sale on given index
     */
    public Sale get(int id) {
        for (Sale temp : history) {
            if (temp.getId() == id) {
                return temp;
            }
        }

        return null;
    }


    /**
     * searches for a sale of the given product, and returns the last one
     *
     * @param productName name of product
     *
     * @return last sale of given product
     */
    public Sale searchForProduct(String productName) {
        ListIterator<Sale> lit = history.listIterator(history.size() - 1);

        while (lit.hasPrevious()) {
            Sale temp = lit.previous();
            if (temp.getProductName().equals(productName)) {
                return temp;
            }
        }

        return null;
    }


    /**
     * returns a list of the last sales made for a given amount of time.
     *
     * @param seconds number of seconds to go back
     *
     * @return sales made since (now - seconds)
     */
    public SalesHistory filterLast(long seconds) {
        ListIterator<Sale> lit = history.listIterator(history.size() - 1);

        if (!lit.hasPrevious()) return new SalesHistory();

        long goBackTo = System.currentTimeMillis() / 1000L - seconds;
        Sale temp = lit.previous();

        while (lit.hasPrevious() && temp.getTimestamp() > goBackTo) {
            temp = lit.previous();
        }

        LinkedList<Sale> saleList = new LinkedList<Sale>();

        while (lit.hasNext()) {
            saleList.add(lit.next());
        }

        return new SalesHistory(saleList);
    }


    public List<ViewProduct> toList() {
        ListIterator<Sale> lit = history.listIterator(history.size() - 1);
        List<ViewProduct> list = new LinkedList<>();

        int maxThreshold = 100;
        int counter = 0;

        Product p;

        while (lit.hasPrevious() && counter < maxThreshold) {
            list.add(new ViewProduct(lit.previous().getProduct()));
            counter++;
        }

        return list;
    }


    public HashMap<String, Integer> getSummary() {
        HashMap<String, Integer> dict = new HashMap<String, Integer>();

        for (Sale s : history) {
            if (!dict.containsKey(s.getProductName())) {
                dict.put(s.getProductName(), 1);
            } else {
                int previous = dict.get(s.getProductName());
                dict.remove(s.getProductName());
                dict.put(s.getProductName(), previous + 1);
            }
        }

        return dict;
    }


    public double totalMoneySpent() {
        double sum = 0;

        for (Sale s : history) {
            sum += s.getPricePayed();
        }

        return sum;
    }


    public int size() {
        return history.size();
    }


    /**
     * resets the sales history (deletes everything)
     */
    public void reset() {
        history = new LinkedList<Sale>();
    }


    public Sale getFirst() {
        return history.getFirst();
    }


    public Sale getLast() {
        return history.getLast();
    }
}

