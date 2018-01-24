package application.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AboutController 
{
	@FXML
	private TextArea AboutText;
	
	@FXML
	private Label Version;
	
	@FXML
	private void initialize()
	{
		setLicenseText();
		setVersion();
	}
	
	private void setLicenseText()
	{
		try
		{
			File license = new File("license.txt");
			Scanner scan = new Scanner(license);
			scan.useDelimiter("\\Z");
			String content = scan.next();
			AboutText.setText(content);			
		}
		catch(Exception ex)
		{
			Alert alert = new Alert(AlertType.ERROR, "Could not open license file", ButtonType.OK);
		}
	}
	
	private void setVersion()
	{
		try
		{
			File versionFile = new File("VERSION");
			Scanner scan = new Scanner(versionFile);
			scan.useDelimiter("\\Z");
			String versionText = scan.next();
			System.out.println("Version is " + versionText);
			String versionLabelText = Version.getText();
			versionLabelText = String.format(versionText,versionLabelText);
			Version.setText(versionText);
		}
		catch(Exception ex)
		{
			Alert alert = new Alert(AlertType.ERROR, "Could not open version file", ButtonType.OK);
		}
	}
}
