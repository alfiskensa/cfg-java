package com.ubl.cfg.fx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CommonUtils {

	public static void infoBox(String infoMessage, String titleBar) {
		/*
		 * By specifying a null headerMessage String, we cause the dialog to not have a
		 * header
		 */
		infoBox(infoMessage, titleBar, null);
	}

	public static void infoBox(String infoMessage, String titleBar, String headerMessage) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titleBar);
		alert.setHeaderText(headerMessage);
		alert.setContentText(infoMessage);
		alert.showAndWait();
	}
	
	
	public static void showDataset(String data) {
		Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        
        TextArea text = new TextArea();
        text.setEditable(false);
        text.setFont(new Font(15));
        text.setText(data);
        
		BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setCenter(text);

        Scene scene = new Scene(layout, 1000, 200);
        window.setScene(scene);
        window.setTitle("Generated Dataset");
        window.showAndWait();
	}
}
