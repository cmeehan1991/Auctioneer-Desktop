/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

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
public class ViewItemsMain extends Application {
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	public boolean addItemsToAuction;
	public AuctionFXMLController auctionController;

	private FXDesktopPane desktopPane = MainApp.desktopPane;

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewItemsFXML.fxml"));
		AnchorPane rootAnchorPane = (AnchorPane) loader.load();
		ViewItemsFXMLController controller = (ViewItemsFXMLController) loader.getController();
		FXInternalWindow fxInternalWindow = new WindowController().newInternalWindow("All Items", rootAnchorPane);

		if (addItemsToAuction) {
			controller.addItemToAuctionButton.setVisible(true);
			controller.addItemSeparator.setVisible(true);
			controller.auctionController = auctionController;
			controller.internalWindow = fxInternalWindow;
		}
		desktopPane.getWindows().add(fxInternalWindow);
	}

}
