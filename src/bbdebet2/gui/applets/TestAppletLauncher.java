/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.kernel.Kernel;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestAppletLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        Kernel kernel = new Kernel();
        Console.createAndDisplayDialog("kernel", kernel);
    }


}
