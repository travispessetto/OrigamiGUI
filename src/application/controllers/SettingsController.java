package application.controllers;
import java.io.File;

import application.settings.SettingsSingleton;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

public class SettingsController 
{
   @FXML
   protected TextField portNumber;
   
   @FXML
   protected TextField BrowserPath;
   
   @FXML
   protected CheckBox UsePrivateBrowsing;
   
   @FXML
   protected void applyBrowserSettings(Event event)
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   settings.setBrowser(BrowserPath.getText());
	   settings.setPrivateBrowsing(UsePrivateBrowsing.isSelected());
	   Alert alert = new Alert(AlertType.INFORMATION);
	   alert.setTitle("Browser Settings");
	   alert.setHeaderText(null);
	   alert.setContentText("Browser settings applied");
	   alert.showAndWait();
	   settings.serialize();
   }
   
   @FXML
   protected void applyConnectionSettings(Event event)
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   settings.setSMTPPort(Integer.parseInt(portNumber.getText()));
	   settings.restartSMTPServer();
	   Alert alert = new Alert(AlertType.INFORMATION);
	   alert.setTitle("Connect Settings");
	   alert.setHeaderText(null);
	   alert.setContentText("Connection settings applied");
	   alert.showAndWait();
	   settings.serialize();
   }
   
   @FXML
   protected void browseForBrowser(Event event)
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   FileChooser browserChooser = new FileChooser();
	   browserChooser.setTitle("Choose a browser");
	   File selectedFile = browserChooser.showOpenDialog(null);
	   if(selectedFile != null)
	   {
		   Platform.runLater(new Runnable(){
			@Override
			public void run() {
				BrowserPath.setText(selectedFile.getAbsolutePath());	
			}});
	   }
   }
   
   
   @FXML
   protected void initialize()
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   portNumber.setText(Integer.toString(settings.getSMTPPort()));
	   UsePrivateBrowsing.setSelected(settings.getPrivateBrowsing());
	   BrowserPath.setText(settings.getBrowser());
   }
   
}
