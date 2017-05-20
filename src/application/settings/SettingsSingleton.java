package application.settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.BindException;
import java.util.ArrayList;

import application.constants.SettingsVariables;
import application.listeners.SMTPErrorListener;
import application.listeners.SMTPThreadErrorListener;
import application.threads.SMTPThread;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SettingsSingleton implements Serializable
{
	private static final long serialVersionUID = 3840045425215974390L;
	private int port;
	private transient static SettingsSingleton instance;
	private transient Thread smtpThread;
	private transient SMTPThread smtp;
	private transient boolean minimizeToTray;
	private transient SMTPErrorListener smtpErrorListener;
	private SettingsSingleton(int port)
	{
		// No direct creation
		this.port = port; 
		this.minimizeToTray = true;
		smtpErrorListener = new SMTPThreadErrorListener();
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
					instance = (SettingsSingleton)oin.readObject();
					oin.close();
					fin.close();
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
		smtp = new SMTPThread(this.port,this.smtpErrorListener);
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
	
	public void setSMTPPort(int port)
	{
		this.port = port;
	}
	
	public void restartSMTPServer()
	{
		stopSMTPServer();
		startSMTPServer();
	}

}
