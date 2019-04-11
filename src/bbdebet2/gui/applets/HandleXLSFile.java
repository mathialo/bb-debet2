/*
 * Copyright (c) 2019. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import bbdebet2.kernel.mailing.TextTemplate;
import bbdebet2.kernel.mailing.TextTemplateLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HandleXLSFile extends Applet {

    private File xlsFile;

    private List<MoneyInsets> fileInsets;
    private List<MoneyInsets> insetsForProcessing;

    private Collection<MoneyInsets> processedInsets;
    private Map<String, String> nameToUsername;
    private Map<String, TextField> unknownNameToUsername;

    @FXML
    private TextArea outputTextField;
    @FXML
    private GridPane nameMappingsTable;
    @FXML
    private CheckBox sendEmailsCheckBox;


    public static void createAndDisplayDialog() {
        Applet.createAndDisplayDialog("Prosesser XLS-fil", "HandleXLSFileView");
    }


    public static void main(String[] args) throws IOException {

    }


    private static boolean unqualifiedInputLine(String line) {
        return line.replaceAll("\\s+", "").equals("");
    }


    private void loadXlsFile() throws IOException {
        FileInputStream fis = new FileInputStream(xlsFile);
        HSSFWorkbook workbook = new HSSFWorkbook(fis);

        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();

        while (rowIt.hasNext()) {
            Row row = rowIt.next();
            if (row.getCell(0).toString().equals("Bokført")) break;
        }

        fileInsets = new LinkedList<>();

        while (rowIt.hasNext()) {
            Row row = rowIt.next();

            // Look for end of transactions:
            if (row.getCell(0).toString().startsWith("Sum")) break;

            // Qualify current transaction
            if (row.getCell(1).toString().equals("Omkostninger")
                || !row.getCell(4).toString().equals("")) continue;

            fileInsets.add(new MoneyInsets(row.getCell(0), row.getCell(2), row.getCell(5)));
        }
    }


    private void loadProcessed() {
        processedInsets = new HashSet<>();
        nameToUsername = new HashMap<>();

        Scanner inputFile = null;
        try {
            inputFile = new Scanner(new File(kernel.PROCESSEDINSETS_FILEPATH));
        } catch (FileNotFoundException e) {
            kernel.getLogger().log(e);
            kernel.getLogger().log("Could not read processed bank transactions");
            return;
        }

        String line;

        // Skip initial empty lines, if any
        while (inputFile.hasNextLine()) {
            line = inputFile.nextLine();
            if (line.replaceAll("\\s+", "").equalsIgnoreCase("[KNOWNMAPPINGS]")) break;
        }

        // Load known mappings between names and usernames
        while (inputFile.hasNextLine()) {
            line = inputFile.nextLine();

            // Stop when processed comes
            if (line.replaceAll("\\s+", "").equalsIgnoreCase("[PROCESSED]")) break;

            // Skip empty lines, etc
            if (unqualifiedInputLine(line)) continue;

            String[] elements = line.split("\\s*=\\s*");
            nameToUsername.put(elements[0], elements[1]);
        }

        // Load already processed transactions
        while (inputFile.hasNextLine()) {
            line = inputFile.nextLine();

            // Skip empty lines, etc
            if (unqualifiedInputLine(line)) continue;


            String[] elements = line.split("\\s*;\\s*");
            processedInsets.add(new MoneyInsets(elements[0], elements[1], elements[2]));
        }
    }


    private void constructInsetsForProcessing() {
        insetsForProcessing = new LinkedList<>();

        for (MoneyInsets m : fileInsets) {
            if (!processedInsets.contains(m)) insetsForProcessing.add(m);
        }
    }


    private void constructUnknownNameToUsername() {
        unknownNameToUsername = new HashMap<>();

        for (MoneyInsets m : insetsForProcessing) {
            if (!nameToUsername.containsKey(m.getFullName())) {
                TextField input = new TextField();
                input.setPromptText("Brukernavn");
                unknownNameToUsername.put(m.getFullName(), input);
            }
        }
    }


    private void setupGui() {
        // Setup name input
        int row = 0;
        nameMappingsTable.getChildren().clear();
        for (String name : unknownNameToUsername.keySet()) {
            nameMappingsTable.add(new Label(name), 0, row);
            nameMappingsTable.add(unknownNameToUsername.get(name), 1, row);
            row++;
        }

        StringBuilder outputText = new StringBuilder();
        outputText.append("Følgende transaksjoner vil bli lagt til:\n");
        for (MoneyInsets m : insetsForProcessing) {
            outputText.append(m.toString() + "\n");
        }
        outputTextField.setText(outputText.toString());
    }


    @FXML
    private void processInsets(ActionEvent event) {
        Iterator<MoneyInsets> iterator = insetsForProcessing.iterator();
        iterator.hasNext();

        while (iterator.hasNext()) {
            MoneyInsets m = iterator.next();
            String username = null;
            if (nameToUsername.containsKey(m.getFullName()))
                username = nameToUsername.get(m.getFullName());
            else username = unknownNameToUsername.get(m.getFullName()).getText();

            User user = kernel.getUserList().find(username);
            if (user == null) {
                Alert alert = new Alert(
                    Alert.AlertType.ERROR, "Kunne ikke finne bruker '" + username + "'");
                alert.showAndWait();
                saveNewProcessedInsetsFile();
                setupGui();
                return;
            } else if (!nameToUsername.containsKey(m.getFullName())) {
                nameToUsername.put(m.getFullName(), username);
                unknownNameToUsername.remove(m.getFullName());
            }


            kernel.getTransactionHandler().newMoneyInsert(user, Double.parseDouble(m.getAmount()));
            if (sendEmailsCheckBox.isSelected()) {
                try {
                    kernel.getEmailSender().sendMail(
                        user,
                        "Påfyll av penger",
                        TextTemplateLoader.getTemplate(TextTemplate.USERADDEDMONEY)
                    );
                } catch (MessagingException | InvalidEncryptionException e) {
                    kernel.getLogger().log(e);
                }
            }

            processedInsets.add(m);
            iterator.remove();
        }

        Main.getCurrentAdminController().repaintUserList();
        if (saveNewProcessedInsetsFile()) exit(event);
    }


    private boolean saveNewProcessedInsetsFile() {
        try {
            PrintWriter pw = new PrintWriter(new File(kernel.PROCESSEDINSETS_FILEPATH));

            pw.println("[KNOWN MAPPINGS]");
            for (String name : nameToUsername.keySet()) {
                pw.println(name + "=" + nameToUsername.get(name));
            }


            pw.println("\n[PROCESSED]");
            for (MoneyInsets m : processedInsets) {
                pw.println(m);
            }

            pw.close();
        } catch (FileNotFoundException e) {
            kernel.getLogger().log(e);
            Alert alert = new Alert(
                Alert.AlertType.ERROR, "Kunne ikke lagre ny statusfil. Kontakt administrator.");
            alert.setHeaderText("Feil i fillagring");
            alert.showAndWait();
            return false;
        }
        return true;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Finn XLS-fil");
        FileChooser.ExtensionFilter excelfiles = new FileChooser.ExtensionFilter("Excel regneark (*.xls)", "*.xls");
        FileChooser.ExtensionFilter all = new FileChooser.ExtensionFilter("Alle filer", "*");
        fileChooser.getExtensionFilters().addAll(excelfiles, all);
        fileChooser.setSelectedExtensionFilter(excelfiles);
        xlsFile = fileChooser.showOpenDialog(null);
        if (xlsFile == null) {
            outputTextField.setText("Ingen fil valgt");
            return;
        }

        try {
            loadXlsFile();
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }
        loadProcessed();
        constructInsetsForProcessing();
        constructUnknownNameToUsername();
        setupGui();
    }


    public class MoneyInsets {

        private String date;
        private String fullName;
        private String amount;


        public MoneyInsets(String date, String fullName, String amount) {
            this.date = date;
            this.fullName = fullName;
            this.amount = amount;
        }


        public MoneyInsets(Cell date, Cell fullName, Cell amount) {
            this(date.toString(), fullName.toString(), amount.toString());
        }


        public String getDate() {
            return date;
        }


        public String getFullName() {
            return fullName;
        }


        public String getAmount() {
            return amount;
        }


        @Override
        public int hashCode() {
            return Objects.hash(date, fullName, amount);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MoneyInsets that = (MoneyInsets) o;
            return Objects.equals(date, that.date) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(amount, that.amount);
        }


        @Override
        public String toString() {
            return date + ';' + fullName + ';' + amount;
        }
    }
}
