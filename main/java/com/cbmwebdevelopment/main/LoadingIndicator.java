/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.main;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class LoadingIndicator  {

    public Pane loadingIndicator(){
        
        Pane pane = new Pane();
        
        // Create an HBox for the indicator and label
        HBox hBox = new HBox();
        hBox.getChildren().add(indicator());        
        hBox.getChildren().add(new Label("Loading"));
        
        // Add the hBox to the pane
        pane.getChildren().add(hBox);
        pane.getStyleClass().add("loadingIndicator");
        
        return pane;
    }
    
    private ProgressIndicator indicator(){
        ProgressIndicator progressIndicator = new ProgressIndicator();
        
        // Set progress to indeterminate
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        
        return progressIndicator;
    }
    
}
