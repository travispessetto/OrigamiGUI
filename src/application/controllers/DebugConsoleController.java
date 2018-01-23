package application.controllers;

import application.debug.DebugLogSingleton;
import application.settings.SettingsSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class DebugConsoleController 
{
	@FXML
	private TextArea debugConsole;
	
	private SettingsSingleton settings;
	
	@FXML
	private void initialize()
	{
		settings = SettingsSingleton.getInstance();
		DebugLogSingleton debugLog = DebugLogSingleton.getInstance();
		String logText = debugLog.readLog();
		debugConsole.setText(logText);
	}
}
