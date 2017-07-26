package application.web;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import application.controllers.EmailController;
import application.settings.SettingsSingleton;
import javafx.scene.control.Alert.AlertType;

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
}
