package application.console;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.pessetto.main.*;

import application.settings.SettingsSingleton;
import application.threads.SMTPThread;

public class Console extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// icons
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("origami.png")));
		AnchorPane console = FXMLLoader.load(getClass().getClassLoader().getResource("Console.fxml"));
		Scene scene = new Scene(console,500,300);
		stage.setTitle("Origami SMTP");
		stage.setScene(scene);
		stage.show();
	}
	
	@Override
	public void stop()
	{
		System.out.println("Closing threads");
		SettingsSingleton.getInstance().stopSMTPServer();
		//System.exit(0);
	}
	
	public static void main(String[] args)
	{
		// Start the SMTP Server via settings
		SettingsSingleton.getInstance().startSMTPServer();
		System.out.println("Origami GUI");
		launch(args);
	}
	

}
