/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        } catch (IOException ignored) {
        }

        if (terminaldump) System.out.println("LOG:\t" + message);
    }


    public void log(Exception exception) {
        try {
            logWriter.write(getTimeStamp() + "New " + exception.getClass().getSimpleName() + " occurred:\n");
            logWriter.write("  -->> " + exception.getMessage() + "\n");
            logWriter.write("BEGIN STACKTRACE\n");
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (StackTraceElement e : stackTrace) {
                logWriter.write("\t" + e.toString() + "\n");
            }
            logWriter.write("END STACKTRACE\n");
        } catch (IOException ignored) {
        }

        if (terminaldump) System.out.println("LOG:\tError: " + exception.getMessage());
    }


    public void close() {
        try {
            log("Closing");
            logWriter.close();
        } catch (IOException ignored) {
        }
    }
}
