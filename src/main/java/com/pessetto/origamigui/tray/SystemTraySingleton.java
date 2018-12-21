package application.tray;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import application.console.OrigamiGUI;
import application.settings.SettingsSingleton;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;

public class SystemTraySingleton
{
	private static SystemTraySingleton instance;
	private SystemTray systemTray;
	private TrayIcon trayIcon;
	private OrigamiGUI console;
	private ArrayList<ActionListener> actionListeners;
	private Image icon;
	private boolean started;
	
	private SystemTraySingleton()
	{
		actionListeners = new ArrayList<ActionListener>();
		started = false;
	}
	
	public static SystemTraySingleton getInstance()
	{
		if(instance == null)
		{
			instance = new SystemTraySingleton();
		}
		return instance;
	}
	
	
	public void addActionListener(ActionListener listener)
	{
		actionListeners.add(listener);
	}
	
	private void addAppToTray()
	{
		SettingsSingleton settings = SettingsSingleton.getInstance();
		try
		{
			java.awt.Toolkit.getDefaultToolkit();
			if(!java.awt.SystemTray.isSupported())
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("System tray minimization not supported.  Continuing without minimization to tray.");
				alert.showAndWait();
				settings.setMinimizeToTray(false);
			}
			else
			{
				systemTray = java.awt.SystemTray.getSystemTray();
				BufferedImage trayImage = SwingFXUtils.fromFXImage(icon, null);
				trayIcon = new TrayIcon(trayImage);
				trayIcon.setToolTip("Origami SMTP");
				trayIcon.setImageAutoSize(true);
				this.addAllListenersToTrayIcon();
				trayIcon.setActionCommand("Open");
				// add event listener to show stage
				MenuItem openItem = new MenuItem("Open");
				addAllListenersToMenuItem(openItem);
				MenuItem exitItem = new MenuItem("Exit");
				addAllListenersToMenuItem(exitItem);
				
				PopupMenu popup = new PopupMenu();
				popup.add(openItem);
				popup.add(exitItem);
				trayIcon.setPopupMenu(popup);
				systemTray.add(trayIcon);
			}
		}
		catch(Exception ex)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText(ex.getMessage());
			ex.printStackTrace(System.err);
		}
	}
	
	private void addAllListenersToMenuItem(MenuItem item)
	{
		for(ActionListener listener : actionListeners)
		{
			item.addActionListener(listener);
		}
	}
	
	private void addAllListenersToTrayIcon()
	{
		for(ActionListener listener : actionListeners)
		{
			trayIcon.addActionListener(listener);
		}
	}
	
	public void displayMessage(String caption, String message, MessageType messageType)
	{
		if(trayIcon != null)
		{
			javax.swing.SwingUtilities.invokeLater(new Runnable()
					{

						@Override
						public void run() {
							trayIcon.displayMessage(caption, message, messageType);	
						}
				
					});
		}
		else
		{
			Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
			alert.show();
		}
		
	}
	
	public void setIcon(Image icon)
	{
		this.icon = icon;
	}
	
	public void startTrayIcon()
	{
		started = true;
		javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
	}
	
	public void stop()
	{
		if(started)
		{
			systemTray.remove(trayIcon);
		}
	}
}
