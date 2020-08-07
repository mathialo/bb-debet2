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

package com.mathiaslohne.bbdebet2.kernel.logging;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class CsvLogger {

    private static Map<String, String[]> headers = new HashMap<>();


    public static Map<String, String[]> getHeaders() {
        return headers;
    }


    public static void initialize() {
        String[] head0 = {"From", "To", "Amount"};
        headers.put("usertransactions", head0);
//        String[] head1 = {"Product", "Quantity", "Value"};
//        headers.put("losses", head1);
        String[] head2 = {"User", "Amount"};
        headers.put("moneyinserts", head2);
    }


    public static Set<String> getAvailableFileNames() {
        return headers.keySet();
    }


    private static void addRow(String filename, String[] row) throws IOException {

        long timestamp = System.currentTimeMillis() / 1000L;

        String appendline = timestamp + "," + String.join(",", row) + "\n";

        String useFilename = filename.endsWith(".csv") ? filename : filename + ".csv";

        // try to append to existing file
        try {
            Files.write(
                Paths.get(Kernel.SAVE_DIR + useFilename), appendline.getBytes(),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // if file doesn't exist, the above will fail. Try to create a new file, and write to it
            PrintWriter pw = new PrintWriter(Kernel.SAVE_DIR + useFilename);
            String[] header = headers.get(filename.replaceAll(".csv", ""));
            if (header != null) pw.println("Timestamp," + String.join(",", header));
            pw.print(appendline);
            pw.close();
        }
    }


    public static void addUserTransaction(User from, User to, double amount) throws IOException {
        String[] row = {from.getUserName(), to.getUserName(), amount + ""};
        addRow("usertransactions", row);
    }


//    public static void addProductLoss(Product product, int num) throws IOException {
//        String[] row = {product.getName(), num + "", product.getSellPrice() * num + ""};
//        addRow("losses", row);
//    }


    public static void addMoneyInserts(User user, double amount) throws IOException {
        String[] row = {user.getUserName(), amount + ""};
        addRow("moneyinserts", row);
    }
}
