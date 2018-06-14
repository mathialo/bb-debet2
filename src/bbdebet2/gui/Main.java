/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui;

import bbdebet2.gui.controllers.AdminController;
import bbdebet2.gui.controllers.LoginController;
import bbdebet2.gui.controllers.UserController;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    public static final String SHORT_VERSION = "";
    public static final String FULL_VERSION = "";

    private static Kernel kernel;
    private static Stage primaryStage;

    private static Scene loginScene;
    private static Scene userScene;
    private static Scene adminScene;

    private static Parent loginRoot;
    private static Parent userRoot;
    private static Parent adminRoot;

    private static User activeUser;

    private static LoginController currentLoginController;
    private static UserController currentUserController;
    private static AdminController currentAdminController;

    private boolean errorsOccuredDuringStartup;
    private String errorMessage;


    public static LoginController getCurrentLoginController() {
        return currentLoginController;
    }


    public static void setCurrentLoginController(LoginController currentLoginController) {
        Main.currentLoginController = currentLoginController;
    }


    public static UserController getCurrentUserController() {
        return currentUserController;
    }


    public static void setCurrentUserController(UserController currentUserController) {
        Main.currentUserController = currentUserController;
    }


    public static AdminController getCurrentAdminController() {
        return currentAdminController;
    }


    public static void setCurrentAdminController(AdminController currentAdminController) {
        Main.currentAdminController = currentAdminController;
    }


    public static void toUserScreen() {
        primaryStage.setScene(userScene);
    }


    public static void toAdminScreen() {
        Main.getCurrentAdminController().repaintAll();
        primaryStage.setScene(adminScene);
    }


    public static void toLoginScreen() {
        Main.getCurrentLoginController().resetLogin();
        primaryStage.setScene(loginScene);
    }


    public static User getActiveUser() {
        return activeUser;
    }


    public static void setActiveUser(User activeUser) {
        Main.activeUser = activeUser;
    }


    public static void main(String[] args) {
        launch(args);
    }


    public static Kernel getKernel() {
        return kernel;
    }


    public static Scene getLoginScene() {
        return loginScene;
    }


    public static Scene getUserScene() {
        return userScene;
    }


    public static Scene getAdminScene() {
        return adminScene;
    }


    @Override
    public void init() throws Exception {
        super.init();
        try {
            kernel = new Kernel();
        } catch (IllegalStateException e) {
            errorsOccuredDuringStartup = true;
            errorMessage = e.getMessage();
        }
    }


    @Override
    public void start(Stage primaryStage) {
        if (errorsOccuredDuringStartup) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Feilmelding:\n" + errorMessage + "\n\nVil du likevel tvinge en oppstart?");
            alert.setHeaderText("Feil i startup");
            alert.getDialogPane().setPrefHeight(300);
            alert.getDialogPane().setPrefWidth(400);

            ButtonType buttonTypeYes = new ButtonType("Ja");
            ButtonType buttonTypeCancel = new ButtonType("Nei", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == buttonTypeYes) {
                kernel = new Kernel(true);
            } else {
                System.exit(1);
            }
        }

        Main.primaryStage = primaryStage;

        try {
            loginRoot = FXMLLoader.load(
                getClass().getClassLoader().getResource("bbdebet2/gui/views/LoginScreen.fxml"));
            loginScene = new Scene(loginRoot);

            userRoot = FXMLLoader.load(
                getClass().getClassLoader().getResource("bbdebet2/gui/views/UserScreen.fxml"));
            userScene = new Scene(userRoot);

            adminRoot = FXMLLoader.load(
                getClass().getClassLoader().getResource("bbdebet2/gui/views/AdminScreen.fxml"));
            adminScene = new Scene(adminRoot);

            primaryStage.setTitle("BBDebet " + SHORT_VERSION);

            primaryStage.getIcons().addAll(
                new Image("file:/usr/local/share/bbdebet2/img/bblogo_32.png"),
                new Image("file:/usr/local/share/bbdebet2/img/bblogo_64.png"),
                new Image("file:/usr/local/share/bbdebet2/img/bblogo_128.png"),
                new Image("file:/usr/local/share/bbdebet2/img/bblogo_256.png"),
                new Image("file:/usr/local/share/bbdebet2/img/bblogo_512.png")
            );

            primaryStage.setScene(loginScene);
            primaryStage.setHeight(1000);
            primaryStage.setWidth(1200);
            primaryStage.setMaximized(true);

            currentLoginController.postInitialize();
            currentUserController.postInitialize();
            currentAdminController.postInitialize();

            primaryStage.show();
        } catch (IOException e) {
            kernel.getLogger().log(e);
            Platform.exit();
        }
    }
}
