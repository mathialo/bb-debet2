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

package bbdebet2.kernel.accounting;

import bbdebet2.gui.modelwrappers.ViewExpence;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.ErrorInFileException;
import bbdebet2.kernel.datastructs.Exportable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Ledger extends ArrayList<Expence> implements Exportable {

    public Ledger() {
        super();
    }


    public Ledger(File csvFile, AccountSet accounts) throws IOException, ErrorInFileException {
        super();

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

            // read rest as csv
            while (sc.hasNextLine()) {
                // read line, split on comma
                String[] line = sc.nextLine().split("\\s*,\\s*");

                add(new Expence(
                        accounts.fromAccountNumber(Integer.parseInt(line[1])),
                        Double.parseDouble(line[3]),
                        line[4],
                        Long.parseLong(line[0])
                    ).resolve(accounts.fromAccountNumber(Integer.parseInt(line[2])))
                );

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


    @Override
    public void saveFile(File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(file);

        printWriter.println("Timestamp,To,From,Amount,Comment");

        for (Expence expence : this) {
            printWriter.println(String.format("%d,%d,%d,%f,%s",
                expence.getTimestamp(),
                expence.getTo().getNumber(),
                expence.getFrom().getNumber(),
                expence.getAmount(),
                expence.getComment()
            ));
        }

        printWriter.close();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.LEDGER_FILEPATH));
    }


    public ObservableList<ViewExpence> toObservableList() {
        ArrayList<ViewExpence> newList = new ArrayList<>(size());

        for (Expence e : this) newList.add(new ViewExpence(e));

        return FXCollections.observableList(newList);
    }
}


