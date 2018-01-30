package application.controllers;
import java.io.File;
import java.util.LinkedList;

import application.settings.SettingsSingleton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SettingsController 
{
   @FXML
   protected TextField portNumber;
   
   @FXML
   protected TextField BrowserPath;
   
   @FXML
   protected CheckBox UsePrivateBrowsing;
   
   @FXML
   protected TextField smtpRemoteUser;
   
   @FXML
   protected PasswordField smtpRemotePassword;
   
   @FXML
   protected TextField smtpRemoteAddress;
   
   @FXML
   protected TextField smtpRemotePort;
   
   @FXML
   protected TableView forwardingEmails;
   
   private ObservableList<String> forwardingEmailAddresses;
   
   private LinkedList<String> emailList ;
   
   @FXML
   protected void addForwardingEmail(Event event)
   {
	   System.out.println("Adding email to forwarding list");
	   forwardingEmailAddresses.add("john.doe@example.com");
   }
   
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
   protected void applySMTPSettings(Event event)
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   settings.setSMTPPort(Integer.parseInt(portNumber.getText()));
	   settings.restartSMTPServer();
	   Alert alert = new Alert(AlertType.INFORMATION);
	   alert.setTitle("SMTP Settings");
	   alert.setHeaderText(null);
	   alert.setContentText("SMTP settings applied");
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
	   loadEmailAddresses();
   }
   
   private void loadEmailAddresses()
   { 
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   emailList = settings.getSmtpRemoteEmailList();
	   forwardingEmailAddresses = FXCollections.observableList(emailList);
	   forwardingEmails = new TableView();
	   forwardingEmails.setItems(forwardingEmailAddresses);
   }
   
}
