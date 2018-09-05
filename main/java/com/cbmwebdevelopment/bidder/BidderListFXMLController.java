/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.NotificationPane;
import javafx.application.Platform;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.auction.AuctionController;
import com.cbmwebdevelopment.tablecontrollers.BiddersTableController;
import com.cbmwebdevelopment.tablecontrollers.BiddersTableController.Bidders;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class BidderListFXMLController implements Initializable {

    @FXML
    TextField searchInput;

    @FXML
    TableView<Bidders> biddersTableView;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    Button addSelectedBidderButton;

    public AuctionController auctionController;
    private BiddersTableController btc;
    public String auctionId;
    private ArrayList<String> bidders;

    private void getBidders(String terms) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            //ObservableList<Bidders> data = new Bidder().getBidders(terms);
            Platform.runLater(() -> progressIndicator.toFront());
            Platform.runLater(() -> {
                biddersTableView.getItems().setAll(new Bidder().getBidders(terms));
            });
            Platform.runLater(() -> progressIndicator.setVisible(false));
            executor.shutdown();
        });
    }

    @FXML
    protected void addSelectedBidderAction(ActionEvent event) {
        ObservableList<Bidders> selectedBidders = biddersTableView.getSelectionModel().getSelectedItems();
        if (selectedBidders != null) {
            Auction auction = new Auction();
            bidders = new ArrayList<>();
            selectedBidders.forEach(bidder -> {
                boolean bidderExists = new Auction().checkForBidder(auctionId, String.valueOf(bidder.getId()));
                if (!bidderExists) {
                    auction.addAttendee(Integer.parseInt(auctionId), bidder.getId());
                } else {
                    bidders.add(String.valueOf(bidder.getId()) + " - " + bidder.getName());
                }
            });
            if (!bidders.isEmpty()) {
                Alert alert = new Alerts().errorAlert("Auction Bidders Exist", "Selected Bidders Associated With Auction", "The following bidders will not be added to the auction because they have already been assigned to the auction:", bidders);
                alert.showAndWait();
            }
            auctionController.refreshBidderTable(auctionId);

        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Auction Bidders");
            alert.setHeaderText("No bidders selected");
            alert.setContentText("You must select at least one bidder to add to the auction.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void search(ActionEvent event) {
        if (searchInput != null && !searchInput.getText().trim().isEmpty()) {
            getBidders(searchInput.getText());
        } else {
            System.out.println("Showing notification");
            NotificationPane np = new NotificationPane();
            np.setText("Please enter something to search for.");
            np.show();
        }
    }

    @FXML
    protected void viewBidder(ActionEvent event) {
        BidderMain bidderMain = new BidderMain();

        ObservableList<Bidders> bidders = biddersTableView.getSelectionModel().getSelectedItems();
        bidders.forEach((bidder) -> {
            bidderMain.isNew = false;
            bidderMain.bidderId = String.valueOf(bidder.getId());
            try {
                bidderMain.start(new Stage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }
    
    /**
     * Initialize the views, set table controllers, etc.
     */
    private void initViews() {
        if (auctionId != null) {
            addSelectedBidderButton.setVisible(true);
        } else {
            addSelectedBidderButton.setVisible(false);
        }

        btc = new BiddersTableController();
        btc.biddersTable(biddersTableView);
        // Get all of the bidders
        getBidders(null);

    }

    /**
     * Set any action listeners
     */
    private void setListeners() {
        // Listen for the text to change on the search input
        searchInput.textProperty().addListener((obs, ov, nv) -> {
            if (nv == null || nv.trim().isEmpty()) {
                getBidders(null);
            }
        });

        // Set click listener for table
        biddersTableView.setRowFactory(tv -> {
            TableRow<Bidders> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    BidderMain bidderMain = new BidderMain();
                    bidderMain.isNew = false;
                    bidderMain.bidderId = String.valueOf(row.getItem().getId());
                    try {
                        bidderMain.start(new Stage());
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            });
            return row;
        });
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initViews();
        setListeners();
    }
}
