/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import java.io.IOException;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.WindowController;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class BidMain extends Application {

    public boolean isNew;
    public String bidId;

    private FXDesktopPane desktopPane = MainApp.desktopPane;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BidFXML.fxml"));
        AnchorPane anchorPane = (AnchorPane) loader.load();
        BidFXMLController controller = (BidFXMLController) loader.getController();
        if (!isNew && bidId != null) {
            controller.isNew = false;
            controller.loadBid(bidId);
        }

        FXInternalWindow window = new WindowController().newInternalWindow("Bid", anchorPane);
        desktopPane.getWindows().add(window);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
