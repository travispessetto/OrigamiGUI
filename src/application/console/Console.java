package application.console;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.util.Optional;

import com.pessetto.main.*;

import application.settings.SettingsSingleton;
import application.threads.SMTPThread;

public class Console extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Platform.setImplicitExit(false);
		// icons
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("origami.png")));
		AnchorPane console = FXMLLoader.load(getClass().getClassLoader().getResource("Console.fxml"));
		Scene scene = new Scene(console,500,300);
		
		
		stage.setTitle("Origami SMTP");
		stage.setScene(scene);
		
		// Make sure to prompt before close
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        exit(event);
		    }
		});
		
		stage.show();
	}
	
	
	public void exit(WindowEvent event)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Really Quit?");
        alert.setContentText("Are you sure you wish to exit?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if(event != null && result.get() == ButtonType.CANCEL)
        {
        	event.consume();
        }
        else
        {
        	stop();
        }
	}
	
	@Override
	public void stop()
	{
		SettingsSingleton.getInstance().stopSMTPServer();
		Platform.exit();
	}
	
	public static void main(String[] args)
	{
		// Start the SMTP Server via settings
		SettingsSingleton.getInstance().startSMTPServer();
		System.out.println("Origami GUI");
		launch(args);
	}
	

}
