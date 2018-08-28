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

public class BidderAuctionsTableController {

	ObservableList<BidderAuctions> data = FXCollections.observableArrayList();

	public void tableView(TableView<BidderAuctions> tableView) {
		// Initialize the columns
		TableColumn<BidderAuctions, Integer> idColumn = new TableColumn<>("Auction ID");
		TableColumn<BidderAuctions, String> auctionColumn = new TableColumn<>("Auction");
		TableColumn<BidderAuctions, LocalDate> dateColumn = new TableColumn<>("Auction Date");

		// Set the column width percentages
		idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
		auctionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.65));
		dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));

		// Set the column value factories
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		auctionColumn.setCellValueFactory(new PropertyValueFactory<>("auction"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

		tableView.getColumns().setAll(idColumn, auctionColumn, dateColumn);
		tableView.setItems(data);
	}

	/**
	 * Getter/setter for Bidder Auctions table view.
	 * 
	 * @author cmeehan
	 *
	 */
	public static class BidderAuctions {
		private SimpleIntegerProperty id;
		private SimpleStringProperty auction;
		private SimpleObjectProperty<LocalDate> date;

		public BidderAuctions(Integer id, String auction, LocalDate date) {
			this.id = new SimpleIntegerProperty(id);
			this.auction = new SimpleStringProperty(auction);
			this.date = new SimpleObjectProperty<>(date);
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

		public void setAuction(String val) {
			this.auction.set(val);
		}

		public void setDate(LocalDate val) {
			this.date.set(val);
		}

		public void setId(Integer val) {
			this.id.set(val);
		}
	}
}
