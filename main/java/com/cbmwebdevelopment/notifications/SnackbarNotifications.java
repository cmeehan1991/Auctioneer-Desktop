/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.notifications;

import com.jfoenix.controls.JFXSnackbar;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;

/**
 *
 * @author cmeehan
 */
public class SnackbarNotifications {
    
    /**
     * Create a general snack bar to be displayed when there is some type of error
     * such as missing information. 
     * @param content 
     * @param vBox 
     */
    public void errorSnackbar(String content, VBox vBox){
        JFXSnackbar snackbar = new JFXSnackbar(vBox);
        EventHandler actionHandler = (EventHandler) (Event event) -> {
            snackbar.unregisterSnackbarContainer(vBox);
        };
        snackbar.show(content, "OK", 5000, actionHandler);
    } 
}
