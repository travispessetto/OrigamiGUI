package application.listeners;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SMTPThreadErrorListener implements SMTPErrorListener{

	@Override
	public void notify(Exception ex) 
	{
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				showAlert(ex);
			}});
	}

	@Override
	public void notifyFatal(Exception ex) {
		Platform.runLater(new Runnable()
		{

			@Override
			public void run()
			{
				showAlert(ex);
				System.exit(1);
			}

		});
	}
	
	private void showAlert(Exception ex)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(ex.getMessage());
		alert.showAndWait();
	}

	@Override
	public void notifyWithMessage(Exception ex, String message) 
	{
		Platform.runLater(new Runnable()
				{

					@Override
					public void run() {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setContentText(ex.getMessage() + "\n" + message);
						alert.showAndWait();						
					}
			
				});
	}

}
