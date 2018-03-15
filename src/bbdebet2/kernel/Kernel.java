/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel;

import bbdebet2.kernel.datastructs.ErrorInFileException;
import bbdebet2.kernel.datastructs.Exportable;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.Sale;
import bbdebet2.kernel.datastructs.SalesHistory;
import bbdebet2.kernel.datastructs.Storage;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.datastructs.UserList;
import bbdebet2.kernel.logging.CsvLogger;
import bbdebet2.kernel.logging.Logger;
import bbdebet2.kernel.transactions.TransactionHandler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


/**
 *
 */
public class Kernel {

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

    private Logger logger;
    private File runningFile;

    private Exportable[] saveOnExit;
    private UserList userList;
    private Storage storage;
    private SalesHistory salesHistory;
    private TransactionHandler transactionHandler;


    /**
     * Initializes a new kernel from specifications in ~/.bbdebet2. Creates an empty kernel if nothing is specified in ~/.bbdebet2.
     *
     * @throws IllegalStateException If a kernel is allready running on the system.
     */
    public Kernel() throws IllegalStateException {
        // Initialize Kernel
        createLogger();
        createRunningFile();
        readFiles();
        CsvLogger.initialize();

        transactionHandler = new TransactionHandler(this);

        // Make sure shutDown is called on exit
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));

        // Log status update
        logger.log("New kernel instantiated");
    }


    public static void main(String[] args) {
        Kernel kernel = null;
        
        try {
            kernel = new Kernel();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        boolean cont = true;
        Scanner cmdline = new Scanner(System.in);

        String[] command;

        while (cont) {
            System.out.print("  >> ");
            command = cmdline.nextLine().split("\\s+");

            try {
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

                    case "getSalesHistory":
                        for (Sale s : kernel.getSalesHistory()) {
                            System.out.println(s.getUserName() + ", " + s);
                        }
                        break;

                    case "newSale":
                        kernel.getTransactionHandler().newPurchase(kernel.getUserList().find(command[1]), kernel.getStorage().get(command[2]));
                        break;

                    case "getStorage":
                        System.out.printf("%20s  %6s  %6s\n", "Product", "Price", "Num");

                        for (Product p : kernel.getStorage().getProductSet()) {
                            System.out.printf("%20s  %6.2f  %6d\n", p.getName(), p.getSellPrice(), kernel.getStorage().getNum(p));
                        }
                        break;

                    case "addProduct":
                        kernel.getStorage().add(new Product(command[1], Double.parseDouble(command[2]), Double.parseDouble(command[3])));
                        break;

                    case "newUserTransaction":
                        kernel.getTransactionHandler().newUserTransaction(kernel.getUserList().find(command[1]), kernel.getUserList().find(command[2]), Double.parseDouble(command[3]));
                        break;


                    default:
                        System.out.println("Unknown command '" + command[0] + "'");
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Not enough arguments for '" + command[0] + "'");
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument type(s) for '" + command[0] + "'");
            } catch (Exception e) {
                kernel.getLogger().log(e);
            }
        }
    }


    private void createLogger() {
        try {
            logger = new Logger(new File(LOG_FILEPATH), true);
        } catch (IOException e) {
            System.out.println("Warning: Could not instantiate logger!");
            e.printStackTrace();
        }
    }


    private void createRunningFile() throws IllegalStateException {
        runningFile = new File(SAVE_DIR + "running");
        if (runningFile.exists()) {
            logger.log("Error: Kernel already running on system");
            logger.close();
            throw new IllegalStateException("Could not instantiate kernel. An instance of bbdebet2 is already running on the system.");
        }

        try {
            //noinspection ResultOfMethodCallIgnored
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

        logger.log("Loading Storage");
        try {
            storage = new Storage(new File(STORAGE_FILEPATH));
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to empty storage");

            storage = new Storage();
        }

        logger.log("Loading SalesHistory");
        try {
            salesHistory = new SalesHistory(new File(SALESHISTORY_FILEPATH));
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to empty storage");

            salesHistory = new SalesHistory();
        }

        this.saveOnExit = new Exportable[]{userList, storage, salesHistory};
    }


    public UserList getUserList() {
        return userList;
    }


    public SalesHistory getSalesHistory() {
        return salesHistory;
    }


    public Storage getStorage() {
        return storage;
    }


    public Logger getLogger() {
        return logger;
    }


    /**
     * Returns the active transaction handler for this kernel
     *
     * @return Active transaction handler
     */
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
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

        //noinspection ResultOfMethodCallIgnored
        runningFile.delete();

        logger.log("Shutting down kernel");
        logger.close();
    }


    public boolean isRunning() {
        return runningFile.exists();
    }
}
