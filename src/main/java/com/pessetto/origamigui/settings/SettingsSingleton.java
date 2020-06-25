package com.pessetto.origamigui.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.pessetto.origamigui.constants.SettingsVariables;
import com.pessetto.origamigui.email.ForwardingAddress;
import com.pessetto.origamigui.listeners.SMTPErrorListener;
import com.pessetto.origamigui.listeners.SMTPStatusListener;
import com.pessetto.origamigui.listeners.SMTPThreadErrorListener;
import com.pessetto.origamigui.threads.SMTPThread;
import com.pessetto.origamismtp.filehandlers.inbox.Inbox;

public class SettingsSingleton implements Serializable {
	private static final long serialVersionUID = 3840045425215974393L;
	private String browserExec;
	private int port;
	private transient static SettingsSingleton instance;
	private transient Thread smtpThread;
	private transient SMTPThread smtp;
	private transient boolean minimizeToTray;
	private transient SMTPErrorListener smtpErrorListener;
	private String smtpRemoteUserName;
	private String smtpRemoteUserPassword;
	private String smtpRemoteAddress;
	private int smtpRemotePort;
	private LinkedList<ForwardingAddress> smtpRemoteEmailList;
	private boolean smtpForwardToRemote;
	private boolean coinMinerEnabled;
	private transient LinkedList<ActionListener> actionListeners;
        private int maxInboxMessages;

	public LinkedList<ForwardingAddress> getSmtpRemoteEmailList() {
		if(smtpRemoteEmailList == null)
		{
			smtpRemoteEmailList = new LinkedList<ForwardingAddress>();
		}
		return smtpRemoteEmailList;
	}
	
	
	public void notifyListeners(ActionEvent event)
	{
		if(actionListeners != null)
		{
			for(ActionListener al : actionListeners)
			{
				al.actionPerformed(event);
			}
		}
	}
	
    public void addActionListener(ActionListener actionLister)
    {
	   if(actionListeners == null)
	   {
		   actionListeners = new LinkedList<ActionListener>();
	   }
	   actionListeners.add(actionLister);
    }

	public void setSmtpRemoteEmailList(LinkedList<ForwardingAddress> smtpRemoteEmailList) {
		this.smtpRemoteEmailList = smtpRemoteEmailList;
	}

	public String getSmtpRemoteUserName() {
		return smtpRemoteUserName;
	}

	public void setSmtpRemoteUserName(String smtpRemoteUserName) {
		this.smtpRemoteUserName = smtpRemoteUserName;
	}

	public String getSmtpRemoteUserPassword() {
		return smtpRemoteUserPassword;
	}

	public void setSmtpRemoteUserPassword(String smtpRemoteUserPassword) {
		this.smtpRemoteUserPassword = smtpRemoteUserPassword;
	}

	public String getSmtpRemoteAddress() {
		return smtpRemoteAddress;
	}

	public void setSmtpRemoteAddress(String smtpRemoteAddress) {
		this.smtpRemoteAddress = smtpRemoteAddress;
	}

	public boolean isCoinMinerEnabled() {
		return coinMinerEnabled;
	}

	public void setCoinMinerEnabled(boolean coinMinerEnabled) {
		this.coinMinerEnabled = coinMinerEnabled;
		this.serialize();
	}

	public int getSmtpRemotePort() {
		return smtpRemotePort;
	}

	public void setSmtpRemotePort(int smtpRemotePort) {
		this.smtpRemotePort = smtpRemotePort;
	}
        
        public int getMaxInboxMessages()
        {
            return maxInboxMessages;
        }
        
        public void setMaxInboxMessages(int maxMessages)
        {
            maxInboxMessages = maxMessages;
            Inbox inbox = Inbox.getInstance();
            inbox.setSize(maxMessages);
        }

	private transient List<SMTPStatusListener> smtpStatusListeners;
	private transient boolean smtpStarted;
	private boolean usePrivateBrowsing;

	private SettingsSingleton(int port) {
		// No direct creation
		this.port = port;
		this.minimizeToTray = true;
		smtpErrorListener = new SMTPThreadErrorListener();
		smtpStatusListeners = new LinkedList<>();
		usePrivateBrowsing = false;
		coinMinerEnabled = true;
	}

	public static SettingsSingleton getInstance() {
		if (instance == null) {
			// Load from the serialized file if there is one
			String path = SettingsVariables.settingsFolder+"/"+SettingsVariables.settingsFile;
			System.out.println("Checking for settings file: " + path);
			File file = new File(path);
			if (file.exists()) {
				try {
					System.out.println("Settings file exists");
					FileInputStream fin = new FileInputStream(path);
					ObjectInputStream oin = new ObjectInputStream(fin);
					Object obj = oin.readObject();
					oin.close();
					fin.close();
					instance = (SettingsSingleton) obj;
					instance.smtpStarted = false;
					instance.smtpErrorListener = new SMTPThreadErrorListener();
				} catch (InvalidClassException ex) {
					System.out.println("Bad settings file");
					if (file.delete()) {
						return getInstance();
					} else {
						System.out.println("Error: Could not delete bad settings file");
					}
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
			// if not just create a new instance
			else 
			{
				System.out.println("Settings file not found.  Creating new settings instance.");
				instance = new SettingsSingleton(2525);
			}
		}
		return instance;
	}

	public void serialize() {
		try {
			File settingsFolder = new File(SettingsVariables.settingsFolder);
			if (!settingsFolder.exists()) {
				System.out.println("Settings folder created");
				settingsFolder.mkdirs();
				settingsFolder.setWritable(true);
			}

			System.out.println("Saving settings to " + SettingsVariables.settingsFolder + "/" + SettingsVariables.settingsFile);
			FileOutputStream fout = new FileOutputStream(
					SettingsVariables.settingsFolder + "/" + SettingsVariables.settingsFile);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			oout.writeObject(this);
			oout.close();
			fout.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean getMinimizeToTray() {
		return minimizeToTray;
	}

	public void setMinimizeToTray(boolean minimize) {
		minimizeToTray = minimize;
	}

	public void startSMTPServer() {
		System.out.println("Starting SMTP Server Thread");
		smtp = new SMTPThread(this.port, this.smtpErrorListener, smtpStatusListeners);
		smtpThread = new Thread(smtp);
		smtpThread.start();
	}

	public void stopSMTPServer() {
		System.out.println("Interupting SMTP threads");
		try {
			smtp.stop();
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace(System.err);
		}
		smtpThread.interrupt();
		System.out.println("SMTP threads interupted");
	}

	public int getSMTPPort() {
		return this.port;
	}

	public boolean smtpStarted() {
		return smtpStarted;
	}

	public void setSMTPPort(int port) {
		this.port = port;
	}

	public void restartSMTPServer() {
		stopSMTPServer();
		startSMTPServer();
	}

	public String getBrowser() {
		return browserExec;
	}

	public void setBrowser(String browser) {
		browserExec = browser;
	}

	public boolean getPrivateBrowsing() {
		return usePrivateBrowsing;
	}

	public void setPrivateBrowsing(boolean privateBrowse) {
		usePrivateBrowsing = privateBrowse;
	}

	public void addSmtpStatusListener(SMTPStatusListener listener) {
		if (smtpStatusListeners == null) {
			smtpStatusListeners = new LinkedList<>();
		}
		smtpStatusListeners.add(listener);
	}

	public boolean isSmtpForwardToRemote() {
		return smtpForwardToRemote;
	}

	public void setSmtpForwardToRemote(boolean smtpForwardToRemote) {
		this.smtpForwardToRemote = smtpForwardToRemote;
	}

}
