/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.auction.AuctionFXMLController;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController.AllItems;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ViewItemsFXMLController implements Initializable {

	@FXML
	Button addItemToAuctionButton;
	
	@FXML
	Separator addItemSeparator;
	
    @FXML
    TextField searchTextField;

    @FXML
    TableView<AllItems> itemsTableView;

    protected FXInternalWindow internalWindow;
    public AuctionFXMLController auctionController;
    private ItemsTableController tableController;

    @FXML
    protected void addItemToAuctionAction(ActionEvent event) {
    		// Add the item to the auction
    		ObservableList<AllItems> selectedItems = itemsTableView.getSelectionModel().getSelectedItems();
    		selectedItems.forEach(item->{
    			Auction auction = new Auction();
    			auction.addItem(String.valueOf(AuctionFXMLController.auctionId), String.valueOf(item.getId()));
    		});
    		
    		// Reset the auction items table
    		auctionController.refreshItemsTable(String.valueOf(AuctionFXMLController.auctionId));
    		
    		// Close the window
    		internalWindow.close();
    }
    
    @FXML
    protected void addNewItemAction(ActionEvent event) throws IOException {
        ItemMain itemMain = new ItemMain();
        itemMain.isNew = true;
        itemMain.itemsController = this;
        itemMain.start(new Stage());
    }

    @FXML
    protected void deleteSelectedItemsAction(ActionEvent event) {
        ObservableList<AllItems> selectedItems = itemsTableView.getSelectionModel().getSelectedItems();
        selectedItems.forEach((selectedItem) -> {
            // Remove the item from the database
            Item item = new Item();
            item.removeItem(selectedItem.getId());
            
            // Remove the item from the table
            itemsTableView.getItems().remove(selectedItem);
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
        tableController = new ItemsTableController();
        tableController.itemsTable(itemsTableView);
        populateTable(null);

        // Perform a search for the item(s) based on the user input in the search text field
        searchTextField.textProperty().addListener((obs, ov, nv) -> {
            if (nv.trim().isEmpty()) {
                populateTable(null);
            }else {
            		populateTable(nv.toString());
            }
        });
        
        // Handle a double click on an item in the items table. 
        itemsTableView.setRowFactory(tv->{
        		TableRow<AllItems> row = new TableRow<>();
        		row.setOnMouseClicked(evt ->{
        			if(evt.getClickCount() == 2 && (!row.isEmpty())) {
        				AllItems item = row.getItem();
        				viewSelectedItems(String.valueOf(item.getId()));
        			}
        		});
        		return row;
        });
    }

    protected void populateTable(String terms) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
        		ObservableList<AllItems> data = new Item().getAuctionItems(terms, null);
            Platform.runLater(() -> {
                itemsTableView.getItems().setAll(data);
            });
            executor.shutdown();
        });
    }
    
    @FXML
    protected void searchForItemAction(ActionEvent event) {
        String terms = searchTextField.getText();
        if (terms != null && !terms.trim().isEmpty()) {
            populateTable(terms);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Search Terms");
            alert.setContentText("Please fill in what you would like to search for.");
            alert.showAndWait();
        }
    }
    
    /**
     * Opens the item view
     * @param id
     */
    private void viewSelectedItems(String id) {
    		try {
	    		ItemMain itemMain = new ItemMain();
	    		itemMain.itemNumber = id;
	    		itemMain.isNew = false;
	    		itemMain.start(new Stage());
    		}catch(IOException ex) {
	    		System.err.println(ex.getMessage());
	    	}
    }

    @FXML
    protected void viewSelectedItemsAction(ActionEvent event) {
        System.out.println("View Selected");
        ObservableList<AllItems> selectedItems = itemsTableView.getSelectionModel().getSelectedItems();
        selectedItems.forEach(item -> {
        		viewSelectedItems(String.valueOf(item.getId()));
        });
    }

}
