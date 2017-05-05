package application.settings;

import application.threads.SMTPThread;

public class SettingsSingleton 
{
	private int port;
	private static SettingsSingleton instance;
	private Thread smtpThread;
	private SMTPThread smtp;
	private SettingsSingleton(int port)
	{
		// No direct creation
		this.port = port; 
	}
	
	public static SettingsSingleton getInstance()
	{
		if(instance == null)
			instance = new SettingsSingleton(2525);
		return instance;
	}
	
	public void startSMTPServer()
	{
		System.out.println("Starting SMTP Server Thread");
		smtp = new SMTPThread(this.port);
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
