/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.main.Values;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class UserInformationController implements Initializable {

    @FXML 
    AnchorPane anchorPane;
    
    @FXML
    PrefixSelectionComboBox<String> prefixComboBox, suffixComboBox, stateComboBox;

    @FXML
    TextField firstNameTextField, lastNameTextField, streetAddressTextField, secondaryAddressTextField, cityTextField, postalCodeTextField, telephoneTextField, emailTextField, usernameTextField;

    @FXML
    Button resetPasswordButton, saveUserButton;

    @FXML
    Label userIdLabel;

    protected String prefix, firstName, lastName, suffix, streetAddress, secondaryAddress, city, state, postalCode, telephone, email, username, userId;
    private ArrayList<String> missingItems;
    protected boolean isNew;

    private void assignValues() {
        prefix = prefixComboBox.getSelectionModel().getSelectedIndex() >= 0 ? prefixComboBox.getSelectionModel().getSelectedItem().toString() : "";
        firstName = firstNameTextField.getText();
        lastName = lastNameTextField.getText();
        suffix = suffixComboBox.getSelectionModel().getSelectedIndex() >= 0 ? suffixComboBox.getSelectionModel().getSelectedItem().toString() : "";
        streetAddress = streetAddressTextField.getText();
        secondaryAddress = secondaryAddressTextField.getText();
        city = cityTextField.getText();
        state = stateComboBox.getSelectionModel().getSelectedIndex() >= 0 ? stateComboBox.getSelectionModel().getSelectedItem().toString() : "";
        postalCode = postalCodeTextField.getText();
        telephone = telephoneTextField.getText();
        email = emailTextField.getText();
        username = usernameTextField.getText();
        userId = userIdLabel.getText();

    }


    @FXML
    protected void resetPassword(ActionEvent event) {
	    	if(validateEntries()) {
	        Users users = new Users();
	        users.resetPassword(username, userId);
	    	}
    }

    @FXML
    protected void saveUser(ActionEvent event) {
        if (validateEntries()) {
            if (userId.equals("N/A")) {
//                new Users().addNewUser(this);
            } else {
                new Users().updateUserInformation(this);
            }
        } else {
            Alert alert = new Alerts().errorAlert("Missing Data", "Missing required fields", "Missing Items:", missingItems);
            alert.showAndWait();
        }
    }

    private boolean validateEntries() {
        // Assing the values for validation
        assignValues();

        // Initialize the missingItems ArrayList
        missingItems = new ArrayList<>();

        // Check required items
        if (firstName == null || firstName.trim().isEmpty()) {
            missingItems.add("First Name");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            missingItems.add("Last Name");
        }

        if (streetAddress == null || firstName.trim().isEmpty()) {
            missingItems.add("Street Address");
        }

        if (city == null || city.trim().isEmpty()) {
            missingItems.add("City");
        }

        if (state == null || state.trim().isEmpty()) {
            missingItems.add("State");
        }

        if (postalCode == null || postalCode.trim().isEmpty()) {
            missingItems.add("Postal Code");
        }

        if (telephone == null || telephone.trim().isEmpty()) {
            missingItems.add("Telephone Number");
        }

        if (telephone == null || telephone.trim().isEmpty()) {
            missingItems.add("Telephone Number");
        }

        if (email == null || email.trim().isEmpty()) {
            missingItems.add("Email Address");
        }

        return missingItems.isEmpty();
    }

    /**
     * Validate the username 
     * @param username
     * @param userId 
     */
    private void validateUsername(String username, String userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            boolean usernameExists = new Users().userExists(username, userId);
            if (usernameExists) {
                Platform.runLater(() -> {
                    usernameTextField.setStyle("-fx-border-color:#ff0000;");
                    saveUserButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    usernameTextField.setStyle("-fx-border-color:#008000;");
                    saveUserButton.setDisable(false);
                });
            }
            executor.shutdown();
        });
    }


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set the values for the combo boxes
        prefixComboBox.setItems(Values.PREFIXES);
        suffixComboBox.setItems(Values.SUFFIXES);
        stateComboBox.setItems(Values.STATES);

        usernameTextField.textProperty().addListener((obs, ov, nv) -> {
            if (!isNew) {
                validateUsername(nv, userIdLabel.getText());
            } else if (nv.trim().isEmpty()) {
                usernameTextField.setStyle("-fx-border-color:#ff0000;");
                saveUserButton.setDisable(true);
            } else {
                validateUsername(nv, null);
            }
        });
    }
    
}
