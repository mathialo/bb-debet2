/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui;

import bbdebet2.gui.controllers.AdminController;
import bbdebet2.gui.controllers.LoginController;
import bbdebet2.gui.controllers.UserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.User;

import java.io.IOException;

public class Main extends Application {

    private static Kernel kernel;
    private static Stage primaryStage;

    private static Scene loginScene;
    private static Scene userScene;
    private static Scene adminScene;

    private static User activeUser;

    private static LoginController currentLoginController;
    private static UserController currentUserController;
    private static AdminController currentAdminController;


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
        primaryStage.setScene(adminScene);
    }


    public static void toLoginScreen() {
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


    @Override
    public void init() throws Exception {
        super.init();
        kernel = new Kernel();
    }


    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;

        try {
            Parent loginRoot = FXMLLoader.load(getClass().getClassLoader().getResource("bbdebet2/gui/views/LoginScreen.fxml"));
            loginScene = new Scene(loginRoot, 1200, 1000);

            Parent userRoot = FXMLLoader.load(getClass().getClassLoader().getResource("bbdebet2/gui/views/UserScreen.fxml"));
            userScene = new Scene(userRoot, 1200, 1000);

            Parent adminRoot = FXMLLoader.load(getClass().getClassLoader().getResource("bbdebet2/gui/views/AdminScreen.fxml"));
            adminScene = new Scene(adminRoot, 1200, 1000);

            primaryStage.setTitle("BBDebet 2.0.1");
            primaryStage.setScene(loginScene);
            // primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            kernel.getLogger().log(e);
        }
    }
}
