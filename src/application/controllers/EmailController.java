package application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;

import application.watchers.FileWatcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
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
	}
	
	public void loadEmails()
	{
		System.out.println("Load emails");
		File file = new File("messages");
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(file.list()));
		emailList = FXCollections.observableArrayList(names);
		emails.setItems(emailList);
	}
	
	private void watchFolder() throws IOException
	{
		Path messagePath = Paths.get("messages");
		WatchService watchService = messagePath.getFileSystem().newWatchService();
		messagePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		Thread watcher = new Thread(new FileWatcher(watchService,emailList));
		watcher.setDaemon(true);
		watcher.start();
	}
	
	public void addEmailToList(String email)
	{
		emailList.add(email);
	}
}
