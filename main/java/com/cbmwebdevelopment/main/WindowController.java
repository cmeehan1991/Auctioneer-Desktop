/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.main;

import javafx.collections.ObservableList;

import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow.State;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author cmeehan
 */
public class WindowController {
    
    ObservableList<String> buttons = FXCollections.observableArrayList();
    
    private Button minimizedWindow(FXInternalWindow fxInternalWindow){
        
        
        // Close symbol
        Button closeButton = new Button("X");
        closeButton.setOnAction(event->{
            Button button = (Button) event.getTarget();
            MainApp.toolbar.getItems().remove(button.getParent());
            fxInternalWindow.close();
        });

        // Initialize the button
        Button button = new Button(fxInternalWindow.getTitle(), closeButton);
                
        button.setText(fxInternalWindow.getTitle());
        button.setDisable(false);
        button.disableProperty().bind(new SimpleBooleanProperty(false));
        // Set the on click property
        button.setOnAction(event->{
            fxInternalWindow.visibleProperty().bindBidirectional(new SimpleBooleanProperty(true));
            fxInternalWindow.setVisible(true);
            fxInternalWindow.setState(State.NORMAL);
            MainApp.toolbar.getItems().remove(event.getTarget());
        });
        
        // Hide the window
        fxInternalWindow.setVisible(false);
                
        return button;
    }

    
    public FXInternalWindow newInternalWindow(String title, AnchorPane view) {
        FXInternalWindow fxInternalWindow = new FXInternalWindow();
        fxInternalWindow.setContent(view);
        fxInternalWindow.setTitle(title);
        
        // Set the layout
        fxInternalWindow.setLayoutX(10);
        fxInternalWindow.setLayoutY(100);
        
        // Set the window size
        fxInternalWindow.setMinWidth(view.getMinWidth());
        fxInternalWindow.setMaxWidth(view.getMaxWidth());
        fxInternalWindow.setPrefWidth(view.getPrefWidth());
        fxInternalWindow.setPrefHeight(view.getPrefHeight());
        fxInternalWindow.setMinHeight(view.getMinHeight());
        fxInternalWindow.setMinWidth(view.getMinWidth());
        
        // Bind the view size to the internal window
        fxInternalWindow.setStyle("-fx-background-color:#ffffff;");
       
        fxInternalWindow.setIcon(new Image(getClass().getResource("/images/auction.jpg").toString()));
        fxInternalWindow.setOnStateChanged((change)->{
            System.out.println("State change: " + change.getNewState());
            if(change.getNewState() == State.MINIMIZED){
                MainApp.toolbar.getItems().add(minimizedWindow(fxInternalWindow));
            }
        });
        
        return fxInternalWindow;
    }
}
