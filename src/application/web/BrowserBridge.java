package application.web;

import java.io.IOException;

import application.controllers.EmailController;
import application.settings.SettingsSingleton;

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
			String[] params = new String[] {browser,href};
			try {
				Process p = Runtime.getRuntime().exec(params);
			} catch (IOException e) {
				System.err.println("Could not launch browser");
				e.printStackTrace();
			}
		}
		else
		{
			//Todo: load in web engine.
			System.err.println("No browser");
		}
	}
}
