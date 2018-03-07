/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.logger;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private FileWriter logWriter;
    private boolean terminaldump;

    public Logger(File logFile, boolean writeToTerminal) throws IOException {
        logWriter = new FileWriter(logFile, true);
        terminaldump = writeToTerminal;
        log("New startup");
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()) + ":\t";
    }

    public void log(String message) {
        try {
            logWriter.write(getTimeStamp() + message + ".\n");
        } catch (IOException e) {}

        if (terminaldump) System.out.println("LOG:\t" + message);
    }

    public void log(Exception exeption) {
        try {
            logWriter.write(getTimeStamp() + "NEW EXCEPTION OCCURED:\n");
            logWriter.write("  -->> " + exeption.getMessage() + "\n");
            logWriter.write("BEGIN STACKTRACE\n");
            StackTraceElement[] stackTrace = exeption.getStackTrace();
            for (StackTraceElement e : stackTrace) {
                logWriter.write("\t" + e.toString() + "\n");
            }
            logWriter.write("END STACKTRACE\n");
        } catch (IOException e) {}

        if (terminaldump) System.out.println(exeption.getMessage());
    }

    public void close() {
        try {
            log("Closing");
            logWriter.close();
        } catch (IOException e) {}
    }
}
