package org.example.davidmesquitamod8assignment;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NetworkApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NetworkApplication.class.getResource("main_form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 580, 375);
        stage.setTitle("Test Server!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}