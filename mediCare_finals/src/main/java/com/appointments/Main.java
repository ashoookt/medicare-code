package com.appointments;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
        @Override
        public void start(Stage primaryStage) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/mc_login.fxml")));
            primaryStage.setTitle("MediCare");
            primaryStage.getIcons().add(new Image("/pics/logo.png"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    public static void main(String[] args) {
        DatabaseController dc = new DatabaseController();
        launch(args);
    }
}
