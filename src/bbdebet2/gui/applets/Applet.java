/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.applets;

import bbdebet2.gui.Main;
import bbdebet2.kernel.Kernel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Applet implements Initializable {

    protected Kernel kernel;


    protected static FXMLLoader createAndDisplayDialog(String title, String fxmlFileName) {
        try {
            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(NewUserTransaction.class.getClassLoader().getResource("bbdebet2/gui/views/" + fxmlFileName + ".fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            return fxmlLoader;
        } catch (IOException e) {
            Main.getKernel().getLogger().log(e);
        }
        return null;
    }


    protected void exit(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    protected void exit(Node node) {
        ((Stage) node.getScene().getWindow()).close();
    }


    protected void exit(Scene scene) {
        ((Stage) scene.getWindow()).close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.kernel = Main.getKernel();
    }
}
