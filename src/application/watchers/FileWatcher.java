package application.watchers;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import application.controllers.EmailController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FileWatcher implements Runnable{
	private WatchService watchService;
	private EmailController emailController;
	public FileWatcher(WatchService service, EmailController controller)
	{
		watchService = service;
		emailController = controller;
	}

	@Override
	public void run() {
		try
		{
			WatchKey key = null;
			while(true)
			{
				key = watchService.take();
				if(key != null)
				{
					key.pollEvents().stream().forEach(event -> emailController.addEmailToList(event.context().toString()));
				}
				key.reset();
			}
		}
		catch(InterruptedException e)
		{
			//Interrupted
			System.err.println("Interupted");
		}
	}

}
