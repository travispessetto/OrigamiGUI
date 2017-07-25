package application.controllers;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pessetto.FileHandlers.Inbox.DeleteMessageListener;
import com.pessetto.FileHandlers.Inbox.Inbox;
import com.pessetto.FileHandlers.Inbox.Message;
import com.pessetto.FileHandlers.Inbox.NewMessageListener;
import com.pessetto.Variables.InboxVariables;

import application.listeners.TrayIconListener;
import application.settings.SettingsSingleton;
import application.tray.SystemTraySingleton;
import application.web.BrowserBridge;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class EmailController implements NewMessageListener, DeleteMessageListener
{

	private ObservableList<String> emailList;
	@FXML
	private ListView<String> emails = new ListView<>();
	@FXML
	private WebView webview;
	private WebEngine webengine;
	@FXML
	private Label smtpStatus;
	
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
			Scene scene = new Scene(settings,500,300);
			stage.setTitle("Origami SMTP Settings");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void initialize() throws Exception
	{
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
		URL emailRef = EmailController.class.getClassLoader().getResource("jqueryEmailPage.html");
		if(emailRef == null)
		{
			System.err.println("Bad HTML GUI Ref");
		}
		String emailExternalForm = emailRef.toExternalForm();
		webengine.load(emailExternalForm);
		loadEmails();
		if(SettingsSingleton.getInstance().smtpStarted())
		{
			smtpStatus.setText("Started");
		}
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
					System.out.println("Mouse clicked on " + selectedItem);
					try {
						Message message = inbox.getMessage(selectedItem);
						if(message.getHTMLMessage() != null)
						{
							loadEmailLineByLine(webengine,message.getHTMLMessage());
						}
						else
						{
							loadEmailLineByLine(webengine,message.getPlainMessage());
						}
					
					} catch (Exception e) {
						System.err.println("Could not open file");
						System.err.println(e.getMessage());
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
					webengine.loadContent("");
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
		Inbox inbox = Inbox.getInstance();
		for(int i = 0; i < inbox.getMessageCount(); ++i)
		{
			Message message = inbox.getMessage(i);
			String subject = message.getSubject();
			subjects.add(0,subject);
		}
		emailList = FXCollections.observableList(subjects);
		emails.setItems(emailList);
	}
	
	public void addEmailToList()
	{

		Platform.runLater(new Runnable()
		{
			@Override
			public void run() {
				SettingsSingleton settings = SettingsSingleton.getInstance();
				Inbox inbox = Inbox.getInstance();
				Message message = inbox.getNewestMessage();
				emailList.add(0,message.getSubject());
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
	
	public void updateStatus(boolean status)
	{
		
	}

	@Override
	public void messageRecieved()
	{
		addEmailToList();
	}
	
	private void loadEmailLineByLine(WebEngine engine, String message)
	{
		String[] lines = message.split("\\r?\\n");
		engine.executeScript("clearContent()");
		for(String line : lines)
		{
			line = line.replace("\"", "\\\"");
			System.out.println("Line: "+line);
			engine.executeScript("addContentLine(\""+line+ "\");");
		}
	}
}
