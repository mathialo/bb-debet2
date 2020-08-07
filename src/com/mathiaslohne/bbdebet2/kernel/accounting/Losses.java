/*
 * Copyright (C) 2020  Mathias Lohne
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

import com.mathiaslohne.bbdebet2.kernel.core.ErrorInFileException;
import com.mathiaslohne.bbdebet2.kernel.core.Exportable;
import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.Listable;
import com.mathiaslohne.bbdebet2.kernel.core.Product;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Losses implements Exportable {

    private List<LossEntry> losses;


    public Losses() {
        losses = new LinkedList<>();
    }


    public Losses(File csvFile) throws IOException, ErrorInFileException {
        this();
        readCsv(csvFile);
    }


    private void readCsv(File csvFile) throws IOException, ErrorInFileException {
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
                losses.add(
                    new LossEntry(
                        Long.parseLong(line[0]),
                        line[1],
                        Integer.parseInt(line[2]),
                        Double.parseDouble(line[3])
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


    public void add(Product product, int number) {
        losses.add(new LossEntry(product, number));
    }


    /**
     * Saves the object. If the File object is a directory, it should place itself in the directory with a suitable, automatically generated filename.
     *
     * @param file Where to save
     *
     * @throws IOException On IO errors
     */
    @Override
    public void saveFile(File file) throws IOException {
        if (file.isDirectory()) {
            saveFile(new File(file.getAbsolutePath() + "/" + Kernel.LOSSES_FILENAME));
            return;
        }

        // new output stream
        PrintWriter pw = new PrintWriter(file);

        // write csv-headers
        pw.println("Timestamp,Product,Quantity,Value");

        // sale num counter
        for (LossEntry l : losses) {
            // write sale as csv, with sale counter as first col
            pw.println(l.getTimestamp() + "," + l.getProductName() + "," + l.getNumber() + "," + l.getAmount());
        }

        // close output stream
        pw.close();
    }


    /**
     * Saves the object with an automatically generated filename.
     *
     * @throws IOException On IO errors
     */
    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.LOSSES_FILEPATH));
    }
}
