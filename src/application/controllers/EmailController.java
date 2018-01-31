package application.controllers;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pessetto.FileHandlers.Inbox.Attachment;
import com.pessetto.FileHandlers.Inbox.DeleteMessageListener;
import com.pessetto.FileHandlers.Inbox.Inbox;
import com.pessetto.FileHandlers.Inbox.Message;
import com.pessetto.FileHandlers.Inbox.NewMessageListener;
import com.pessetto.Variables.InboxVariables;
import com.sun.mail.smtp.SMTPTransport;

import application.debug.DebugLogSingleton;
import application.email.ForwardingAddress;
import application.gui.Email;
import application.listeners.SMTPStatusListener;
import application.listeners.TrayIconListener;
import application.settings.SettingsSingleton;
import application.tray.SystemTraySingleton;
import application.web.BrowserBridge;
import application.web.ResourceLoader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class EmailController implements NewMessageListener,
DeleteMessageListener, SMTPStatusListener
{

	private DebugLogSingleton debugLog;
	private ObservableList<Email> emailList;
	@FXML
	private TableView emails = new TableView<String>();
	@FXML
	private TableColumn emailsColumn = new TableColumn<Email,String>();
	@FXML
	private WebView webview;
	private WebEngine webengine;
	@FXML
	private Label smtpStatus;
	private Message selectedMessage;
	
	public Message getSelectedMessage() {
		return selectedMessage;
	}

	@FXML
	protected void handleExitMenuItemClicked(ActionEvent event)
	{
		SettingsSingleton.getInstance().stopSMTPServer();
		Platform.exit();
	}
	
	@FXML
	protected void handleServerSettingsMenuItemClicked(ActionEvent event)
	{
		try {
			Stage stage = new Stage();
			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("origami.png")));
			AnchorPane settings = FXMLLoader.load(getClass().getClassLoader().getResource("ServerSettings.fxml"));
			Scene scene = new Scene(settings);
			stage.setTitle("Origami SMTP Settings");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	protected void handleDebugMenuClicked(ActionEvent event)
	{
		showDebugConsole();
	}
	
	@FXML
	private void initialize() throws Exception
	{
		debugLog = DebugLogSingleton.getInstance();
		SettingsSingleton settings = SettingsSingleton.getInstance();
		settings.addSmtpStatusListener(this);
		settings.startSMTPServer();
		Inbox inbox = Inbox.getInstance();
		inbox.addNewMessageListener(this);
		inbox.addDeleteMessageListener(this);
		BrowserBridge bridge = new BrowserBridge(this);
		webengine = webview.getEngine();
		webengine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>(){
			public void changed(ObservableValue ov, State oldState, State newState)
			{
				JSObject win = (JSObject) webengine.executeScript("window");
				win.setMember("app",bridge);
			}
		});
		InputStream emailHTMLHandlerStream = EmailController.class.getClassLoader().getResourceAsStream("jqueryEmailPage.html");
		String emailExternalForm = ResourceLoader.loadFile(emailHTMLHandlerStream);
		webengine.loadContent(emailExternalForm, "text/html");
		emails.setPlaceholder(new Label("No messages"));
		emailsColumn.setCellValueFactory(new PropertyValueFactory<Email,String>("subject"));
		loadEmails();
		//watchFolder();
		emails.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				// if want double click ad event.getClickCount() == 2 to condition
				if(!event.isConsumed())
				{
					event.consume();
					Inbox inbox = Inbox.getInstance();
					int selectedItem = emails.getSelectionModel().getSelectedIndex();
					try {
						Message message = inbox.getMessage(selectedItem);
						selectedMessage = message;
						if(message.getHTMLMessage() != null)
						{
							loadEmail(webengine,message.getHTMLMessage());
						}
						else
						{
							loadEmail(webengine,message.getPlainMessage());
						}
						loadAttachments(webengine,message);
					
					} catch (Exception e) {
						System.out.println("Could not open file");
						System.out.println(e.getMessage());
						e.printStackTrace(System.err);
					}
				}
			}
			
		});
		
		emails.setOnKeyReleased(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				event.consume();
				Inbox inbox = Inbox.getInstance();
				KeyCode keyCode = event.getCode();
				if(keyCode == KeyCode.DELETE || keyCode == KeyCode.BACK_SPACE)
				{
					int selected = emails.getSelectionModel().getSelectedIndex();
					inbox.deleteMessage(selected);
					loadEmail(webengine,"");
					loadAttachments(webengine,null);
					selectedMessage = null;
				}
				else
				{
					System.err.println("Keycode " + keyCode + " not recognized");
				}
			}
			
		});
		
	}
	
	public void loadEmails()
	{
		System.out.println("Load emails");
		LinkedList subjects = new LinkedList();
		emailList = FXCollections.observableList(subjects);
		emails.setItems(emailList);
		Inbox inbox = Inbox.getInstance();
		for(int i = 0; i < inbox.getMessageCount(); ++i)
		{
			Message message = inbox.getMessage(i);
			addMessageToList(message);
		}
	}
	
	public void handleNewEmail()
	{

		Platform.runLater(new Runnable()
		{
			@Override
			public void run() {
				SettingsSingleton settings = SettingsSingleton.getInstance();
				Inbox inbox = Inbox.getInstance();
				Message message = inbox.getNewestMessage();
				addMessageToList(message);
				forwardMessage(message);
				if(settings.getMinimizeToTray())
				{
					SystemTraySingleton systemTray = SystemTraySingleton.getInstance();
					systemTray.displayMessage("New Message", "You have a new message", MessageType.INFO);
				}
				else
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("New Message");
					alert.setHeaderText(null);
					alert.setContentText("You have a new message");
					alert.showAndWait();	
				}
			}
		});
		
	}
	
	
	public void removeEmail(int index)
	{
		Platform.runLater(new Runnable(){
			@Override
			public void run()
			{
				emailList.remove(index);
			}
		});
	}
	
	public void showAbout()
	{
		try 
		{
			Stage stage = new Stage();
			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("origami.png")));
			AnchorPane settings = FXMLLoader.load(getClass().getClassLoader().getResource("About.fxml"));
			Scene scene = new Scene(settings,600,221);
			stage.setTitle("About Origami SMTP");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showDebugConsole()
	{
		try 
		{
			System.out.println("Show debug console");
			Stage stage = new Stage();
			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("origami.png")));
			AnchorPane debug = FXMLLoader.load(getClass().getClassLoader().getResource("DebugConsole.fxml"));
			Scene scene = new Scene(debug,600,221);
			stage.setTitle("Debug Console Origami SMTP");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showAlert(AlertType type,String title, String message)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public void updateStatus(boolean status)
	{
		
	}

	@Override
	public void messageRecieved()
	{
		handleNewEmail();
	}
	
	private void loadEmail(WebEngine engine, String message)
	{
		String scrubbedContent = message.replaceAll("\\r?\\n","\\\\n");
		scrubbedContent = scrubbedContent.replace("\"", "\\\"");
		engine.executeScript("clearContent()");
		engine.executeScript("setContent(\""+scrubbedContent+ "\");");
	}
	
	private void loadAttachments(WebEngine engine, Message message)
	{
		if(message != null)
		{
			LinkedList<Attachment> attachments = message.getAttachments();
			int index = 0;
			engine.executeScript("clearAttachments();");
			for(Attachment attach : attachments)
			{
				engine.executeScript("addAttachment('"+attach.getFileName()+"',"+index+");");
				++index;
			}
		}
		else
		{
			engine.executeScript("clearAttachments();");
		}
	}

	@Override
	public void smtpStatusChanged(boolean started) 
	{
		SettingsSingleton settings = SettingsSingleton.getInstance();
		Platform.runLater(new Runnable()
				{

					@Override
					public void run() 
					{
						if(started)
						{
							smtpStatus.setText("Started on port " + settings.getSMTPPort());
							smtpStatus.setTextFill(Color.DARKGREEN);
						}
						else
						{
							smtpStatus.setText("Stopped");
							smtpStatus.setTextFill(Color.DARKRED);
						}						
					}
			
				});
	}
	
	private void addMessageToList(Message message)
	{
		Email email = new Email();
		email.setTo(message.getTo());
		email.setFrom(message.getFrom());
		email.setSubject(message.getSubject());
		emailList.add(0,email);
	}
	
	private void forwardMessage(Message message)
	{
		System.out.println("Attempting to forward message");
		try {
			SettingsSingleton settings = SettingsSingleton.getInstance();
			if(settings.isSmtpForwardToRemote() && settings.getSmtpRemoteEmailList().size() > 0)
			{
				Properties props = System.getProperties();
				props.put("mail.smtps.host",settings.getSmtpRemoteAddress());
				props.put("mail.smtp.port", settings.getSMTPPort());
				String authorize = "false";
				String remoteUser = settings.getSmtpRemoteUserName();
				if(remoteUser != null && !remoteUser.trim().isEmpty())
				{
					authorize = "true";
				}
				props.put("mail.smtps.auth",authorize);
				Session session = Session.getInstance(props,null);
				MimeMessage sendingMessage = new MimeMessage(session);
				sendingMessage.setFrom(new InternetAddress(message.getFrom()));
				for(ForwardingAddress forwardingAddress : settings.getSmtpRemoteEmailList())
				{
					sendingMessage.addRecipients(javax.mail.Message.RecipientType.BCC, InternetAddress.parse(forwardingAddress.getAddress(),false));
				}
				sendingMessage.setSubject(message.getSubject());
				sendingMessage.setHeader("X-Mailer", "Origami SMTP");
				sendingMessage.setSentDate(new Date());
				BodyPart messageBodyPart = new MimeBodyPart();
				if(message.getHTMLMessage() != null)
				{
					messageBodyPart.setContent(message.getHTMLMessage(),"text/html");
				}
				else
				{
					messageBodyPart.setText(message.getPlainMessage());
				}
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				LinkedList<Attachment> attachments = message.getAttachments();
				for(Attachment attachment : attachments)
				{
					messageBodyPart = new MimeBodyPart();
					String mimeType = attachment.getMimeType();
					System.out.println("Mime Type is: " + mimeType);
					ByteArrayDataSource byteDS = new ByteArrayDataSource(attachment.getContent(),attachment.getMimeType());
					messageBodyPart.setDataHandler(new DataHandler(byteDS));
					messageBodyPart.setFileName(attachment.getFileName());
					multipart.addBodyPart(messageBodyPart);
				}
				sendingMessage.setContent(multipart);
				SMTPTransport smtpTransport = (SMTPTransport)session.getTransport("smtps");
				smtpTransport.connect(settings.getSmtpRemoteAddress(),settings.getSmtpRemoteUserName(),settings.getSmtpRemoteUserPassword());
				smtpTransport.sendMessage(sendingMessage, sendingMessage.getAllRecipients());
				System.out.println("Remote SMTP Server Response: " + smtpTransport.getLastServerResponse());
				smtpTransport.close();
			}
			else
			{
				System.out.println("Message not forwarded due to lack of forwarding email addresses");
			}
		} catch (MessagingException e)
		{
			showAlert(AlertType.ERROR,"Failed to forward",e.getMessage());
			e.printStackTrace();
		}
	}
	
}
