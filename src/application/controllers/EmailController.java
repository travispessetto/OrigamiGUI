package application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;

import application.watchers.DeleteFileWatcher;
import application.watchers.FileWatcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class EmailController {

	private ObservableList<String> emailList;
	@FXML
	private ListView<String> emails = new ListView<>();
	
	@FXML
	private void initialize() throws Exception
	{
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
						Runtime.getRuntime().exec("C:\\Program Files\\Internet Explorer\\iexplore.exe " + System.getProperty("user.dir") + "\\messages\\"+selectedItem);
					} catch (IOException e) {
						System.err.println("Could not open file");
						System.err.println(e.getMessage());
					}
				}
			}
			
		});
		
		emails.setOnKeyReleased(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				event.consume();
				KeyCode keyCode = event.getCode();
				if(keyCode == KeyCode.DELETE)
				{
					String selected = emails.getSelectionModel().getSelectedItem();
					selected = System.getProperty("user.dir") + "\\messages\\"+selected;
					File selectedFile = new File(selected);
					selectedFile.delete();
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
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fatal Error");;
			alert.setHeaderText(null);;
			alert.setContentText("Fatal Error: Messages folder not found.");
			alert.showAndWait();
			System.exit(1);
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
