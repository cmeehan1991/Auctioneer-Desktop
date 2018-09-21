/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.tablecontrollers.ViewBidsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.ViewBidsTableViewController.Bids;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ViewBidsFXMLController implements Initializable {

    @FXML
    TableView<Bids> bidsTableView;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    JFXComboBox<String> auctionSelectionComboBox;

    ViewBidsTableViewController tableViewController;
    private final ObservableList<Bids> DATA = FXCollections.observableArrayList();
    private Bid bid = new Bid();
    private ObservableList<String> auctionIds, auctionNames;
    private HashMap<String, String> auctions;
    public StackPane stackPane;

    @FXML
    protected void addBidAction(ActionEvent event) {
        JFXDialog dialog = new BidDialog().bidDialog(stackPane);
        dialog.show();
    }

    @FXML
    protected void refreshFeedAction(ActionEvent event) {
        String auction = auctionSelectionComboBox.getSelectionModel() == null ? null : auctionSelectionComboBox.getSelectionModel().getSelectedItem();
        getAuctionItems(auction);
    }

    /**
     * Get the auction items and set the table data. Using a cached thread pool
     * to do this asynchronously.
     *
     * @param auction
     */
    protected void getAuctionItems(String auction) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<Bids> data = new Bid().getBids(auction);
            Platform.runLater(() -> {
                bidsTableView.getItems().setAll(data);
            });
            executor.shutdown();
        });
    }

    /**
     * Initialize the table view. Show all items by default.
     */
    private void initTableView() {
        tableViewController = new ViewBidsTableViewController();
        tableViewController.setTableView(bidsTableView);
        getAuctionItems(null);
    }

    private void initViews() {
        auctionNames = observableArrayList();
        auctionIds = observableArrayList();
        auctions = new Auction().getAuctionNames();
        auctions.forEach((key, value) -> {
            auctionIds.add(key);
            auctionNames.add(value);
        });
        auctionSelectionComboBox.getItems().setAll(auctionNames);
    }

    private void setActionListeners() {
        auctionSelectionComboBox.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null && !nv.trim().isEmpty()) {
                String auction = auctionIds.get(auctionSelectionComboBox.getSelectionModel().getSelectedIndex());
                System.out.println("Auction: " + auction);
                getAuctionItems(auction);
            }
        });

        // Set listener for double click on row
        bidsTableView.setRowFactory(tv -> {
            TableRow<Bids> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                
               System.out.println(row);
                
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Bids bids = row.getItem();
                    BidDialog bidDialog = new BidDialog();
                    bidDialog.bidId = bids.getBidId();
                    System.out.println(bids.getBidId());
                    JFXDialog dialog = bidDialog.bidDialog(stackPane);
                    dialog.show();
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
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0.0);
        initViews();
        initTableView();
        setActionListeners();
    }

}
