/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.logging;

import bbdebet2.kernel.Kernel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Logger {

    private final long MAX_FILESIZE = 100000;

    private File logFile;

    private FileWriter logWriter;
    private boolean terminaldump;

    private List<PrintStream> outputStreams;


    public Logger(File logFile, boolean writeToTerminal) throws IOException {
        logWriter = new FileWriter(logFile, true);
        this.logFile = logFile;

        outputStreams = new LinkedList<>();
        if (writeToTerminal) {
            outputStreams.add(System.out);
        }

        log("New startup");
    }


    public void addOutputStream(PrintStream outputStream) {
        outputStreams.add(outputStream);
    }


    public void removeOutputStream(PrintStream outputStream) {
        outputStreams.remove(outputStream);
    }


    public void removeAllOutputStreams() {
        outputStreams.clear();
    }


    private void controlFileSize() {
        if (logFile.length() >= MAX_FILESIZE) {
            // Close previous file
            try {
                logWriter.close();
            } catch (IOException ignored) {
            }

            // Delete previous .old-file if it exists
            File oldFile = new File(Kernel.LOG_FILEPATH + ".old");
            if (oldFile.exists()) oldFile.delete();

            // Copy log to log.old
            try {
                Files.copy(logFile.toPath(), oldFile.toPath());
            } catch (IOException ignored) {
            }

            // Create new logger and new log file
            logFile = new File(Kernel.LOG_FILEPATH);
            try {
                logWriter = new FileWriter(logFile);
            } catch (IOException e) {
            }
        }
    }


    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()) + ":\t";
    }


    public void log(String message) {
        try {
            logWriter.write(getTimeStamp() + message + ".\n");
        } catch (IOException ignored) {
        }

        for (PrintStream output : outputStreams) {
            output.println("LOG:\t" + message);
        }

        controlFileSize();
    }


    public void log(Exception exception) {
        try {
            logWriter.write(
                getTimeStamp() + "New " + exception.getClass().getSimpleName() + " occurred:\n");
            logWriter.write("  -->> " + exception.getMessage() + "\n");
            logWriter.write("BEGIN STACKTRACE\n");
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (StackTraceElement e : stackTrace) {
                logWriter.write("\t" + e.toString() + "\n");
            }
            logWriter.write("END STACKTRACE\n");
        } catch (IOException ignored) {
        }

        for (PrintStream output : outputStreams) {
            output.println("LOG:\tError: " + exception.getMessage());
        }

        controlFileSize();
    }


    public void log(Object object) {
        log("Requested to log object of type " + object.getClass() + ": " + object.toString());
    }


    public void close() {
        try {
            log("Closing");
            logWriter.close();
        } catch (IOException ignored) {
        }
    }
}
