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

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.gui.modelwrappers.ViewTransactionForAddition;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.accounting.Account;
import bbdebet2.kernel.accounting.Expense;
import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.User;
import bbdebet2.kernel.mailing.InvalidEncryptionException;
import bbdebet2.kernel.mailing.TextTemplate;
import bbdebet2.kernel.mailing.TextTemplateLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;


public class MakeExpense extends Applet {

    @FXML
    private ChoiceBox<Account> accountChoiceBox;
    @FXML
    private TextField amountTextField;

    @FXML
    private TableView<ViewTransactionForAddition> transactionTableView;
    @FXML
    private TableColumn<ViewTransactionForAddition, String> accountTableColumn;
    @FXML
    private TableColumn<ViewTransactionForAddition, String> amountTableColumn;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private ChoiceBox<Account> initialFromAccountChoiceBox;
    @FXML
    private TextField initialUsernameTextField;
    @FXML
    private TextField initialAmountTextField;

    private ArrayList<ChoiceBox<Account>> fromAccountChoiceBoxes;
    private ArrayList<TextField> usernameTextFields;
    private ArrayList<TextField> amountTextFields;

    @FXML
    private Button addRowButton;

    @FXML
    private List<Button> removeRowButtons;

    @FXML
    private Label totalExpenseLabel;
    private double totalExpense;

    @FXML
    private GridPane paymentSources;

    private Account userAccount;


    public static void createAndDisplayDialog(List<Expense.Transaction> initialTransactions) {
        FXMLLoader loader = Applet.createAndDisplayDialog("Før utlegg", "MakeExpenseView");
        ((MakeExpense) loader.getController()).addAll(initialTransactions);
    }


    protected void add(Expense.Transaction transaction) {
        if (transaction != null) {
            transactionTableView.getItems().add(new ViewTransactionForAddition(transaction));
            totalExpense += transaction.getAmount();
            totalExpenseLabel.setText("Totalt utlegg: " + CurrencyFormatter.format(totalExpense));
        }
    }
    protected void addAll(Collection<Expense.Transaction> transactions) {
        if (transactions != null) transactions.forEach(this::add);
    }


