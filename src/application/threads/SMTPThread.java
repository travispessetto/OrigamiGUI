package application.threads;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.SocketException;
import java.util.List;

import com.pessetto.Status.StatusListener;
import com.pessetto.main.ConsoleMain;

import application.debug.DebugLogSingleton;
import application.listeners.SMTPErrorListener;
import application.listeners.SMTPStatusListener;

public class SMTPThread implements Runnable{

	private int port;
	private ConsoleMain smtpServer;
	private SMTPErrorListener listener;
	private boolean smtpStarted;
	private List<SMTPStatusListener> statusListeners;
	private DebugLogSingleton debugLog;
	private ByteArrayOutputStream baos;
	private PrintStream printStream;
	private PrintStream systemPrintStream;
	
	public SMTPThread(int port,SMTPErrorListener listener,List<SMTPStatusListener> statusListeners)
	{
		this.port = port;
		this.listener = listener;
		this.statusListeners = statusListeners;
		smtpServer = new ConsoleMain(this.port);
		debugLog = DebugLogSingleton.getInstance();
		baos = new ByteArrayOutputStream();
		printStream = new PrintStream(baos);
		systemPrintStream = System.out;
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
			debugLog.writeToLog("Starting bundled SMTP Server");
			notifyAllStatusListeners(true);
			System.setOut(printStream);
			smtpServer.startSMTP();
			getMessages();
		}
		catch(BindException ex)
		{
			listener.notifyWithMessage(ex, "You may be able to fix this by changing the port in File > Settings > Connection or exiting a previous instance");
			ex.printStackTrace(System.err);
			notifyAllStatusListeners(false);
		}
		catch (Exception e) {
			listener.notifyWithMessage(e, e.getMessage());
			e.printStackTrace();
			notifyAllStatusListeners(false);
		}
	}
	
	public void stop()
	{
		smtpServer.closeSMTP();
		notifyAllStatusListeners(false);
	}
	
	public void getMessages()
	{
		System.out.flush();
		System.setOut(systemPrintStream);
		debugLog.writeToLog(baos.toString());;
	}
	
	public void notifyAllStatusListeners(boolean started)
	{
		for(SMTPStatusListener listener : statusListeners)
		{
			listener.smtpStatusChanged(started);
		}
	}
	

}
