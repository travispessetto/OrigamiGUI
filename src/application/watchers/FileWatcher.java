package application.watchers;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FileWatcher extends Task<Void>{
	private WatchService watchService;
	private ObservableList<String> emailList;
	public FileWatcher(WatchService service, ObservableList<String> list)
	{
		watchService = service;
		emailList = list;
	}

	@Override
	protected Void call() throws Exception {
		try
		{
			WatchKey key = null;
			while(true)
			{
				key = watchService.poll(1, TimeUnit.MINUTES);
				if(key != null)
				{
					key.pollEvents().stream().forEach(event -> emailList.add(event.context().toString()));
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("New Message");
					alert.setHeaderText(null);
					alert.setContentText("You have a new message");
					alert.showAndWait();
				}
				else
				{
					key.reset();
				}
			}
		}
		catch(InterruptedException e)
		{
			//Interrupted
			System.err.println("Interupted");
		}
		return null;
	}

}
