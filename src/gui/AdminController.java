/*
 * Copyright (c) 2018. Mathias Lohne
 */

package gui;

import javafx.fxml.Initializable;
import kernel.Kernel;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private Kernel kernel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kernel = Main.getKernel();
        Main.setCurrentAdminController(this);
    }
}
