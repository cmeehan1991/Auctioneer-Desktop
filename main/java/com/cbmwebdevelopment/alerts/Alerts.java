/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.alerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;

import com.cbmwebdevelopment.main.MainApp;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * @author cmeehan
 */
public class Alerts {

    public Dialog<Integer> comboBoxAlert(String title, String headerText, String content, ObservableList<String> options){
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.initOwner(MainApp.desktopPane.getScene().getWindow());
        
        ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, cancelButtonType);
        
        Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDefaultButton(true);
        
        GridPane gridPane = new GridPane();
        GridPane.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setVgrow(gridPane, Priority.ALWAYS);
        
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(options);
        
        gridPane.add(new Label("Choose One:"),0,0);
        gridPane.add(comboBox,0,1);
        
        dialog.getDialogPane().setContent(gridPane);
        
        dialog.setOnCloseRequest((event)->{
            dialog.close();
        });
        
        dialog.setResultConverter(dialogButton->{
            if(dialogButton == submitButtonType){
                return comboBox.getSelectionModel().getSelectedIndex();
            }
            return null;
        });
        
        return dialog;
    }
    
    public CommandLinksDialog commandLinkDialog(String title, String headerText, String content, HashMap<String, HashMap<String, Boolean>> options){
        
        List<CommandLinksButtonType> links = new ArrayList<>();
        options.forEach((buttonTitle, nestedMap)->{
           nestedMap.forEach((buttonDescription, isDefault)->{
               links.add(new CommandLinksButtonType(buttonTitle, buttonDescription, isDefault));
           });
        });
        
        CommandLinksDialog dialog = new CommandLinksDialog(links);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        
        return dialog;
    }
    
    /**
     * Returns an error alert with an optional error display list. 
     * @param title
     * @param headerText
     * @param content
     * @param errors
     * @return 
     */
    public Alert errorAlert(String title, String headerText, String content, ArrayList<String> errors) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);

        if (errors != null) {
            // Expandable Exception
            TextArea textArea = new TextArea();
            errors.forEach((item) -> {
                textArea.appendText(item + "\n");
            });
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);

        }

        return alert;
    }
    
    /**
     * Returns an information alert. 
     * @param title
     * @param headerText
     * @param content
     * @return 
     */
    public Alert informationAlert(String title, String headerText, String content){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        return alert;
    }
    
    public TextInputDialog inputAlert(String title, String headerText, String content){
        TextInputDialog input = new TextInputDialog();
        input.setTitle(title);
        input.setHeaderText(headerText);
        input.setContentText(content);
        return input;
    }
}
