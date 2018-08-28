package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsersTableController {
	private ObservableList<AllUsers> data = FXCollections.observableArrayList();
	
	/**
	 * Creates a tableview to display all users registered to the auctioneer program associated.
	 * Only users associated with the specific entity will be visible. 
	 * This does not retrieve the users, that is done in a different method. 
	 * Table has three columns, ID, Name (First & Last), Username. 
	 * @param tableView
	 */
	public void tableView(TableView<AllUsers> tableView) {
		TableColumn<AllUsers, Integer> idColumn = new TableColumn<>("ID");
		TableColumn<AllUsers, String> nameColumn = new TableColumn<>("Name");
		TableColumn<AllUsers, String> usernameColumn = new TableColumn<>("Username");
		
		// set the cell value factory
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		
		// Columns are not editable
		idColumn.setEditable(false);
		nameColumn.setEditable(false);
		usernameColumn.setEditable(false);
		
		// Set the column widths
		idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.45));
		usernameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.45));
		
		// Add the columns to the table
		tableView.getColumns().setAll(idColumn, nameColumn, usernameColumn);
		
		// Add the data to the table
		tableView.setItems(data);
	}

	/**
	 *Getter/setter class for the AllUsers tableview. 
	 * @author cmeehan
	 *
	 */
	public static class AllUsers{
		private SimpleIntegerProperty id;
		private SimpleStringProperty name, username;
		
		public AllUsers(Integer id, String name, String username) {
			this.id = new SimpleIntegerProperty(id);
			this.name = new SimpleStringProperty(name);
			this.username = new SimpleStringProperty(username);
		}
		
		public Integer getId() {
			return this.id.get();
		}
		
		public String getName() {
			return this.name.get();
		}
		
		public String getUsername() {
			return this.username.get();
		}
		
		public void setId(Integer val) {
			this.id = new SimpleIntegerProperty(val);
		}
		
		public void setName(String val) {
			this.name = new SimpleStringProperty(val);
		}
		
		public void setUsername(String val) {
			this.username = new SimpleStringProperty(val);
		}
	}
	
}
