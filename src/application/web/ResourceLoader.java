package application.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.controllers.EmailController;

public class ResourceLoader
{
	public static String loadFile(InputStream externalStream)
	{
		String out = "";
		Scanner in = new Scanner(externalStream).useDelimiter("\\Z");
		while(in.hasNext())
		{
			out += in.next();
		}
		return ExternalJSLoader(out);
	}
	
	public static String ExternalJSLoader(String html)
	{
		Pattern p = Pattern.compile("src=\"(.+)\"");
		Matcher m = p.matcher(html);
		while(m.find())
		{
			String match = m.group(1);
			System.out.println("Match: " + match);
			String resource = ResourceLoader.class.getClassLoader().getResource(match).toExternalForm();
			System.out.println("External Resource: " + resource);
			html = html.replace(match, resource);
		}
		return html;
	}
}
