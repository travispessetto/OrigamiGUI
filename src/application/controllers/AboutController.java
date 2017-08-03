package application.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

public class AboutController 
{
	@FXML
	private TextArea AboutText;
	
	@FXML
	private void initialize()
	{
		setLicenseText();
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
}
