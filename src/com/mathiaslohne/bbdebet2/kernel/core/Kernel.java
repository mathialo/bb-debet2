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

package com.mathiaslohne.bbdebet2.kernel.core;

import com.mathiaslohne.bbdebet2.gui.Main;
import com.mathiaslohne.bbdebet2.kernel.accounting.Account;
import com.mathiaslohne.bbdebet2.kernel.accounting.AccountSet;
import com.mathiaslohne.bbdebet2.kernel.accounting.Ledger;
import com.mathiaslohne.bbdebet2.kernel.backup.AutoSaver;
import com.mathiaslohne.bbdebet2.kernel.logging.CsvLogger;
import com.mathiaslohne.bbdebet2.kernel.logging.Logger;
import com.mathiaslohne.bbdebet2.kernel.mailing.EmailSender;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 *
 */
public class Kernel implements CommandLineInterface {

    public static final String SAVE_DIR = System.getProperty("user.home") + "/.bbdebet2/";

    public static final String PROCESSEDINSERTS_FILENAME = "processedInserts";
    public static final String SALESHISTORY_FILENAME = "saleshistory.csv";
    public static final String USERLIST_FILENAME = "users.usl";
    public static final String STORAGE_FILENAME = "storage.csv";
    public static final String CATEGORIES_FILENAME = "categories.csv";
    public static final String TRANSACTIONHIST_FILENAME = "transactionhistory.csv";
    public static final String SETTINGS_FILENAME = "settings.properties";
    public static final String LOG_FILENAME = "log";
    public static final String LEDGER_FILENAME = "ledger.csv";
    public static final String ACCOUNTS_FILENAME = "accounts.csv";

    public static final String SALESHISTORY_FILEPATH = SAVE_DIR + SALESHISTORY_FILENAME;
    public static final String USERLIST_FILEPATH = SAVE_DIR + USERLIST_FILENAME;
    public static final String STORAGE_FILEPATH = SAVE_DIR + STORAGE_FILENAME;
    public static final String CATEGORIES_FILEPATH = SAVE_DIR + CATEGORIES_FILENAME;
    public static final String TRANSACTIONHIST_FILEPATH = SAVE_DIR + TRANSACTIONHIST_FILENAME;
    public static final String SETTINGS_FILEPATH = SAVE_DIR + SETTINGS_FILENAME;
    public static final String LOG_FILEPATH = SAVE_DIR + LOG_FILENAME;
    public static final String PROCESSEDINSERTS_FILEPATH = SAVE_DIR + PROCESSEDINSERTS_FILENAME;
    public static final String LEDGER_FILEPATH = SAVE_DIR + LEDGER_FILENAME;
    public static final String ACCOUNTS_FILEPATH = SAVE_DIR + ACCOUNTS_FILENAME;

    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yy - HH:mm");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    private static Logger logger;
    private File runningFile;

    private Exportable[] saveOnExit;

    private UserList userList;

    private Storage storage;
    private CategoryDict categories;

    private SalesHistory salesHistory;
    private TransactionHandler transactionHandler;
    private Ledger ledger;
    private AccountSet accounts;

    private SettingsHolder settingsHolder;
    private EmailSender emailSender;


    /**
     * Initializes a new kernel from specifications in ~/.com.mathiaslohne.bbdebet2. Creates an empty kernel if
     * nothing is specified in ~/.com.mathiaslohne.bbdebet2.
     *
     * @throws IllegalStateException If a kernel is already running on the system.
     */
    public Kernel() throws IllegalStateException {
        this(false);
    }


