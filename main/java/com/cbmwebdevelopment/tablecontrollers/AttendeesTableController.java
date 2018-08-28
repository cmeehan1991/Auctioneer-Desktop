package com.cbmwebdevelopment.tablecontrollers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.Notifications;

import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.auction.AuctionFXMLController;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class AttendeesTableController {

	/**
	 * Removes a bidder from the auction.
	 * @param id
	 * @param tableView
	 * @param tableRow
	 */
	private void removeBidder(int id, TableView<Attendees> tableView, TableRow<Attendees> tableRow) {
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(()->{
			boolean removed = new Auction().removeAttendee(id, AuctionFXMLController.auctionId);
			Platform.runLater(()->{
				if(removed) {
					tableView.getItems().remove(tableRow.getIndex());
				}else {
					Notifications.create().title("Failed to Remove Bidder").text("Failed to remove bidder. Please try again or check with your administrator.").showError();
				}
			});
			executor.shutdown();
		});
	}
	
	public void tableController(TableView<Attendees> tableView) {
		TableColumn<Attendees, Integer> idColumn = new TableColumn<>("Bidder ID");
		TableColumn<Attendees, String> nameColumn = new TableColumn<>("First & Last Name");
		TableColumn<Attendees, Hyperlink> removeBidderColumn = new TableColumn<>("");
		
        Callback<TableColumn<Attendees, Hyperlink>, TableCell<Attendees, Hyperlink>> cellFactory = (TableColumn<Attendees, Hyperlink> param) -> new RemoveBidderCell();
        
        // Set cell widths
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.70));
        removeBidderColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
			
		// Set cell value factory
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		removeBidderColumn.setCellFactory(cellFactory);
		
		tableView.getColumns().setAll(idColumn, nameColumn, removeBidderColumn);
	}
	
	public static class Attendees{
		public final SimpleIntegerProperty id;
		public final SimpleStringProperty name;
		public final SimpleObjectProperty<Hyperlink> removeBidder;
		
		public Attendees(Integer id, String name, Hyperlink removeBidder) {
			this.id = new SimpleIntegerProperty(id);
			this.name = new SimpleStringProperty(name);
			this.removeBidder = new SimpleObjectProperty<>(removeBidder);
		}
		
		public Integer getId() {
			return this.id.get();
		}
		
		public String getName() {
			return this.name.get();
		}
		
		public Hyperlink getRemoveBidder() {
			return this.removeBidder.get();
		}
		
		public void setId(Integer id) {
			this.id.set(id);
		}

		
		public void setName(String name) {
			this.name.set(name);
		}
		
		public void setRemoveBidder(Hyperlink val) {
			this.removeBidder.set(val);
		}
	}
	
	
	/**
	 * Custom table cell to create the remove row table cell
	 */
	public class RemoveBidderCell<T> extends TableCell<Attendees, Void>{
		private final Hyperlink link; 
		
		public RemoveBidderCell() {
			link = new Hyperlink("Remove");
			link.setOnAction(event->{
				
				Attendees attendee = getTableView().getItems().get(getTableRow().getIndex());
				removeBidder(attendee.getId(), getTableView(), getTableRow());
			});
		}
		
		@Override
	    protected void updateItem(Void item, boolean empty) {
	        super.updateItem(item, empty);
	        setGraphic(empty ? null : link);
	    }
	}	
}

