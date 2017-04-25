package application.threads;

import com.pessetto.main.ConsoleMain;

public class SMTPThread implements Runnable{

	public SMTPThread()
	{
		// Add port and stuff here later
	}
	
	
	@Override
	public void run() {
		try {
			String smtpArgs[] = {"2525"};
			ConsoleMain.main(smtpArgs);
			System.out.println("Starting bundled SMTP Server");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
