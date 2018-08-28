/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
/**
 *
 * @author cmeehan
 */
public class AuctionsTableViewController {
   
    
    public ObservableList<Auctions> data = FXCollections.observableArrayList();
    
    public void tableView(TableView<Auctions> tableView){
        TableColumn<Auctions, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Auctions, String> titleColumn = new TableColumn<>("Auction");
        TableColumn<Auctions, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Auctions, String> typeColumn = new TableColumn<>("Type");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        titleColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        typeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        
        tableView.getColumns().setAll(idColumn, titleColumn, dateColumn, typeColumn);
        tableView.getItems().setAll(data);
        tableView.setEditable(false);

    }
    
    public static class Auctions{
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty title, date, type;
        
        public Auctions(Integer id, String title, String date, String type){
            this.id = new SimpleIntegerProperty(id);
            this.title = new SimpleStringProperty(title);
            this.date = new SimpleStringProperty(date);
            this.type = new SimpleStringProperty(type);
        }
        
        public String getDate(){
            return date.get();
        }
        
        public Integer getId(){
            return id.getValue();
        }
        
        public String getTitle(){
            return title.get();
        }
        
        public String getType(){
            return type.get();
        }
        
        public void setDate(String val){
            date.set(val);
        }
        
        public void setId(Integer val){
            id.set(val);
        }
        
        public void setTitle(String val){
            title.set(val);
        }
        
        public void setType(String val){
            type.set(val);
        }
    }
}
