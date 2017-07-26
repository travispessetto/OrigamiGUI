package application.settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import application.constants.SettingsVariables;
import application.listeners.SMTPErrorListener;
import application.listeners.SMTPStatusListener;
import application.listeners.SMTPThreadErrorListener;
import application.threads.SMTPThread;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SettingsSingleton implements Serializable
{
	private static final long serialVersionUID = 3840045425215974392L;
	private String browserExec;
	private int port;
	private transient static SettingsSingleton instance;
	private transient Thread smtpThread;
	private transient SMTPThread smtp;
	private transient boolean minimizeToTray;
	private transient SMTPErrorListener smtpErrorListener;
	private transient List<SMTPStatusListener> smtpStatusListeners;
	private boolean smtpStarted;
	private boolean usePrivateBrowsing;
	private SettingsSingleton(int port)
	{
		// No direct creation
		this.port = port; 
		this.minimizeToTray = true;
		smtpErrorListener = new SMTPThreadErrorListener();
		smtpStatusListeners = new LinkedList<SMTPStatusListener>();
		usePrivateBrowsing = false;
	}
	
	public static SettingsSingleton getInstance()
	{
		if(instance == null)
		{
			// Load from the serialized file if there is one
			File file = new File(SettingsVariables.settingsFile);
			if(file.exists())
			{
				try
				{
					FileInputStream fin = new FileInputStream(SettingsVariables.settingsFile);
					ObjectInputStream oin = new ObjectInputStream(fin);
					Object obj = oin.readObject();
					oin.close();
					fin.close();
					instance = (SettingsSingleton)obj;
				}
				catch(InvalidClassException ex)
				{
					System.err.println("Bad settings file");
					if(file.delete())
					{
						return getInstance();
					}
					else
					{
						System.err.println("Could not delete bad settings file");
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace(System.err);
				}
			}
			// if not just create a new instance
			else
			{
				instance = new SettingsSingleton(2525);
			}
		}
		return instance;
	}
	
	
	
	public void serialize()
	{
		try
		{
			File settingsFolder = new File("settings");
			if(settingsFolder.exists())
			{
				settingsFolder.setWritable(true,false);
				settingsFolder.mkdir();
			}
			
			FileOutputStream fout = new FileOutputStream(SettingsVariables.settingsFile);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			oout.writeObject(this);
			oout.close();
			fout.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean getMinimizeToTray()
	{
		return minimizeToTray;
	}
	
	public void setMinimizeToTray(boolean minimize)
	{
		minimizeToTray = minimize;
	}
	
	public void startSMTPServer()
	{
		System.out.println("Starting SMTP Server Thread");
		smtp = new SMTPThread(this.port,this.smtpErrorListener,smtpStatusListeners);
		smtpThread = new Thread(smtp);
		smtpThread.start();
	}
	
	public void stopSMTPServer()
	{
		System.out.println("Interupting SMTP threads");
		try
		{
			smtp.stop();
		}
		catch(Exception ex)
		{
			System.err.println("Error: " + ex.getMessage());
			ex.printStackTrace(System.err);
		}
		smtpThread.interrupt();
		System.out.println("SMTP threads interupted");
	}
	
	public int getSMTPPort()
	{
		return this.port;
	}
	
	public boolean smtpStarted()
	{
		return smtpStarted;
	}
	
	public void setSMTPPort(int port)
	{
		this.port = port;
	}
	
	public void restartSMTPServer()
	{
		stopSMTPServer();
		startSMTPServer();
	}
	
	public String getBrowser()
	{
		return browserExec;
	}
	
	public void setBrowser(String browser)
	{
		browserExec = browser;
	}
	
	public boolean getPrivateBrowsing()
	{
		return usePrivateBrowsing;
	}
	
	public void setPrivateBrowsing(boolean privateBrowse)
	{
		usePrivateBrowsing = privateBrowse;
	}
	
	public void addSmtpStatusListener(SMTPStatusListener listener)
	{
		if(smtpStatusListeners == null)
		{
			smtpStatusListeners = new LinkedList<SMTPStatusListener>();
		}
		smtpStatusListeners.add(listener);
	}

}