    public Kernel(boolean force) throws IllegalStateException {
        // Initialize Kernel
        CurrencyFormatter.initilalize(this);

        try {
            createLogger();
        } catch (Exception e) {
            if (!force) throw e;
        }
        try {
            createRunningFile();
        } catch (Exception e) {
            if (!force) throw e;
        }
        try {
            readFiles();
        } catch (Exception e) {
            if (!force) throw e;
        }
        CsvLogger.initialize();
        setupBackuping();

        transactionHandler = new TransactionHandler(this);
        emailSender = new EmailSender(this);

        // Make sure shutDown is called on exit
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));

        // Log status update
        logger.log("New kernel instantiated");
    }


    /**
     * Opens a CLI variant of the kernel.
     *
     * @param args
     */
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

        String command;

        while (cont) {
            System.out.print("  >> ");
            try {
                command = cmdline.nextLine();
                cont = kernel.parseAndRunCommand(command, System.out);

            } catch (NoSuchElementException e) {
                // Caused by an EOF in nextLine()
                System.out.println("EOF!");
                cont = false;
            }
        }
    }


    /**
     * Parse a command and execute it
     *
     * @param rawCommand   command to execute
     * @param outputStream where to dump output
     *
     * @return
     */
    public boolean parseAndRunCommand(String rawCommand, PrintStream outputStream) {
        boolean cont = true;
        Kernel kernel = this;

        String[] command = rawCommand.split("\\s+");

        try {
            switch (command[0]) {
                case "shutDown":
                    cont = false;
                    break;

                case "isRunning":
                    outputStream.println(kernel.isRunning());
                    break;

                case "getUserList":
                    for (User u : kernel.getUserList()) {
                        outputStream.println(u);
                    }
                    break;

                case "addUser":
                    kernel.getUserList().add(new User(command[1], command[2]));
                    break;

                case "removeUser":
                    kernel.getUserList().remove(kernel.getUserList().find(command[1]));
                    break;

                case "addBalance":
                    kernel.getUserList().find(command[1]).addBalance(
                        Double.parseDouble(command[2]));
                    break;

                case "subtractBalance":
                    kernel.getUserList().find(command[1]).subtractBalance(
                        Double.parseDouble(command[2]));
                    break;

                case "getBalance":
                    outputStream.println(kernel.getUserList().find(command[1]).getBalance());
                    break;

                case "getSalesHistory":
                    for (Sale s : kernel.getSalesHistory()) {
                        outputStream.println(s.getUserName() + ", " + s);
                    }
                    break;

                case "newSale":
                    kernel.getTransactionHandler().newPurchase(
                        kernel.getUserList().find(command[1]),
                        kernel.getStorage().get(command[2])
                    );
                    break;

                case "getStorage":
                    outputStream.printf("%20s  %6s  %6s\n", "Product", "Price", "Num");

                    for (Product p : kernel.getStorage().getProductSet()) {
                        outputStream.printf(
                            "%20s  %6.2f  %6d\n", p.getName(), p.getSellPrice(),
                            kernel.getStorage().getNum(p)
                        );
                    }
                    break;

                case "addProduct":
                    kernel.getStorage().add(
                        new Product(command[1], Double.parseDouble(command[2]),
                            Double.parseDouble(command[3])
                        ));
                    break;

                case "newUserTransaction":
                    kernel.getTransactionHandler().newUserTransaction(
                        kernel.getUserList().find(command[1]),
                        kernel.getUserList().find(command[2]), Double.parseDouble(command[3])
                    );
                    break;


                default:
                    outputStream.println("Unknown command '" + command[0] + "'");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            outputStream.println("Not enough arguments for '" + command[0] + "'");
        } catch (NumberFormatException e) {
            outputStream.println("Invalid argument type(s) for '" + command[0] + "'");
        } catch (Exception e) {
            Kernel.getLogger().log(e);
        }

        return cont;
    }


    /**
     * Sets up AutoSaver objects to automatically save data to disk
     */
    private void setupBackuping() {
        // set up autosaving every x minutes (as specified by user)
        AutoSaver autoSaver = new AutoSaver(this);
        autoSaver.start(settingsHolder.getAutoSaveInterval());

        // set up backup every x hours (as specified in settings)
        AutoSaver autoBackup = new AutoSaver(this, "autosave/");
        autoBackup.placeInSubdirs(true);
        autoBackup.autoSendToRemoteOnSave(settingsHolder.isAutoSend());
        if (settingsHolder.getBackupInterval() > 0) {
            autoBackup.start(60 * settingsHolder.getBackupInterval());
        }
    }


    /**
     * Creates a Logger object. Dumps stacktrace to terminal if an IOException occurs.
     */
    private void createLogger() {
        try {
            logger = new Logger(new File(LOG_FILEPATH), true);
        } catch (IOException e) {
            System.out.println("Warning: Could not instantiate logger!");
            e.printStackTrace();
        }
    }


    /**
     * Creates a file "running" in SAVE_DIR. <p> This will be deleted on kernel shutdown, hence we
     * know if a kernel is running on the system or not.
     *
     * @throws KernelAlreadyRunningException If "running" file already exists.
     */
    private void createRunningFile() throws KernelAlreadyRunningException {
        runningFile = new File(SAVE_DIR + "running");
        if (runningFile.exists()) {
            logger.log("Error: Kernel already running on system");
            logger.close();
            throw new KernelAlreadyRunningException(
                "Could not instantiate kernel. An instance of com.mathiaslohne.bbdebet2 is already running on the system."
            );
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            runningFile.createNewFile();
        } catch (IOException e) {
            logger.log(e);
        }
    }


    /**
     * Reads user list, storage and sales history from SAVE_DIR and initializes objects from them.
     */
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

        logger.log("Loading CategoryDict");
        try {
            categories = new CategoryDict(new File(CATEGORIES_FILEPATH));
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to empty category dict");

            categories = new CategoryDict();
        }

        logger.log("Loading SalesHistory");
        try {
            salesHistory = new SalesHistory(new File(SALESHISTORY_FILEPATH));
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to empty sales history");

            salesHistory = new SalesHistory();
        }

        logger.log("Loading settings");
        try {
            settingsHolder = new SettingsHolder(new File(SETTINGS_FILEPATH));
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to standard settings");

            settingsHolder = new SettingsHolder();
        }

        logger.log("Loading account list");
        try {
            accounts = new AccountSet(new File(ACCOUNTS_FILEPATH));
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to standard account list");

            accounts = new AccountSet();
            accounts.add(new Account("Varebeholdning", 1400));
            accounts.add(new Account("Pant", 1410, true, false));
            accounts.add(new Account("Kontanter", 1900, true, true));
            accounts.add(new Account("Bankkonto", 1910, true, true));
            accounts.add(new Account("Debetbok", 2000, true, false));
            accounts.add(new Account("Avanse", 3000));
            accounts.add(new Account("Sponsing", 7000, true, false));
            accounts.add(new Account("Gaver", 7010, true, false));
            accounts.add(new Account("Rekvisita", 7020));
            accounts.add(new Account("Svinn", 8000));
            accounts.add(new Account("Øreavrunding", 8010));
        }

        logger.log("Loading ledger");
        try {
            ledger = new Ledger(new File(LEDGER_FILEPATH), accounts);
        } catch (IOException | ErrorInFileException e) {
            logger.log(e);
            logger.log("Falling back to empty ledger");

            ledger = new Ledger();
        }

        this.saveOnExit = new Exportable[]{userList, storage, categories, salesHistory, settingsHolder, accounts, ledger};
    }


    /**
     * Returns active EmailSender for this kernel
     *
     * @return Current email sender
     */
    public EmailSender getEmailSender() {
        return emailSender;
    }


    /**
     * Returns active settings for this kernel
     *
     * @return Current settings
     */
    public SettingsHolder getSettingsHolder() {
        return settingsHolder;
    }


    /**
     * Returns active user list for this kernel
     *
     * @return Current user list
     */
    public UserList getUserList() {
        return userList;
    }


    /**
     * Returns active sales history for this kernel
     *
     * @return Current sales history
     */
    public SalesHistory getSalesHistory() {
        return salesHistory;
    }


    /**
     * Returns active storage for this kernel
     *
     * @return Current storage
     */
    public Storage getStorage() {
        return storage;
    }


    /**
     * Returns active categories for this kernel
     *
     * @return Current categories
     */
    public CategoryDict getCategories() {
        return categories;
    }


    /**
     * Returns the active logger for this kernel
     *
     * @return Current logger
     */
    public static Logger getLogger() {
        return logger;
    }


    /**
     * Returns the active transaction handler for this kernel
     *
     * @return Current transaction handler
     */
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }


    /**
     * Returns the active ledger for this kernel
     *
     * @return List of expences
     */
    public Ledger getLedger() {
        return ledger;
    }


    /**
     * Returns all the accounts on this kernel
     *
     * @return Set of accounts
     */
    public AccountSet getAccounts() {
        return accounts;
    }


    /**
     * Saves all savable data (sales history, settings, storage, etc)
     */
    public void saveAll() {
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
    }


    /**
     * Properly shuts down kernel if running.
     * <p>
     * Saves all exportable files to SAVE_DIR, deletes "running" file, and closes logger.
     */
    public void shutDown() {
        if (!isRunning()) return;

        logger.log("Initiating safe shutdown");

        // If in GUI mode, we must empty cart so no items are lost
        if (Main.getCurrentUserController() != null) {
            logger.log("Making sure no items are left in the cart");
            Main.getCurrentUserController().reAddCartToStorage();
        }

        saveAll();

        //noinspection ResultOfMethodCallIgnored
        runningFile.delete();

        logger.log("Shutting down kernel");
        logger.close();
    }


    /**
     * Returns the state of the kernel, ie, the existance of the "running" file.
     *
     * @return true if kernel is running, false if not
     */
    public boolean isRunning() {
        return runningFile.exists();
    }
}
