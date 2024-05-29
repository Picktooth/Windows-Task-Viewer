package com.example;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Date;

public class LogViewer extends Application {
    public static void main(String[] args) {
        // Start the Activity Logger
        new Thread(ActivityLogger::getTasks).start();

        // Launch the JavaFX Application
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Activity Log Viewer");

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setEditable(false);

        VBox root = new VBox(textArea);
        Scene scene = new Scene(root, 600, 400);

        // Continuously update the displayed log file
        Thread logUpdaterThread = new Thread(() -> {
            File logFile = new File("C:/OS Project/activity_log.txt");

            while (true) {
                try {
                    StringBuilder logText = new StringBuilder();

                    if (logFile.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                logText.append(line).append("\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        logText.append("Log file not found.");
                    }

                    String finalLogText = logText.toString();

                    // Update the TextArea on the JavaFX Application Thread
                    Platform.runLater(() -> textArea.setText(finalLogText));

                    Thread.sleep(5 * 60 * 1000); // Sleep for 5 minutes (adjust as needed)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        logUpdaterThread.setDaemon(true);
        logUpdaterThread.start();

        stage.setScene(scene);
        stage.show();
    }
}

class ActivityLogger {
    public static void getTasks() {
        try {
            while (true) {
                Process p = Runtime.getRuntime().exec(new String[]{(System.getenv("windir") + "\\system32\\" + "tasklist.exe")});
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line;

                File log = new File("C:/OS Project/activity_log.txt");
                BufferedWriter w = new BufferedWriter(new FileWriter(log, true));

                while ((line = r.readLine()) != null) {
                    w.write("Time: " + new Date() + "\n");
                    w.write(line + "\n");
                }

                w.close();

                Thread.sleep(5 * 60 * 1000); // Sleep for 5 minutes (adjust as needed)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}