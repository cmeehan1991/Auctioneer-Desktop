/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import java.io.IOException;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

import javafx.application.Application;
import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.WindowController;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

/**
 *
 * @author cmeehan
 */
public class UserInformationMain extends Application {


    public boolean newUser;
    public String userId;

    public FXDesktopPane desktopPane = MainApp.desktopPane;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserInformation.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        UserInformationController controller = (UserInformationController) loader.getController();
        controller.isNew = newUser;
        
        if(!newUser){
            Users users = new Users();
            users.setUserInformation(controller, userId);
        }
        
        FXInternalWindow internalWindow = new WindowController().newInternalWindow("User Information", root);
        desktopPane.getWindows().add(internalWindow);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
