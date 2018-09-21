/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author cmeehan
 */
public class BidDialog {
    public String bidId;
    
    public JFXDialog bidDialog(StackPane stackPane){
        try{
            JFXDialogLayout content = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BidFXML.fxml"));
            VBox vBox = loader.load();
            BidFXMLController bidController = (BidFXMLController) loader.getController();
            
            System.out.println("Bid ID: " + bidId);
            if(bidId != null){
                
                bidController.loadBid(bidId);
            }
            
            JFXButton saveBidButton = new JFXButton("Save Bid");
            saveBidButton.setOnAction(evt -> {
                bidController.submitBid(dialog);
            });
            
            JFXButton closeButton = new JFXButton("Close");
            closeButton.setOnAction(evt -> {
                dialog.close();
            });
            content.setHeading(new Label("Winning Bid"));
            
            content.setBody(vBox);
            content.setActions(saveBidButton, closeButton);
            dialog.setOverlayClose(true);
            
            return dialog;
            
        }catch(IOException ex){
            System.err.println("Bid dialog: " + ex.getMessage());
            return errorDialog(stackPane);
        }
    }
    
    public JFXDialog errorDialog(StackPane stackPane){
        return new JFXDialog(stackPane, new JFXDialogLayout(), JFXDialog.DialogTransition.BOTTOM);
    }
}
