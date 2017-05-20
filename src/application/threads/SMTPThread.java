package application.threads;

import java.net.BindException;
import java.net.SocketException;

import com.pessetto.main.ConsoleMain;

import application.listeners.SMTPErrorListener;

public class SMTPThread implements Runnable{

	private int port;
	private ConsoleMain smtpServer;
	private SMTPErrorListener listener;
	public SMTPThread(int port,SMTPErrorListener listener)
	{
		this.port = port;
		this.listener = listener;
		smtpServer = new ConsoleMain(this.port);
	}
	
	
	@Override
	public void run() 
	{
		try 
		{
			System.out.println("Starting bundled SMTP Server");
			smtpServer.startSMTP();
		}
		catch(BindException ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace(System.err);
			listener.notifyFatal(ex);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		smtpServer.closeSMTP();
	}

}
