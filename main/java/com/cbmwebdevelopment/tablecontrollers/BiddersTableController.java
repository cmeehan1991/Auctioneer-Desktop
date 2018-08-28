/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class BiddersTableController {
      
    private ObservableList<Bidders> data = FXCollections.observableArrayList();
    
    public void biddersTable(TableView<Bidders> tableView){
        TableColumn<Bidders, String> idColumn = new TableColumn<>("ID");
        TableColumn<Bidders, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Bidders, String> cityColumn = new TableColumn<>("City");
        TableColumn<Bidders, String> stateColumn = new TableColumn<>("State");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        
        
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.10));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.40));
        cityColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.25));
        stateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.25));
        
        tableView.getColumns().setAll(idColumn, nameColumn, cityColumn, stateColumn);
        tableView.getItems().setAll(data);
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    public static class Bidders{
        final SimpleIntegerProperty id;
        final SimpleStringProperty  name, city, state;
          
        public Bidders(int id, String name, String city, String state){
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.city = new SimpleStringProperty(city);
            this.state = new SimpleStringProperty(state);
        }
        
        public String getCity(){
            return city.get();
        }
        
        public int getId(){
            return id.get();
        }
        
        public String getName(){
            return name.get();
        }
        
        public String getState(){
            return state.get();
        }
        
        public void setCity(String value){
            city.set(value);
        }
        
        public void setId(int value){
            id.set(value);
        }
        
        public void setName(String value){
            name.set(value);
        }
        
        public void setState(String value){
            state.set(value);
        }
        
    }
}
