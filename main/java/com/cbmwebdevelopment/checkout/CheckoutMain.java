/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.checkout;

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
public class CheckoutMain extends Application {

	private FXDesktopPane desktopPane = MainApp.desktopPane;

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CheckoutFXML.fxml"));
		AnchorPane root = (AnchorPane) loader.load();
		CheckoutFXMLController controller = loader.getController();
		controller.actionToolbar.setDisable(true);
		controller.bidderCheckoutTabPane.setDisable(true);

		FXInternalWindow windowPane = new WindowController().newInternalWindow("Check Out", root);
		desktopPane.getWindows().add(windowPane);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