    private void payBackUser(User user, double amount) {
        kernel.getTransactionHandler().newMoneyInsert(user, amount);

        Task sendEmailTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    kernel.getEmailSender().sendMail(user, "Påfyll av penger", TextTemplateLoader.getTemplate(TextTemplate.USERADDEDMONEY));
                } catch (MessagingException | InvalidEncryptionException e) {
                    Kernel.getLogger().log(e);
                }
                return null;
            }
        };
        new Thread(sendEmailTask).start();
    }


    @FXML
    private void saveExpense(ActionEvent event) {
        for (ChoiceBox<Account> accountChoiceBox : fromAccountChoiceBoxes) {
            if (accountChoiceBox.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Du må velge hvilken konto som dekker utlegget");
                alert.setHeaderText("Ingen betalingsmåte");
                alert.showAndWait();
                return;
            }
        }

        try {
        List<Expense.Transaction> toTransactions = transactionTableView.getItems().stream().map(ViewTransactionForAddition::getTransactionObject).collect(Collectors.toList());

        double expenseSize = toTransactions.stream().mapToDouble(Expense.Transaction::getAmount).sum();
        double covered = 0;

        List<Expense.Transaction> fromTransactions = new LinkedList<>();
        Map<User, Double> payOut = new HashMap<>();

        for (int i = 0; i < fromAccountChoiceBoxes.size(); i++) {
            Account fromAccount = fromAccountChoiceBoxes.get(i).getSelectionModel().getSelectedItem();
            double amount;

            if (i == fromAccountChoiceBoxes.size() - 1) {
                amount = expenseSize - covered;
            } else {
                amount = Double.parseDouble(amountTextFields.get(i).getText());
            }

            fromTransactions.add(new Expense.Transaction(
                fromAccount,
                amount,
                Expense.TransactionType.SUB
            ));

            covered += amount;

            if (fromAccount == userAccount) {
                String error = null;
                String username = usernameTextFields.get(i).getText().trim();

                if (username.equals("")) {
                    error = "Du må skrive et brukernavn for å velge " + fromAccount + " fom betalingsmåte";
                }
                if (!kernel.getUserList().contains(username)) {
                    error = "Finner ikke bruker " + username;
                }

                if (error != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, error);
                    alert.setHeaderText("Feil i brukernavn");
                    alert.showAndWait();
                    return;
                }

                User user = kernel.getUserList().find(username);

                if (payOut.containsKey(user)) {
                    double previous = payOut.get(user);
                    payOut.put(user, previous + amount);
                } else {
                    payOut.put(user, amount);
                }
            }
        }

        payOut.forEach(this::payBackUser);

        Expense expense = new Expense(commentTextArea.getText());
        fromTransactions.forEach(expense::addTransaction);
        toTransactions.forEach(expense::addTransaction);
        kernel.getLedger().add(expense);
        Kernel.getLogger().log("Adding expense: " + expense);

        Main.getCurrentAdminController().repaintUserList();
        Main.getCurrentAdminController().repaintAccounting();

        exit(event);

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Ikke gyldig tall");
            alert.showAndWait();
        }
    }


    private void removeRow(ActionEvent parentEvent, int row) {
        Set<Node> deleteNodes = new HashSet<>();

        for (Node child : paymentSources.getChildren()) {
            // get index from child
            Integer i = GridPane.getRowIndex(child);

            // handle null values for index=0
            int thisRow = i == null ? 0 : i;

            if (thisRow > row) {
                // decrement rows for rows after the deleted row
                GridPane.setRowIndex(child, thisRow - 1);
            } else if (thisRow == row) {
                // collect matching rows for deletion
                deleteNodes.add(child);
            }
        }

        // remove nodes from row
        if (!deleteNodes.isEmpty()) {
            paymentSources.getChildren().removeAll(deleteNodes);

            fromAccountChoiceBoxes.remove(row);
            amountTextFields.remove(row);
            usernameTextFields.remove(row);
            removeRowButtons.remove(0);

            Stage stage = (Stage) ((Node) parentEvent.getSource()).getScene().getWindow();
            stage.setHeight(stage.getHeight() - initialFromAccountChoiceBox.getHeight());

            amountTextFields.get(amountTextFields.size() - 1).setDisable(true);
        }
    }


    @FXML
    private void addRow(ActionEvent event) {
        ChoiceBox<Account> accountChoiceBox = new ChoiceBox<>();
        accountChoiceBox.setPrefWidth(initialFromAccountChoiceBox.getWidth());
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Brukernavn");
        usernameTextField.setDisable(true);
        TextField amountTextField = new TextField();
        amountTextField.setPromptText("Beløp");
        amountTextField.setDisable(true);

        setupFromChoiceBox(accountChoiceBox, usernameTextField);
        amountTextFields.get(amountTextFields.size() - 1).setDisable(false);

        Button removeRowButton = new Button("-");
        removeRowButton.setPrefWidth(40);
        removeRowButtons.add(removeRowButton);
        removeRowButton.setOnAction(ignore -> {
            Integer i = GridPane.getRowIndex(accountChoiceBox);
            int thisRow = i == null ? 0 : i;
            removeRow(event, thisRow);
        });

        fromAccountChoiceBoxes.add(accountChoiceBox);
        usernameTextFields.add(usernameTextField);
        amountTextFields.add(amountTextField);


        paymentSources.getChildren().remove(addRowButton);
        paymentSources.addRow(fromAccountChoiceBoxes.size() - 1, accountChoiceBox, usernameTextField, amountTextField, removeRowButton);

        paymentSources.add(addRowButton, 3, fromAccountChoiceBoxes.size());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setHeight(stage.getHeight() + initialFromAccountChoiceBox.getHeight());
    }


    @FXML
    private void handleAddTransaction(ActionEvent event) {
        Account account = accountChoiceBox.getSelectionModel().getSelectedItem();
        if (account == null) {
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountTextField.getText().replaceAll(",", "."));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Ikke gyldig tall");
            alert.showAndWait();
            return;
        }

        add(new Expense.Transaction(account, amount, Expense.TransactionType.ADD));
    }


    private void setupTableView() {
        accountTableColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        amountTableColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }


    private void setupFromChoiceBox(ChoiceBox<Account> choiceBox, TextField linkedTextField) {
        choiceBox.getItems().addAll(kernel.getAccounts().getPaymentOptions());

        choiceBox.setOnAction(
            e -> linkedTextField.setDisable(choiceBox.getSelectionModel().getSelectedItem() != userAccount)
        );
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupTableView();
        accountChoiceBox.getItems().addAll(kernel.getAccounts().getAll());
        setupFromChoiceBox(initialFromAccountChoiceBox, initialUsernameTextField);

        fromAccountChoiceBoxes = new ArrayList<>();
        fromAccountChoiceBoxes.add(initialFromAccountChoiceBox);
        usernameTextFields = new ArrayList<>();
        usernameTextFields.add(initialUsernameTextField);
        amountTextFields = new ArrayList<>();
        amountTextFields.add(initialAmountTextField);

        removeRowButtons = new LinkedList<>();

        userAccount = kernel.getAccounts().fromAccountNumber(2000);
    }
}
