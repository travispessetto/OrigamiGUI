package application.threads;

import java.net.SocketException;

import com.pessetto.main.ConsoleMain;

public class SMTPThread implements Runnable{

	private int port;
	private ConsoleMain smtpServer;
	public SMTPThread(int port)
	{
		this.port = port;
		smtpServer = new ConsoleMain(port);
	}
	
	
	@Override
	public void run() {
		try {
			System.out.println("Starting bundled SMTP Server");
			smtpServer.startSMTP();
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
