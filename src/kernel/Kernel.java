/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel;

import kernel.datastructs.ErrorInFileException;
import kernel.datastructs.Exportable;
import kernel.datastructs.User;
import kernel.datastructs.UserList;
import kernel.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Kernel {

    public static Logger logger;

    public static final String SAVE_DIR = System.getProperty("user.home") + "/.bbdebet2/";

    public static final String SALESHISTORY_FILENAME = "saleshistory.csv";
    public static final String USERLIST_FILENAME = "users.usl";
    public static final String STORAGE_FILENAME = "storage.csv";
    public static final String TRANSACTIONHIST_FILENAME = "transactionhistory.csv";
    public static final String SETTINGS_FILENAME = "settings.properties";
    public static final String LOG_FILENAME = "log";

    public static final String SALESHISTORY_FILEPATH = SAVE_DIR + SALESHISTORY_FILENAME;
    public static final String USERLIST_FILEPATH = SAVE_DIR + USERLIST_FILENAME;
    public static final String STORAGE_FILEPATH = SAVE_DIR + STORAGE_FILENAME;
    public static final String TRANSACTIONHIST_FILEPATH = SAVE_DIR + TRANSACTIONHIST_FILENAME;
    public static final String SETTINGS_FILEPATH = SAVE_DIR + SETTINGS_FILENAME;
    public static final String LOG_FILEPATH = SAVE_DIR + LOG_FILENAME;

    private File runningFile;

    private Exportable[] saveOnExit;
    private UserList userList;

    public Kernel() {
        // Initialize Kernel
        createLogger();
        createRunningFile();
        readFiles();

        // Make sure shutDown is called on exit
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutDown();
            }
        });

        // Log status update
        logger.log("New kernel instantiated");
    }

    private void createLogger() {
        try {
            logger = new Logger(new File(LOG_FILEPATH), true);

        } catch (IOException e) {
            System.out.println("Warning: Could not instantiate logger!");
            e.printStackTrace();
        }
    }

    private void createRunningFile() {
        runningFile = new File(SAVE_DIR + "running");
        if (runningFile.exists()) {
            logger.log("Error: Kernel already running on system");
            logger.close();
            System.exit(1);
        }

        try {
            runningFile.createNewFile();

        } catch (IOException e) {
            logger.log(e);
        }
    }

    private void readFiles() {
        logger.log("Loading UserList");
        try {
            userList = new UserList(new File(USERLIST_FILEPATH));

        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to empty user list");

            userList = new UserList();
        }

        saveOnExit = new Exportable[1];
        saveOnExit[0] = userList;
    }


    public UserList getUserList() {
        return userList;
    }


    public void shutDown() {
        if (!isRunning()) return;

        if (saveOnExit != null) {
            for (Exportable e : saveOnExit) {
                logger.log("Saving " + e.getClass().getSimpleName());
                try {
                    e.saveFile();

                } catch (IOException ex) {
                    logger.log(ex);
                }
            }
        }

        runningFile.delete();

        logger.log("Shutting down kernel");
        logger.close();
    }


    public boolean isRunning() {
        return runningFile.exists();
    }


    public static void main(String[] args) {
        Kernel kernel = new Kernel();

        boolean cont = true;
        Scanner cmdline = new Scanner(System.in);

        String[] command;

        while (cont) {
            try {
                System.out.print("  >> ");
                command = cmdline.nextLine().split("\\s+");

                switch (command[0]) {
                    case "shutDown":
                        cont = false;
                        break;

                    case "isRunning":
                        System.out.println(kernel.isRunning());
                        break;

                    case "getUserList":
                        for (User u : kernel.getUserList()) {
                            System.out.println(u);
                        }
                        break;

                    case "addUser":
                        kernel.getUserList().add(new User(command[1], command[2]));
                        break;

                    case "removeUser":
                        kernel.getUserList().remove(kernel.getUserList().find(command[1]));
                        break;

                    case "addBalance":
                        kernel.getUserList().find(command[1]).addBalance(Double.parseDouble(command[2]));
                        break;

                    case "subtractBalance":
                        kernel.getUserList().find(command[1]).subtractBalance(Double.parseDouble(command[2]));
                        break;

                    case "getBalance":
                        System.out.println(kernel.getUserList().find(command[1]).getBalance());
                        break;

                    default:
                        System.out.println("Unknown command '" + command[0] + "'");
                        break;
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }
}
