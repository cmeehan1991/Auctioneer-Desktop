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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

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

    @FXML
    Button forwardButton, backButton;

    @FXML
    StackPane stackPane;

    @FXML
    AnchorPane auctionsPane;

    AuctionsTableViewController tableController;
    AuctionFXMLController auctionController;

    @FXML
    protected void createNewAuctionAction(ActionEvent event) throws IOException {
        System.out.println("Create new auction");
        System.out.println(stackPane.getChildren().get(1));
        stackPane.getChildren().get(0).toBack();
        stackPane.getChildren().get(1).setVisible(true);
    }

    private void getAuctions(String filterBy) {
        auctionTableView.setDisable(true);
        progressIndicator.setVisible(true);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<Auctions> data = new Auction().getAuctions(filterBy);
            Platform.runLater(() -> {
                auctionTableView.setItems(data);
                auctionTableView.setDisable(false);
                progressIndicator.setVisible(false);
            });
            executor.shutdown();
        });
    }

    @FXML
    protected void viewExistingAuctionAction(ActionEvent event) throws IOException {
        String id = auctionTableView.getSelectionModel().getSelectedItem().getId().toString();
        auctionController.getAuction(id);
        stackPane.getChildren().get(1);
    }

    private void initViews() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionFXML.fxml"));
            AnchorPane auctionPane = loader.load();
            auctionController = (AuctionFXMLController) loader.getController();

            stackPane.getChildren().setAll(auctionsPane, auctionPane);
            stackPane.getChildren().get(0).toFront();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        // Set the table controller
        tableController = new AuctionsTableViewController();
        tableController.tableView(auctionTableView);
        getAuctions("-");

        // Set the filter by combo box
        filterAuctionTableComboBox.setItems(AUCTION_FILTERS);
        filterAuctionTableComboBox.getSelectionModel().select(0);
        
    }

    private void setListeners(){
        filterAuctionTableComboBox.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null && (!nv.toString().isEmpty())) {
                getAuctions(nv.toString());
            }
        });

        // click listener on table controller
        auctionTableView.setRowFactory(tv -> {
            TableRow<Auctions> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Auctions auctions = row.getItem();
                    String id = String.valueOf(auctions.getId());
                    auctionController.getAuction(id);
                    System.out.println("get auction");
                    System.out.println(stackPane.getChildren().get(1));
                    stackPane.getChildren().get(1);
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
    }

}
