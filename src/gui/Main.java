/*
 * Copyright (c) 2018. Mathias Lohne
 */

package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kernel.Kernel;

import java.io.IOException;

public class Main extends Application {

    private static Kernel kernel;

    private Stage primaryStage;


    public static void main(String[] args) {
        launch(args);
    }


    public static Kernel getKernel() {
        return kernel;
    }


    @Override
    public void init() throws Exception {
        super.init();
        kernel = new Kernel();
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
            Scene scene = new Scene(loginRoot, 1200, 1000);

            primaryStage.setTitle("BBDebet 2.0.1");
            primaryStage.setScene(scene);
            // primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }
    }
}
