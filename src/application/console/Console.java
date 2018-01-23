package application.console;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import javax.imageio.ImageIO;

import application.debug.DebugLogSingleton;
import application.listeners.TrayIconListener;
import application.settings.SettingsSingleton;
import application.threads.SMTPThread;
import application.tray.SystemTraySingleton;

public class Console extends Application implements ActionListener, TrayIconListener{
	
	private Image icon;
	private Stage mainStage;
	private static DebugLogSingleton debugLog;
	
	
	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		Platform.setImplicitExit(false);
		
		// icons
		icon = new Image(getClass().getClassLoader().getResourceAsStream("origami.png"));
		stage.getIcons().add(icon);
		
		SystemTraySingleton systemTray = SystemTraySingleton.getInstance();
		systemTray.setIcon(icon);
		systemTray.addActionListener(this);
		systemTray.startTrayIcon();
		
		AnchorPane console = FXMLLoader.load(getClass().getClassLoader().getResource("Console.fxml"));
		Scene scene = new Scene(console);
		
		
		mainStage.setTitle("Origami SMTP");
		mainStage.setScene(scene);
		
		// Make sure to prompt before close
		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        exit(event);
		    }
		});
		
		openStage();
	}
	
	public void openStage()
	{
		try
		{	
			if(mainStage != null)
			{
				mainStage.show();
			}
		}
		catch(Exception ex)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(ex.getMessage());
			alert.showAndWait();
		}
	}
	
	
	public void exit(WindowEvent event)
	{
		if(exitApplication())
		{
			stop();
		}
		else
		{
			SystemTraySingleton systemTray = SystemTraySingleton.getInstance();
			systemTray.displayMessage("I am here", "I will be down here if you need me. Exit from the menu if you want me to go away.", MessageType.INFO);
			mainStage.hide();
		}
	}
	
	@Override
	public void stop()
	{
		SystemTraySingleton systemTray = SystemTraySingleton.getInstance();
		systemTray.stop();
		SettingsSingleton.getInstance().stopSMTPServer();
		Platform.exit();
	}
	
	public static void main(String[] args)
	{
		debugLog = DebugLogSingleton.getInstance();
		debugLog.writeToLog("Origami GUI");
		debugLog.writeToLog("Working Dir: " + System.getProperty("user.dir"));
		launch(args);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Open"))
		{
			Platform.runLater(new Runnable()
			{
				@Override
				public void run() {
					mainStage.show();	
				}
		
			});
		}
		else if(e.getActionCommand().equals("Exit"))
		{
			stop();
		}
	}

	@Override
	public void TrayAction(String action) {
		if(action.equals("open stage"))
		{
			mainStage.show();
		}
		
	}
	
	private boolean exitApplication()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit or Minimize?");
		alert.setHeaderText("Do you wish to exit or minimize to tray?");
		alert.setContentText("Choose one");
		
		ButtonType buttonExit = new ButtonType("Exit");
		ButtonType buttonMinimize = new ButtonType("Minimize");
		
		alert.getButtonTypes().setAll(buttonExit,buttonMinimize);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == buttonExit)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	

}
