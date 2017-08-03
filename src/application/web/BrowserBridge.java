package application.web;

import java.awt.Desktop;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;

import com.pessetto.FileHandlers.Inbox.Attachment;

import application.controllers.EmailController;
import application.settings.SettingsSingleton;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class BrowserBridge 
{
	private EmailController controller;
	
	public BrowserBridge(EmailController ec)
	{
		controller = ec;
	}
	
	public void openLink(String href)
	{
		SettingsSingleton settings = SettingsSingleton.getInstance();
		String browser = settings.getBrowser();
		if(browser != null && !browser.isEmpty())
		{
			System.out.println("Launching " + browser);
			String[] params = new String[] {browser,"\""+href+"\""};
			if(settings.getPrivateBrowsing())
			{
				System.out.println("Open browser in private");
				if(browser.toLowerCase().contains("chrome"))
				{
					System.out.println("Chrome detected");
					params = new String[]{params[0],params[1],"--incognito"};
				}
			}
			try {
				Process p = Runtime.getRuntime().exec(params);
			} catch (IOException e) {
				System.err.println("Could not launch browser");
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				System.out.println("Use system browser");
				Desktop.getDesktop().browse(new URI(href));
			}
			catch (IOException e) 
			{
				controller.showAlert(AlertType.ERROR, "Error", e.getMessage());
				e.printStackTrace();
			}
			catch (URISyntaxException e)
			{
				controller.showAlert(AlertType.ERROR, "Error", "Bad Link");
				e.printStackTrace();
			}
		}
	}
	
	public void saveAttachment(int index)
	{
		Attachment attach = controller.getSelectedMessage().getAttachments().get(index);
		FileChooser fd = new FileChooser();
		fd.setTitle("Save file as...");
		fd.setInitialFileName(attach.getFileName());
		File path = fd.showSaveDialog(null);
		if(path != null)
		{
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(path);
				fos.write(attach.getContent());
				fos.close();
				controller.showAlert(AlertType.INFORMATION, "File saved", "Attachment saved");
			} catch (FileNotFoundException e) {
				System.err.println("File not found even though save?");
				controller.showAlert(AlertType.ERROR, "Error", "Trying to save file but got file not found.");
				e.printStackTrace();
			} catch (IOException e) {
				controller.showAlert(AlertType.ERROR, "Error",e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
