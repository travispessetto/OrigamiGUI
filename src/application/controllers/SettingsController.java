package application.controllers;
import application.settings.SettingsSingleton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class SettingsController 
{
   @FXML
   protected TextField portNumber;
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
   protected void initialize()
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   portNumber.setText(Integer.toString(settings.getSMTPPort()));
   }
   
}
