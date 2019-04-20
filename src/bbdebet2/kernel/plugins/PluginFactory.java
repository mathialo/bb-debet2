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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class PluginFactory {

    public static Plugin loadPlugin(File pluginDir) throws IOException {
        Map<String, String> properties = readProperties(findPluginProperties(pluginDir));

        return new PluginImplementation(properties, pluginDir);
    }


    private static File findPluginProperties(File pluginDir) throws IOException {
        for (File file : pluginDir.listFiles()) {
            if (file.getName().endsWith(".bbd2plugin")) return file;
        }

        throw new FileNotFoundException("No plugin file in " + pluginDir.getName() + " directory");
    }


    private static Map<String, String> readProperties(File file) throws IOException {
        Scanner sc = new Scanner(file);
        Map<String, String> properties = new HashMap<>();

        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split("\\s*=\\s*");
            try {
                properties.put(line[0], line[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IOException("Incomplete plugin file for " + file.getPath());
            }
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
                dialog.close();
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
