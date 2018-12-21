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
	private PrintStream systemErrStream;
	private PrintStream debugStream;
	private ByteArrayOutputStream baos;
	
	protected DebugLogSingleton()
	{
		redirectSystemOut();
		redirectSystemErr();
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
	
	private void redirectSystemOut()
	{
		createDebugStream();
		systemStream = System.out;
		System.setOut(debugStream);
	}
	
	private void redirectSystemErr()
	{
		createDebugStream();
		systemErrStream = System.err;
		System.setErr(debugStream);
	}
	
	private void createDebugStream()
	{
		if(baos == null || debugStream == null)
		{
			baos = new ByteArrayOutputStream();
			debugStream = new PrintStream(baos);
		}
	}

}
