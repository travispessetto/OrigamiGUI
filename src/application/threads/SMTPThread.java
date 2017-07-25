package application.threads;

import java.net.BindException;
import java.net.SocketException;

import com.pessetto.main.ConsoleMain;

import application.listeners.SMTPErrorListener;

public class SMTPThread implements Runnable{

	private int port;
	private ConsoleMain smtpServer;
	private SMTPErrorListener listener;
	private boolean smtpStarted;
	public SMTPThread(int port,SMTPErrorListener listener)
	{
		this.port = port;
		this.listener = listener;
		smtpServer = new ConsoleMain(this.port);
	}
	
	
	public boolean isStarted()
	{
		return smtpStarted;
	}
	
	@Override
	public void run() 
	{
		try 
		{
			System.out.println("Starting bundled SMTP Server");
			smtpServer.startSMTP();
			smtpStarted = true;
		}
		catch(BindException ex)
		{
			listener.notifyWithMessage(ex, "You may be able to fix this by changing the port in File > Settings or exiting a previous instance");
			ex.printStackTrace(System.err);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		smtpServer.closeSMTP();
		smtpStarted = false;
	}
	

}
