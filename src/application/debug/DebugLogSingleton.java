package application.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.locks.ReentrantLock;

public class DebugLogSingleton
{
	
	private static DebugLogSingleton instance = null;
	private static final ReentrantLock lock = new ReentrantLock();
	private PrintStream systemStream;
	private PrintStream debugStream;
	private ByteArrayOutputStream baos;
	
	protected DebugLogSingleton()
	{
		redirectSystemOut();
	}
	
	public static DebugLogSingleton getInstance()
	{
		if(instance == null)
		{
			instance = new DebugLogSingleton();
		}
		return instance;
	}
	
	public String readLog()
	{
		String out = null;
		try
		{
			lock.lock();
			out = baos.toString();
			baos.flush();
			lock.unlock();
		}
		catch(IOException ex)
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace(System.err);
		}
		return out;
	}
	
	public void writeToLog(String message)
	{
		lock.lock();
		System.out.println(message);
		lock.unlock();
	}
	
	private void redirectSystemOut()
	{
		baos = new ByteArrayOutputStream();
		systemStream = System.out;
		debugStream = new PrintStream(baos);
		System.setOut(debugStream);
	}

}
