/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;

/**
 *
 * @author cmeehan
 */
public class ItemsTableController {

    public ObservableList<AllItems> data = FXCollections.observableArrayList();

    public void itemsTable(TableView<AllItems> tableView) {
        TableColumn<AllItems, String> itemIdColumn = new TableColumn<>("ID");
        TableColumn<AllItems, String> itemNameColumn = new TableColumn<>("Name");
        TableColumn<AllItems, String> itemDescriptionColumn = new TableColumn<>("Description");
        TableColumn<AllItems, String> auctionTypeColumn = new TableColumn<>("Type");
        TableColumn<AllItems, String> itemStatusColumn = new TableColumn<>("Status");

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        itemIdColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.10));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemNameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.25));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        itemDescriptionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.45));
        auctionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        auctionTypeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.1));
        itemStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        itemStatusColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.1));

        tableView.getColumns().setAll(itemIdColumn, itemNameColumn, itemDescriptionColumn, auctionTypeColumn, itemStatusColumn);
        tableView.setEditable(true);
        tableView.setItems(data);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }
    
    public static class AllItems {

        private final SimpleIntegerProperty id;
        private final SimpleStringProperty  name, description, type, status;

        public AllItems(int id, String name, String description, String type, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
            this.type = new SimpleStringProperty(type);
            this.status = new SimpleStringProperty(status);
        }

        public String getDescription() {
            return description.get();
        }

        public int getId() {
            return id.get();
        }

        public String getName() {
            return name.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getType() {
            return type.get();
        }

        public void setDescription(String value) {
            description.set(value);
        }

        public void setId(int value) {
            id.set(value);
        }

        public void setName(String value) {
            name.set(value);
        }

        public void setStatus(String value) {
            status.set(value);
        }

        public void setType(String value) {
            type.set(value);
        }
    }
}
