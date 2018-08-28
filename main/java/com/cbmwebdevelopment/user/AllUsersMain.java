package com.cbmwebdevelopment.user;

import java.io.IOException;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.WindowController;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AllUsersMain extends Application{
	
	private FXDesktopPane desktopPane = MainApp.desktopPane;
	
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AllUsers.fxml"));
		AnchorPane rootPane = (AnchorPane) loader.load();
		
		// Set the internal window
		FXInternalWindow internalWindow = new WindowController().newInternalWindow("All Users", rootPane);

		// Get the controller and set the window variable
		AllUsersController controller = (AllUsersController) loader.getController();
		controller.window = internalWindow;
		
		// Add the window to the desktop pane
		desktopPane.getWindows().add(internalWindow);
	}
	


	public static void main(String...args) {
		launch(args);
	}
	
}
