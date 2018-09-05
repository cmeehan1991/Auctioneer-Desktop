/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import java.io.IOException;

import com.cbmwebdevelopment.auction.AuctionFXMLController;
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
public class BidderListMain extends Application {

	public boolean addAuctionBidder = false;
	public AuctionFXMLController auctionController;
	public String auctionId;

	public FXDesktopPane desktopPane = MainApp.desktopPane;
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BidderListFXML.fxml"));
		AnchorPane anchorPane = (AnchorPane) loader.load();
		BidderListFXMLController bidderListController = loader.getController();
		
		if(addAuctionBidder) {
			//bidderListController.auctionController = this.auctionController;
			bidderListController.addSelectedBidderButton.setVisible(true);
			bidderListController.auctionId = this.auctionId;
		}
		
		FXInternalWindow internalWindow = new WindowController().newInternalWindow("Bidder List", anchorPane);
		//bidderListController.internalWindow = internalWindow;
		desktopPane.getWindows().add(internalWindow);
	}
	
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	

}
