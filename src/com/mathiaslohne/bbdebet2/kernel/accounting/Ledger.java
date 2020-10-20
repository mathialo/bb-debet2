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

import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewExpense;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.ErrorInFileException;
import com.mathiaslohne.bbdebet2.kernel.core.Exportable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Stream;


public class Ledger implements Exportable, Iterable<Expense> {

    private Map<Integer, Expense> expenses;


    public Ledger() {
        expenses = new TreeMap<>();
    }


    public Ledger(File csvFile, AccountSet accounts) throws IOException, ErrorInFileException {
        this();

        readCsv(csvFile, accounts);
    }


    private void readCsv(File file, AccountSet accounts) throws IOException, ErrorInFileException {
        // we'll skip first line, hence the counter can begin on line 2.
        int lineNum = 2;

        try {
            // new stream
            Scanner sc = new Scanner(file);

            // skip first row (table headers)
            sc.nextLine();

            int largestId = 0;

            // read rest as csv
            while (sc.hasNextLine()) {
                // read line, split on comma
                String[] line = sc.nextLine().split("\\s*,\\s*", 6);

                int id = Integer.parseInt(line[0]);

                Expense expense = expenses.get(id);

                if (expense == null) {
                    expense = new Expense(line[5].replaceAll("\\\\n", "\n").trim(), Long.parseLong(line[1]), id);
                    expenses.put(id, expense);
                }

                expense.addTransaction(
                    new Expense.Transaction(
                        accounts.fromAccountNumber(Integer.parseInt(line[3])),
                        Double.parseDouble(line[4]),
                        Expense.TransactionType.valueOf(line[2])
                    ));

                largestId = Math.max(id, largestId);

                // update line num
                lineNum++;
            }

            Expense.counter = largestId+1;

            // close in stream
            sc.close();
        } catch (IllegalArgumentException e) {
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


    @Override
    public void saveFile(File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(file);

        printWriter.println("ID,Timestamp,Operation,Account,Amount,Comment");

        for (Expense expense : this) {
            if (expense == null) {
                Kernel.getLogger().log("Cannot save null as expense");
                continue;
            }

            Stream.concat(expense.getFrom().stream(), expense.getTo().stream()).forEach(transaction -> {
                printWriter.println(String.format("%d,%d,%s,%d,%f,%s",
                    expense.getId(),
                    expense.getTimestamp(),
                    transaction.getType().toString(),
                    transaction.getAccount().getNumber(),
                    transaction.getAmount(),
                    expense.getComment()
                        .replaceAll("\n", "\\\\n")
                        .replaceAll(",","")
                        .replaceAll("\r", "")
                        .replaceAll("\t", " ")
                        .trim()
                ));
            });
        }

        printWriter.close();
    }


    public void add(Expense expense) {
        expenses.put(expense.getId(), expense);
    }


    public void remove(Expense expense) {
        expenses.remove(expense.getId());
    }


    public Iterator<Expense> iterator() {
        return expenses.values().iterator();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.LEDGER_FILEPATH));
    }


    public ObservableList<ViewExpense> toObservableList() {
        ArrayList<ViewExpense> newList = new ArrayList<>(expenses.size());

        for (Expense e : this) newList.add(new ViewExpense(e));

        return FXCollections.observableList(newList);
    }
}


