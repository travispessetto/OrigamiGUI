package com.pessetto.origamigui.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.pessetto.origamigui.controllers.UpdateController;

import javafx.application.Platform;

public class UpdateThread  implements Runnable{

	private Updater Updater;
	private UpdateController controller;
	
	public UpdateThread(Updater up, UpdateController c)
	{
		Updater = up;
		controller = c;
	}
	
	@Override
	public void run() {
		try {
			String[] names = Updater.getLocation().split("/");
			String workingDir = System.getProperty("user.dir");
			String workingDirFile = workingDir+"/Origami SMTP/";
			String name = workingDirFile + names[names.length - 1];
			System.out.println("Location is: " + Updater.getLocation());
			System.out.println("Storing file to " + name);
			URL url = new URL(Updater.getLocation());
			HttpURLConnection httpConnect = (HttpURLConnection)(url.openConnection());
			long completeFileSize = httpConnect.getContentLength();
			BufferedInputStream in = new BufferedInputStream(httpConnect.getInputStream());
			FileOutputStream fos = new FileOutputStream(name);
			BufferedOutputStream bos = new BufferedOutputStream(fos,1024);
			byte[] data = new byte[1024];
			long downloadFileSize = 0;
			int x = 0;
			while((x = in.read(data,0,1024)) >=0)
			{
				downloadFileSize += x;
				final int currentProgress = (int)((((double)downloadFileSize) / ((double)completeFileSize)) * 100000d);
				controller.setProgress(currentProgress);
				bos.write(data, 0, x);
			}
			bos.close();
			in.close();
			controller.runUpdateApplication(name);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
