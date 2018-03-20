package application.gui;

import com.pessetto.FileHandlers.Inbox.Message;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class EmailTableFactory implements Callback<TableColumn<Message,Message>,TableCell<Message,Message>> {

	@Override
	public TableCell<Message, Message> call(TableColumn<Message, Message> arg) 
	{
		return new TableCell<Message,Message>()
				{
					@Override
					protected void updateItem(Message message, boolean empty)
					{
						super.updateItem(message, empty);
						if(message != null)
						{
							Label labelSubject = createSubject(message);
							Label labelFrom = createFrom(message);
							
							VBox emailBox = new VBox(labelSubject,labelFrom);
							
							GridPane box = new GridPane();
							box.add(emailBox, 0, 0);
							
							box.getColumnConstraints().add(new ColumnConstraints());
							box.getColumnConstraints().add(new ColumnConstraints());
							
							setGraphic(box);
						}
						else
						{
							setGraphic(null);
						}
					}
					
					private Label createSubject(Message message)
					{
						Label label = new Label(message.getSubject());
						label.styleProperty().bind(Bindings.when(message.isRead()).then("").otherwise("-fx-font-weight: bold;"));
						return label;
					}
					
					private Label createFrom(Message message)
					{
						Label label = new Label(message.getFrom());
						return label;
					}
				};
	}

}
