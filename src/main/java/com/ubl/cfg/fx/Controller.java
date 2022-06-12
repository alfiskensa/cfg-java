package com.ubl.cfg.fx;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubl.cfg.engine.PermissionFlowGraph;
import com.ubl.cfg.engine.SccSolver;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Controller {

	@FXML
	private MenuItem selectApk;
	
	@FXML
	private TextArea txtOutput;
	
	@FXML
	private ComboBox<String> cbDataset;
	
	@FXML
	private Button btGenerateCfg, btCreate, btShow;
	
    @FXML
    private RadioMenuItem darkTheme, lightTheme;
	
	@FXML
	private Label label;
	
	@FXML
    private VBox root;
	
	@FXML
	private RadioButton rbMalicious, rbBenign;
	
	private ToggleGroup toogle;
   
	private String apkFileLocation;
	
	private String data;
	
	private PermissionFlowGraph permissionFlowGraph;
	
	private PrintStream ps ;
	
	private Boolean clazz;
	
	@FXML
	public void initialize() {
		cbDataset.getItems().clear();
		cbDataset.getItems().addAll("API Usage Dataset", "API Frequence Dataset", "API Sequence Dataset");
		cbDataset.setOnAction(e -> {
			this.btShow.setDisable(true);
		});

		this.ps = new PrintStream(new Console(txtOutput));
		System.setOut(ps);
		System.setErr(ps);
		
		toogle = new ToggleGroup();
		rbBenign.setToggleGroup(toogle);
		rbMalicious.setToggleGroup(toogle);
	}
	
	public void selectApk() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Target APK File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Android APK", "*.apk"));
		File file = fileChooser.showOpenDialog(null);
		if(file != null) {
			this.apkFileLocation = file.getPath();
			StringBuilder builder = new StringBuilder();
			builder.append("Target APK File: \n");
			builder.append(this.apkFileLocation);
			label.setText(builder.toString());
		}
	}
	
	public void generateCfg() {
		if(this.apkFileLocation == null) {
			CommonUtils.infoBox("Select APK terlebih dahulu", "Error");
		} else {
			log.info("Generate CFG.....");
			this.permissionFlowGraph = SccSolver.generateCfg(apkFileLocation);
			log.info(permissionFlowGraph.printGraph());
		}	
	}
	
	public void createDataset() {
		this.btShow.setDisable(true);
		if(permissionFlowGraph == null) {
			CommonUtils.infoBox("Genarate CFG terlebih dahulu", "Error");
		} else {
			String dataset = cbDataset.getValue();
			if(dataset != null) {
				log.info("Creating dataset.....");
				if("API Usage Dataset".equals(dataset)) {
					this.data = this.permissionFlowGraph.ApiUsageDataset().stream().map(p -> p.getO2() ? "1" : "0")
							.collect(Collectors.joining(","));
				} else if("API Frequence Dataset".equals(dataset)) {
					this.data = this.permissionFlowGraph.ApiFrequenceDataset().stream().map(p -> "<"+p.getO1()+","+p.getO2().toString()+">")
					.collect(Collectors.joining(","));
					this.data += " \n"+this.permissionFlowGraph.ApiFrequenceDataset().stream().map(p -> p.getO2().toString()).collect(Collectors.joining(","));
				} else if("API Sequence Dataset".equals(dataset)) {
					this.data = this.permissionFlowGraph.ApiSequenceDataset().stream()
							.map(p -> p+1)
							.map(Object::toString).collect(Collectors.joining(","));
				} else {
					this.data = "";
				}
				log.info("Success create "+dataset);
				this.btShow.setDisable(false);
				RadioButton selectedRadioButton = (RadioButton) toogle.getSelectedToggle();
				String toogleGroupValue = selectedRadioButton.getText();
				this.data += ","+toogleGroupValue;
			} else {
				CommonUtils.infoBox("Dataset tidak boleh kosong", "Error");
			}
		}
		
	}
	
	public void showDataset() {
		log.info("Show dataset.....");
		CommonUtils.showDataset(data);
	}
	
	public void setTheme() {
		if (darkTheme.isSelected())
			root.getScene().getStylesheets().add(getClass().getResource("modena_dark.css").toExternalForm());
		if (lightTheme.isSelected())
			root.getScene().getStylesheets()
					.remove(getClass().getResource("modena_dark.css").toExternalForm());
	}
	
	public class Console extends OutputStream {
        private TextArea console;

        public Console(TextArea console) {
            this.console = console;
        }

        public void appendText(String valueOf) {
            Platform.runLater(() -> console.appendText(valueOf));
        }

        public void write(int b) throws IOException {
            appendText(String.valueOf((char)b));
        }
    }
	
}
