/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class ViewBidsTableViewController {

	public ObservableList<Bids> data = FXCollections.observableArrayList();
    
    public void setTableView(TableView<Bids> tableView){
        TableColumn<Bids, String> idColumn = new TableColumn<>("ID");
        TableColumn<Bids, String> itemNameColumn = new TableColumn<>("Item Name");
        TableColumn<Bids, String> bidderNameColumn = new TableColumn<>("Winner");
        TableColumn<Bids, String> amountColumn = new TableColumn<>("Winning Bid");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bidId"));
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemNameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));
        
        bidderNameColumn.setCellValueFactory(new PropertyValueFactory<>("bidderName"));
        bidderNameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));
        
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        
        
        // Set the columns
        tableView.getColumns().setAll(idColumn, itemNameColumn, bidderNameColumn, amountColumn);
        
        // Set the items
        tableView.getItems().setAll(data);
    }

    public static class Bids {
        SimpleStringProperty bidId, itemName, bidderName, amount;
        public Bids(String bidId, String itemName, String bidderName, String amount){
            this.bidId = new SimpleStringProperty(bidId);
            this.itemName = new SimpleStringProperty(itemName);
            this.bidderName = new SimpleStringProperty(bidderName);
            this.amount = new SimpleStringProperty(amount);
        }
        
        public String getAmount(){
            return this.amount.getValue();
        }
        
        public String getBidderName(){
            return this.bidderName.getValue();
        }
        
        public String getBidId(){
            return this.bidId.getValue();
        }
        
        public String getItemName(){
            return this.itemName.getValue();
        }
        
        public void setAmount(String amount){
            this.amount.set(amount);
        }
        
        public void setBidderName(String bidderName){
            this.bidderName.set(bidderName);
        }
        
        public void setBidId(String itemId){
            this.bidId.set(itemId);
        }
        
        public void setItemName(String itemName){
            this.itemName.set(itemName);
        }
        
        
    }
}
