package com.cbmwebdevelopment.tablecontrollers;

import java.time.LocalDate;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BidderAuctionHistoryTableController {
	
	ObservableList<BidderAuctionHistory> data = FXCollections.observableArrayList();
	
	public void tableView(TableView<BidderAuctionHistory> tableView) {
		// Initialize the columns
		TableColumn<BidderAuctionHistory, Integer> idColumn = new TableColumn<>("Auction ID");
		TableColumn<BidderAuctionHistory, String> auctionColumn = new TableColumn<>("Auction");
		TableColumn<BidderAuctionHistory, LocalDate> dateColumn = new TableColumn<>("Auction Date"); 
		TableColumn<BidderAuctionHistory, Integer> itemsColumn = new TableColumn<>("Items Won");
		
		// Set the column width percentages
		idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
		auctionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.50));
		dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
		itemsColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
		
		// Set the column value factories
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		auctionColumn.setCellValueFactory(new PropertyValueFactory<>("auction"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
		
		// Set the table columns and default data
		tableView.getColumns().setAll(idColumn, auctionColumn, dateColumn, itemsColumn);
		tableView.setItems(data);
	}
	
	/**
	 * Getter/setter for Bidder Auctions table view.
	 * @author cmeehan
	 *
	 */
	public static class BidderAuctionHistory{
		private SimpleIntegerProperty id, items;
		private SimpleStringProperty auction;
		private SimpleObjectProperty<LocalDate> date;
		
		
		public BidderAuctionHistory(Integer id, String auction, LocalDate date, Integer items) {
			this.id = new SimpleIntegerProperty(id);
			this.auction = new SimpleStringProperty(auction);
			this.date = new SimpleObjectProperty<>(date);
			this.items = new SimpleIntegerProperty(items);
		}
		
		public String getAuction() {
			return this.auction.get();
		}
		
		public LocalDate getDate() {
			return this.date.get();
		}
		
		public Integer getId() {
			return this.id.get();
		}
		
		public Integer getItems() {
			return this.items.get();
		}
		
		public void setAuction(String val) {
			this.auction.set(val);
		}
		
		public void setDate(LocalDate val) {
			this.date.set(val);
		}
		
		public void setId(Integer val) {
			this.id.set(val);
		}
		
		public void setItems(Integer val) {
			this.items.set(val);
		}
	}
}
