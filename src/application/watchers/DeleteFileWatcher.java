package application.watchers;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import application.controllers.EmailController;

public class DeleteFileWatcher implements Runnable
{
	private WatchService watchService;
	private EmailController emailController;
	
	public DeleteFileWatcher(WatchService service, EmailController controller)
	{
		watchService = service;
		emailController = controller;
	}
	
	@Override
	public void run()
	{
		try
		{
			WatchKey key = null;
			while(true)
			{
				key = watchService.take();
				if(key != null)
				{
					key.pollEvents().stream().forEach(event -> emailController.removeEmailFromList(event.context().toString()));
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
