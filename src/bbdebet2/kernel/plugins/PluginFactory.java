/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.plugins;

import bbdebet2.gui.customelements.WaitingDialog;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class PluginFactory {

    public static Plugin loadPlugin(File pluginDir) throws FileNotFoundException {
        Map<String, String> properties = readProperties(findPluginProperties(pluginDir));

        return new PluginImplementation(properties, pluginDir);
    }


    private static File findPluginProperties(File pluginDir) throws FileNotFoundException {
        for (File file : pluginDir.listFiles()) {
            if (file.getName().endsWith(".bbd2plugin")) return file;
        }

        throw new FileNotFoundException("No plugin file in " + pluginDir.getName() + " directory");
    }


    private static Map<String, String> readProperties(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        Map<String, String> properties = new HashMap<>();

        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split("\\s*=\\s*");
            properties.put(line[0], line[1]);
        }

        return properties;
    }


    private static class PluginImplementation implements Plugin {

        private Map<String, String> properties;
        private String output;
        private File pluginDir;

        private EventHandler<WorkerStateEvent> onDone;


        public PluginImplementation(Map<String, String> properties, File pluginDir) {
            this.properties = properties;
            this.pluginDir = pluginDir;
        }


        @Override
        public void run() throws Exception {
            WaitingDialog dialog = new WaitingDialog("Kjører beregninger");
            if (properties.getOrDefault("type", "").equals("computation")) {
                dialog.show();
            }

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    ProcessBuilder processBuilder = new ProcessBuilder(
                        "bash",
                        properties.get("executable")
                    );

                    processBuilder.directory(pluginDir);
                    Process process = processBuilder.start();

                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                    );

                    String line = null;
                    StringBuilder outputBuilder = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        outputBuilder.append(line).append("\n");
                    }

                    output = outputBuilder.toString();

                    return null;
                }
            };

            task.setOnSucceeded(event -> {
                dialog.close();
                displayOutput();
            });

            AtomicReference<Exception> e = new AtomicReference<>();
            task.setOnFailed((WorkerStateEvent event) -> {
                e.set((Exception) event.getSource().getException());
            });
            if (e.get() != null) throw e.get();

            new Thread(task).start();
        }


        @Override
        public String getOutput() {
            return output;
        }


        @Override
        public String getOutputMethod() {
            return properties.getOrDefault("outputtype", null);
        }


        @Override
        public void setOnSucceed(EventHandler<WorkerStateEvent> handler) {
            onDone = handler;
        }


        private void displayOutput() {
            if (properties.get("outputtype").equals("text")) {
                Stage stage = new Stage();
                TextArea textArea = new TextArea(output);
                textArea.setFont(new Font("DejaVu Sans Mono", 13));
                textArea.setWrapText(true);
                Scene scene = new Scene(textArea, 600, 400);
                stage.setScene(scene);
                stage.setTitle(properties.get("name"));
                stage.show();
            }
        }


        @Override
        public String toString() {
            return properties.get("name");
        }
    }
}
