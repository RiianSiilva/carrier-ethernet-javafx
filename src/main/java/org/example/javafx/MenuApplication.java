package org.example.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Carrier Ethernet");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }
}
