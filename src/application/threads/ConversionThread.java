package application.threads;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class ConversionThread implements Runnable
{
	private String message;
	private String result;
	public ConversionThread(String messageContent)
	{
		message = messageContent;
	}

	@Override
	public void run() 
	{
		// http://stackoverflow.com/questions/11240368/how-to-read-text-inside-body-of-mail-using-javax-mail
		try
		{
			Session session = Session.getDefaultInstance(new Properties());
			InputStream inputStream = new ByteArrayInputStream(message.getBytes());
			MimeMessage mimeMessage = new MimeMessage(session,inputStream);
			System.out.println((String) mimeMessage.getContent());
		}
		catch(Exception ex)
		{
			// To Do: handle exception
		}
	}

}
