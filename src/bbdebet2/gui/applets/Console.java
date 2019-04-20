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
import bbdebet2.kernel.datastructs.CommandLineInterface;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Console extends Applet {

    private String command;
    private CommandLineInterface backend;
    private PrintStream responseStream;
    private ByteArrayOutputStream responseDestination;

    @FXML
    private TextArea textArea;


    public static void createAndDisplayDialog(String name, CommandLineInterface backend) {
        FXMLLoader loader = Applet.createAndDisplayDialog(name, "ConsoleScreen");
        ((Console) loader.getController()).setBackEnd(backend);
    }


    private void runCommand() {
        // Add new line
        println("");

        if (command.isEmpty()) {
            printPrompt();
            return;
        }

        // Execute command and print result
        if (backend != null) {
            backend.parseAndRunCommand(command, responseStream);
            print(responseDestination.toString());
            responseDestination.reset();
        } else {
            println("Error: no backend set!");
        }

        // Reset command variable
        command = "";

        // Print new prompt
        printPrompt();

        // Repaint GUI if necessary
        if (Main.getCurrentAdminController() != null) Main.getCurrentAdminController().repaintAll();
    }


    public void setBackEnd(CommandLineInterface backend) {
        this.backend = backend;
    }


    private void printPrompt() {
        print(">> ");
    }


    private void println(String line) {
        textArea.setText(textArea.getText() + line + "\n");
        textArea.positionCaret(textArea.getText().length());
    }


    private void print(String line) {
        textArea.setText(textArea.getText() + line);
        textArea.positionCaret(textArea.getText().length());
    }


    private boolean isValidTextKey(KeyCode code) {
        return code.isLetterKey()
            || code.isDigitKey()
            || code.isWhitespaceKey()
            || code == KeyCode.PERIOD
            || code == KeyCode.COMMA
            || code == KeyCode.SEMICOLON
            || code == KeyCode.QUOTE;
    }


    @FXML
    public void checkKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            runCommand();
        } else if (event.getCode() == KeyCode.BACK_SPACE && command.length() > 0) {
            String text = textArea.getText();
            textArea.setText(text.substring(0, text.length() - 1));
            command = command.substring(0, command.length()-1);
        } else if (isValidTextKey(event.getCode())) {
            textArea.setText(textArea.getText() + event.getText());
            command += event.getText();
        }
        textArea.positionCaret(textArea.getText().length());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        command = "";
        responseDestination = new ByteArrayOutputStream();
        responseStream = new PrintStream(responseDestination);
    }
}
