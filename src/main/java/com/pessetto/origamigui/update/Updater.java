package com.pessetto.origamigui.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pessetto.origamigui.constants.ApplicationVariables;
import com.pessetto.origamigui.controllers.UpdateController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Updater 
{
	private String latestVersion;
	private Stage stage;
	private String windowsLocation;
	private String debianLocation;
	private String otherLocation;
	
	public Updater()
	{
		checkLatestVersion();
	}
	
	public void close()
	{
		stage.close();
	}
	
	public String getLocation()
	{
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("windows"))
		{
			return windowsLocation;
		}
		else
		{
			return otherLocation;
		}
		
	}
	
	public void runSystemInstallCmd(String name)
	{
		try
		{
			String os = System.getProperty("os.name").toLowerCase();
			if(os.contains("windows"))
			{
				Runtime.getRuntime().exec("cmd /c start \"\" \""+name+"\"");
			}
			else if(os.contains("debian"))
			{
				Runtime.getRuntime().exec(new String[] {"/bin/sh","-c",name});
			}
			else
			{
				Runtime.getRuntime().exec("java -jar " + name);
			}
		}
		catch(Exception ex)
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace(System.err);
		}
	}
	
	private void checkLatestVersion()
	{
		String os = System.getProperty("os.name").toLowerCase();
		//Linux should use their package manager
		{
			if(!os.equals("linux"))
			latestVersion = findLatestVersion(updateAsJson());
			String currentVersion = findCurrentVersion();
			if(!currentVersion.trim().equals(latestVersion.trim()))
			{
				System.out.println("Update avalible");
				showUpdateDialog();
			}
			else
			{
				System.out.println("Application up to date");
			}
		}
	}
	
	public String getLatestVersion() {
		return latestVersion;
	}

	private String findLatestVersion(JSONArray jsonArr)
	{
		for(int i = 0; i < jsonArr.length(); ++i)
		{
			JSONObject obj = (JSONObject)jsonArr.get(i);
			if(!obj.getBoolean("prerelease"))
			{
				JSONArray assets = obj.getJSONArray("assets");
				for(int j = 0; j < assets.length(); ++j)
				{
					JSONObject download = assets.getJSONObject(j);
					if(download.getString("name").toLowerCase().contains(".jar"))
					{
						otherLocation = download.getString("browser_download_url");
					}
					else if(download.getString("name").toLowerCase().contains(".exe"))
					{
						windowsLocation = download.getString("browser_download_url");
					}
					else if(download.getString("name").toLowerCase().contains(".deb"))
					{
						debianLocation = download.getString("browser_download_url");
					}
				}
				return obj.getString("tag_name");
			}
		}
		return null;
	}
	
	private String findCurrentVersion()
	{
		try
		{
			InputStream in = getClass().getClassLoader().getResourceAsStream("VERSION");
			Scanner scan = new Scanner(in);
			scan.useDelimiter("\\Z");
			String versionText = scan.next();
			return versionText;
		}
		catch(Exception ex)
		{
			Alert alert = new Alert(AlertType.ERROR, "Could not open version file", ButtonType.OK);
		}
		return null;
	}
	
	private String readUpdateAsString()
	{
		try
		{
			String out = new Scanner(new URL(ApplicationVariables.updateUrl).openStream(), "UTF-8").useDelimiter("\\A").next();
			return out;
		} catch (MalformedURLException e) {
			System.err.println("Could not check update");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not check update");
			e.printStackTrace();
		}
		return "";
	}
	
	private void showUpdateDialog()
	{
		try 
		{
			System.out.println("Showing update dialog");
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/Update.fxml"));
			Parent root = (Parent)loader.load();
			UpdateController controller = loader.<UpdateController>getController();
			controller.initData(this);
			
			
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icons/origami.png")));
			stage.setTitle("New Version Avalible");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.showAndWait();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JSONArray updateAsJson()
	{
		JSONArray arr = new JSONArray(readUpdateAsString());
		return arr;
	}

}
