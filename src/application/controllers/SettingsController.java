package application.controllers;
import java.io.File;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import application.email.ForwardingAddress;
import application.settings.SettingsSingleton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
   
   @FXML
   protected TableColumn forwardingEmailsColumn;
   
   @FXML
   protected CheckBox forwardMessages;
   
   private ObservableList<ForwardingAddress> forwardingEmailAddresses;
   
   private LinkedList<ForwardingAddress> emailList ;
   
   public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
   
   @FXML
   protected void addForwardingEmail(Event event)
   {
	   TextInputDialog dialog = new TextInputDialog("john.doe@example.com");
	   dialog.setTitle("Forwarding Email Address");
	   dialog.setHeaderText("What address do you wish to forward to?");
	   dialog.setContentText("Email:");
	   Optional<String> result = dialog.showAndWait();
	   if(result.isPresent())
	   {
		   String address = result.get();
		   Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(address);
		   if(matcher.find())
		   {
			   forwardingEmailAddresses.add(new ForwardingAddress(address));
		   }
		   else
		   {
			   showAlert("That is not a valid email");
		   }
	   }
   }
   
   @FXML
   protected void removeForwardingEmail(Event event)
   {
	   int index = forwardingEmails.getSelectionModel().getSelectedIndex();
	   if(index >= 0)
	   {
		   forwardingEmailAddresses.remove(index);
	   }
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
	   saveLocalSmtpSettings();
	   saveRemoteSmtpSettings();
	   
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
	   loadLocalSmtpSettings();
	   loadRemoteSmtpSettings();
	   loadBrowserSettings();
   }
   
   private void saveLocalSmtpSettings()
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   int oldSMTPPort = settings.getSMTPPort();
	   int newSMTPPort = Integer.parseInt(portNumber.getText());
	   if(oldSMTPPort != newSMTPPort)
	   {
		   settings.setSMTPPort(newSMTPPort);
		   settings.restartSMTPServer();
	   }
   }
   
   private void saveRemoteSmtpSettings()
   {
	   String remoteAddressText = smtpRemoteAddress.getText();
	   if(remoteAddressText == null || remoteAddressText.trim().isEmpty())
	   {
		   showAlert("SMTP remote settings not applied.  SMTP remote address required to forward");
	   }
	   else
	   {
		   SettingsSingleton settings = SettingsSingleton.getInstance();
		   settings.setSmtpRemoteEmailList(emailList);
		   settings.setSmtpRemoteUserName(smtpRemoteUser.getText());
		   settings.setSmtpRemoteUserPassword(smtpRemotePassword.getText());
		   settings.setSmtpRemotePort(Integer.parseInt(smtpRemotePort.getText()));
		   settings.setSmtpRemoteAddress(remoteAddressText);
		   settings.setSmtpForwardToRemote(forwardMessages.isSelected());
		   System.out.println("Forward  messages?:" + forwardMessages.isSelected());
	   }
   }
   
   private void loadLocalSmtpSettings()
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   portNumber.setText(Integer.toString(settings.getSMTPPort()));
   }
   
   private void loadBrowserSettings()
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   UsePrivateBrowsing.setSelected(settings.getPrivateBrowsing());
	   BrowserPath.setText(settings.getBrowser());
   }
   
   private void loadRemoteSmtpSettings()
   {
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   smtpRemoteUser.setText(settings.getSmtpRemoteUserName());
	   smtpRemotePassword.setText(settings.getSmtpRemoteUserPassword());
	   smtpRemotePort.setText(Integer.toString(settings.getSmtpRemotePort()));
	   smtpRemoteAddress.setText(settings.getSmtpRemoteAddress());
	   forwardMessages.setSelected(settings.isSmtpForwardToRemote());
	   loadEmailAddresses();
   }
   
   private void loadEmailAddresses()
   { 
	   System.out.println("load email addresses into settings");
	   SettingsSingleton settings = SettingsSingleton.getInstance();
	   emailList = settings.getSmtpRemoteEmailList();
	   if(emailList == null)
	   {
		   System.out.println("ERROR emailList is null in settings controller");
	   }
	   forwardingEmailAddresses = FXCollections.observableList(emailList);
	   if(forwardingEmailAddresses == null)
	   {
		   System.out.println("ERROR forwardingEmailAddresses is null in settings controller");
	   }
	   if(forwardingEmailsColumn == null)
	   {
		   System.out.println("ERROR forwardingEmailsColumn is null in settings controller");
	   }
	   forwardingEmailsColumn.setCellValueFactory(new PropertyValueFactory<ForwardingAddress,String>("Address"));
	   if(forwardingEmails == null)
	   {
		   System.out.println("ERROR forwardingEmails is null when it should not be in settings controller");
	   }
	   forwardingEmails.setItems(forwardingEmailAddresses);
   }
   
   private void showAlert(String message)
   {
	   Alert alert = new Alert(AlertType.ERROR,message,ButtonType.OK);
	   alert.showAndWait();
   }
   
}
