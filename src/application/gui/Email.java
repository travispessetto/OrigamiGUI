package application.gui;

import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.property.BooleanProperty;

public class Email
{
	private String Subject;
	private String To;
	private String From;
    private BooleanProperty Read;
    
	public BooleanProperty getRead() {
		return Read;
	}
	public void setRead(BooleanProperty read) {
		Read = read;
	}
	public String getSubject() {
		return Subject;
	}
	public void setSubject(String subject) {
		Subject = subject;
	}
	public String getTo() {
		return To;
	}
	public void setTo(String to) {
		To = to;
	}
	public String getFrom() {
		return From;
	}
	public void setFrom(String from) {
		From = from;
	}

}
