package com.ubl.cfg.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;

public class Controller {

	@FXML
	private MenuItem selectApk;
	
	@FXML
	private TextArea txtOutput;
	
	@FXML
	private ComboBox<String> cbDataset;
	
	@FXML
	private Button btGenerateCfg, btCreate;
	
    @FXML
    private RadioMenuItem darkTheme, lightTheme;
	
	@FXML
	private Label label;
	
	@FXML
	public void initialize() {
		cbDataset.getItems().clear();
		cbDataset.getItems().addAll("API Usage Dataset", "API Frequence Dataset", "API Sequence Dataset");
	}
	
	public void selectApk() {
		
	}
	
	public void setTheme() {
		
	}
	
}
