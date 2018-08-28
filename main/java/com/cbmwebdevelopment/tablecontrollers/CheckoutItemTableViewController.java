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
public class CheckoutItemTableViewController {

    ObservableList<ItemData> data = FXCollections.observableArrayList();

    /**
     * Construct the table view
     *
     * @param tableView
     */
	public void tableView(TableView<ItemData> tableView) {
        TableColumn<ItemData, String> idColumn = new TableColumn<>("Item ID");
        TableColumn<ItemData, String> nameColumn = new TableColumn<>("Item Name");
        TableColumn<ItemData, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<ItemData, String> winningBidAmountColumn = new TableColumn<>("Amount");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        descriptionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.40));

        winningBidAmountColumn.setCellValueFactory(new PropertyValueFactory<>("winningBidAmount"));
        winningBidAmountColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        tableView.getItems().setAll(data);
        tableView.getColumns().setAll(idColumn, nameColumn, descriptionColumn, winningBidAmountColumn);
    }
    
    public static class ItemData {

        SimpleStringProperty itemId, itemName, itemDescription, winningBidAmount;

        public ItemData(String itemId, String itemName, String itemDescription, String winningBidAmount) {
            this.itemId = new SimpleStringProperty(itemId);
            this.itemName = new SimpleStringProperty(itemName);
            this.itemDescription = new SimpleStringProperty(itemDescription);
            this.winningBidAmount = new SimpleStringProperty(winningBidAmount);
        }

        public String getItemDescription() {
            return this.itemDescription.get();
        }

        public String getItemId() {
            return this.itemId.get();
        }

        public String getItemName() {
            return this.itemName.get();
        }

        public String getWinningBidAmount() {
            return this.winningBidAmount.get();
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription.set(itemDescription);
        }

        public void setItemId(String itemId) {
            this.itemId.set(itemId);
        }

        public void setItemName(String itemName) {
            this.itemName.set(itemName);
        }

        public void setWinningBidAmount(String winningBidAmount) {
            this.winningBidAmount.set(winningBidAmount);
        }
    }
}
