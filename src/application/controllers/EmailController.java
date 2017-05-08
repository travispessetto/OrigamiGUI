package application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;

import application.settings.SettingsSingleton;
import application.watchers.DeleteFileWatcher;
import application.watchers.FileWatcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class EmailController {

	private ObservableList<String> emailList;
	@FXML
	private ListView<String> emails = new ListView<>();
	@FXML
	private WebView webview;
	private WebEngine webengine;
	
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
		webengine = webview.getEngine();
		loadEmails();
		watchFolder();
		emails.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2 && !(event.isConsumed()))
				{
					event.consume();
					String selectedItem = emails.getSelectionModel().getSelectedItem();
					System.out.println("Mouse clicked on " + selectedItem);
					try {
						webengine.load("file:///"+System.getProperty("user.dir") + "\\messages\\"+selectedItem);
						System.out.println("Web engine loaded " + webengine.getLocation());
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
				KeyCode keyCode = event.getCode();
				if(keyCode == KeyCode.DELETE || keyCode == KeyCode.BACK_SPACE)
				{
					String selected = emails.getSelectionModel().getSelectedItem();
					selected = System.getProperty("user.dir") + "\\messages\\"+selected;
					File selectedFile = new File(selected);
					if(!selectedFile.canWrite())
					{
						Alert alert = new Alert(AlertType.ERROR);
					   alert.setTitle("File Error");
					   alert.setHeaderText(null);
					   alert.setContentText("Could not delete file as it is not writable");
					   alert.showAndWait();
					}
					selectedFile.delete();
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
		File file = new File("messages");
		if(file.exists())
		{
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(file.list()));
			emailList = FXCollections.observableArrayList(names);
			emails.setItems(emailList);	
		}
		else
		{
			try
			{
				file.setWritable(true,false);
				file.setReadable(true,false);
				file.mkdir();
				loadEmails();
			}
			catch(Exception ex)
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fatal Error");;
				alert.setHeaderText(null);;
				alert.setContentText("Fatal Error: Messages folder not found.");
				alert.showAndWait();
				System.exit(1);
			}
		}
	}
	
	private void watchFolder() throws IOException
	{
		Path messagePath = Paths.get("messages");
		WatchService watchService = messagePath.getFileSystem().newWatchService();
		messagePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		Thread watcher = new Thread(new FileWatcher(watchService,this));
		watcher.setDaemon(true);
		watcher.start();
		
		// Delete Watcher
		WatchService deleteWatchService = messagePath.getFileSystem().newWatchService();
		messagePath.register(deleteWatchService, StandardWatchEventKinds.ENTRY_DELETE);
		Thread deleteWatcher = new Thread(new DeleteFileWatcher(deleteWatchService,this));
		deleteWatcher.setDaemon(true);
		deleteWatcher.start();
	}
	
	public void addEmailToList(String email)
	{
		if(email != null)
		{
			Platform.runLater(new Runnable()
			{
				@Override
				public void run() {
					emailList.add(email);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("New Message");
					alert.setHeaderText(null);
					alert.setContentText("You have a new message");
					alert.showAndWait();	
				}
			});
		}
	}
	
	public void removeEmailFromList(String email)
	{
		Platform.runLater(new Runnable(){
			@Override
			public void run()
			{
				emailList.removeAll(email);
			}
		});
	}
}
