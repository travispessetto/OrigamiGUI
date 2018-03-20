package application.gui;

import com.pessetto.FileHandlers.Inbox.Message;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class EmailTableValueFactory implements Callback<TableColumn.CellDataFeatures<Message,Message>,ObservableValue<Message>>
{

	@Override
	public ObservableValue<Message> call(CellDataFeatures<Message, Message> arg) {
		if(arg.getValue() != null)
		{
			return new ReadOnlyObjectWrapper<>(arg.getValue());
		}
		
		return null;
	}

}
