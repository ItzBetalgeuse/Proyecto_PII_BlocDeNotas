package com.example.archivo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        stage.setTitle("Bloc de Notas");
        stage.setScene(new Scene(fxmlLoader.load(), 600, 400));
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icon.png"))));
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}