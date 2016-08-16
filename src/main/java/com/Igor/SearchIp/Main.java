package com.Igor.SearchIp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Created by igor on 02.08.16.
 */
public class Main extends Application {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
        primaryStage.setTitle("IP Manager");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }
}
