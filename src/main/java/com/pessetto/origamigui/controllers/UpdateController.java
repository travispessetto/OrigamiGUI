package com.pessetto.origamigui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import com.pessetto.origamigui.update.UpdateThread;
import com.pessetto.origamigui.update.Updater;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public class UpdateController {

	private Updater Updater;
	
	@FXML
	protected Button LaterButton;
	
	@FXML
	protected ProgressBar ProgressBar;

	@FXML
	protected Button UpdateButton;
	
	@FXML
	protected Text UpdateText;

	
	
	public void initialize()
	{

	}
	
	public void initData(Updater up)
	{
		Updater = up;
		String text = UpdateText.getText();
		text = text.replaceAll("\\{\\{version\\}\\}", Updater.getLatestVersion());
		System.out.println(text);
		UpdateText.setText(text);
	}
	
	@FXML
	public void Later(Event event)
	{
		System.out.println("Update later");
		Updater.close();
	}
	
	@FXML
	public void Update(Event event)
	{
		System.out.println("Update");
		UpdateText.setText("Downloading...");
		ProgressBar.setVisible(true);
		LaterButton.setDisable(true);
		UpdateButton.setDisable(true);
		downloadUpdate();
	}
	
	private void downloadUpdate()
	{
		Thread updateThread = new Thread(new UpdateThread(Updater,this));
		updateThread.start();
	}
	
	public void setProgress(double progress)
	{
		Platform.runLater(new Runnable()
		{

			@Override
			public void run() {
				ProgressBar.setProgress(progress);
				
			}
	
		});
	}
	
	public void runUpdateApplication(String name)
	{

		Platform.runLater(new Runnable()
				{

					@Override
					public void run() {
						try
						{
							UpdateText.setText("Opening installer...");	
							Updater.runSystemInstallCmd(name);
							Platform.exit();
							Updater.close();
						}
						catch(Exception ex)
						{
							System.err.println("Could not update application");
							ex.printStackTrace(System.err);
						}
					}
			
				});

	}
}
