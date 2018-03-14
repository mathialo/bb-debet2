/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.logging;

import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class CsvLogger {

    private static Map<String, String[]> headers = new HashMap<>();


    public static Map<String, String[]> getHeaders() {
        return headers;
    }


    public static void initialize() {
        String[] head0 = {"From", "To", "Amount"};
        headers.put("usertransactions", head0);
    }


    private static void addRow(String filename, String[] row) throws IOException {

        long timestamp = System.currentTimeMillis() / 1000L;

        String appendline = timestamp + "," + String.join(",", row);

        String useFilename = filename.endsWith(".csv") ? filename : filename + ".csv";

        // try to append to existing file
        try {
            Files.write(Paths.get(Kernel.SAVE_DIR + useFilename), appendline.getBytes(), StandardOpenOption.APPEND);
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
}
