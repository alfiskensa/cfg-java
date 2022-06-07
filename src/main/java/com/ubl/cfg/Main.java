package com.ubl.cfg;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
        Parent root = FXMLLoader.load(getClass().getResource("Layout.fxml"));
//        Image icon = new Image(Controller.class.getResource("Resources/logo.png").toExternalForm(), false);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CFG Dataset Generator - Kelompok 1 SO Project");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
//        primaryStage.getIcons().add(icon);
        primaryStage.show();
	}

}
