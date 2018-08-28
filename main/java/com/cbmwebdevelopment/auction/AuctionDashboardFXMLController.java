/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import static com.cbmwebdevelopment.main.Values.AUCTION_FILTERS;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController.Auctions;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class AuctionDashboardFXMLController implements Initializable {

	@FXML
	PrefixSelectionComboBox<String> filterAuctionTableComboBox;

	@FXML
	TableView<Auctions> auctionTableView;

	@FXML
	ProgressIndicator progressIndicator;

	AuctionsTableViewController tableController;

	@FXML
	protected void createNewAuctionAction(ActionEvent event) throws IOException{
		AuctionMain auctionMain = new AuctionMain();
		auctionMain.auctionId = null;
		auctionMain.start(new Stage());
	}

	private void getAuctions(String filterBy){
		auctionTableView.setDisable(true);
		progressIndicator.setVisible(true);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(()->{
			ObservableList<Auctions> data = new Auction().getAuctions(filterBy);
			Platform.runLater(()->{
				auctionTableView.setItems(data);
				auctionTableView.setDisable(false);
				progressIndicator.setVisible(false);
			});
			executor.shutdown();
		});
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Hide the progress indicator on load
		progressIndicator.setVisible(false);

		// Set the table controller
		tableController = new AuctionsTableViewController();
		tableController.tableView(auctionTableView);
		getAuctions("-");

		// Set the filter by combo box
		filterAuctionTableComboBox.setItems(AUCTION_FILTERS);
		filterAuctionTableComboBox.getSelectionModel().select(0);
		filterAuctionTableComboBox.valueProperty().addListener((obs, ov, nv)->{
			if(nv != null && (!nv.toString().isEmpty())){
				getAuctions(nv.toString());
			}
		});

		// click listener on table controller
		auctionTableView.setRowFactory(tv -> {
			TableRow<Auctions> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if(event.getClickCount() == 2 && (!row.isEmpty())) {
					Auctions auctions = row.getItem();
					AuctionMain auctionMain = new AuctionMain();
					auctionMain.auctionId = auctions.getId().toString();
					try {
						auctionMain.start(new Stage());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return row;
		});
	}

	@FXML
	protected void viewExistingAuctionAction(ActionEvent event) throws IOException{
		AuctionMain auctionMain = new AuctionMain();
		auctionMain.auctionId = auctionTableView.getSelectionModel().getSelectedItem().getId().toString();
		auctionMain.start(new Stage());
	}

}
