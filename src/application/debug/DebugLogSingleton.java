package application.debug;

import java.util.concurrent.locks.ReentrantLock;

public class DebugLogSingleton
{
	
	private static DebugLogSingleton instance = null;
	private static final ReentrantLock lock = new ReentrantLock();
	private String log;
	
	protected DebugLogSingleton()
	{
		log = "";
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
		lock.lock();
		out = log;
		lock.unlock();
		return out;
	}
	
	public void writeToLog(String message)
	{
		lock.lock();
		log += message;
		log += "\r\n";
		lock.unlock();
	}

}
