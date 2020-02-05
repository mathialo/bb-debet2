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

package com.mathiaslohne.bbdebet2.kernel.accounting;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.ErrorInFileException;
import com.mathiaslohne.bbdebet2.kernel.core.Exportable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class AccountSet implements Iterable<Account>, Exportable {

    private TreeMap<Integer, Account> accounts;


    public AccountSet() {
        accounts = new TreeMap<>();
    }


    public AccountSet(File csvFile) throws IOException, ErrorInFileException {
        this();
        readCsv(csvFile);
    }


    private void readCsv(File file) throws IOException, ErrorInFileException {
        // we'll skip first line, hence the counter can begin on line 2.
        int lineNum = 2;

        try {
            // new stream
            Scanner sc = new Scanner(file);

            // skip first row (table headers)
            sc.nextLine();

            // read rest as csv
            while (sc.hasNextLine()) {
                // read line, split on comma
                String[] line = sc.nextLine().split("\\s*,\\s*");

                accounts.put(Integer.parseInt(line[0]), new Account(line[1], Integer.parseInt(line[0]), Boolean.parseBoolean(line[2]), Boolean.parseBoolean(line[3])));

                // update line num
                lineNum++;
            }

            // close in stream
            sc.close();
        } catch (NumberFormatException e) {
            throw new ErrorInFileException(
                "Feil i inputfil: Linje " + lineNum + " i " + file + " inneholder et element av feil type.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ErrorInFileException(
                "Feil i inputfil: Linje " + lineNum + " i " + file + " inneholder for få elementer.");
        } catch (NoSuchElementException e) {
            throw new ErrorInFileException(
                "Feil i inputfil: " + file + " inneholder for få linjer.");
        }
    }


    public Account getStorageAccount() {
        return accounts.get(1400);
    }

    public Account getPantAccount() {
        return accounts.get(1410);
    }


    public Account fromAccountNumber(int accountNumber) {
        return accounts.get(accountNumber);
    }


    public void add(Account account) {
        accounts.put(account.getNumber(), account);
    }


    public Collection<Account> getAll() {
        return accounts.values();
    }


    public Collection<Account> getPaymentOptions() {
        return accounts.values().stream().filter(Account::isPaymentOption).collect(Collectors.toCollection(TreeSet::new));
    }


    public Collection<Account> getInsertOptions() {
        return accounts.values().stream().filter(Account::isInsertOption).collect(Collectors.toCollection(TreeSet::new));
    }

    public void deleteAccount(Account account) {
        accounts.remove(account.getNumber());
    }

    public void deleteAccount(int accountNumber) {
        accounts.remove(accountNumber);
    }


    @Override
    public Iterator<Account> iterator() {
        return accounts.values().iterator();
    }


    @Override
    public void saveFile(File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(file);

        printWriter.println("Number,Name,IsPaymentOption,IsInsertOption");

        for (Account account : this) {
            printWriter.println(account.getNumber() + "," + account.getName() + "," + account.isPaymentOption() + "," + account.isInsertOption());
        }

        printWriter.close();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.ACCOUNTS_FILEPATH));
    }
}
