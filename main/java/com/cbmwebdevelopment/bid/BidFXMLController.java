/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.items.Item;
import com.cbmwebdevelopment.notifications.SnackbarNotifications;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class BidFXMLController implements Initializable {

    @FXML
    VBox vBox;

    @FXML
    public JFXTextField itemNumberTextField, itemNameTextField, bidderNumberTextField, bidAmountTextField;

    @FXML
    JFXTextArea itemDescriptionTextArea;

    @FXML
    Label itemNumberLabel, itemNameLabel, itemDescriptionLabel, bidderNumberLabel, bidAmountLabel;

    protected String itemNumber, itemName, itemDescription, bidderNumber, bidAmount;
    private ArrayList<String> requiredFields;
    protected boolean isNew;
    public Alert alert = new Alerts().informationAlert("Item Doesn't Exist", "Item doesn't exist.", "The item you entered does not exist. Please try again.");

    /**
     * Assign the input values to their respective objects.
     */
    private void assignValues() {
        itemNumber = itemNumberTextField.getText();
        itemName = itemNameTextField.getText();
        itemDescription = itemDescriptionTextArea.getText();
        bidderNumber = bidderNumberTextField.getText();
        bidAmount = bidAmountTextField.getText();
    }

    /**
     * Get the item information and display the name and description.
     *
     * @param id
     * @param controller
     */
    private void getItemInformation(String id, BidFXMLController controller) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Item item = new Item();
            HashMap<String, String> selectedItem = item.getItem(id);
            Platform.runLater(() -> {
                if (selectedItem.isEmpty()) {
                    new SnackbarNotifications().errorSnackbar("That item does not exist.", vBox);
                } else {
                    itemNameTextField.setText(selectedItem.get("NAME"));
                    itemDescriptionTextArea.setText(selectedItem.get("DESCRIPTION"));
                    bidderNumberTextField.setDisable(false);
                    bidAmountTextField.setDisable(false);
                    bidderNumberTextField.requestFocus();
                }
            });

            executor.shutdown();

        });
    }

    public void loadBid(String bidId) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            System.out.println("executor");
            Bid bid = new Bid();
            System.out.println("bid");
            HashMap<String, String> bidData = bid.getBid(bidId);
            System.out.println("biddata");
            System.out.println(bidData);
            if (bidData != null) {
                Platform.runLater(() -> {
                    itemNumberTextField.setText(bidData.get("ITEM_ID"));
                    itemNameTextField.setText(bidData.get("ITEM_NAME"));
                    itemDescriptionTextArea.setText(bidData.get("ITEM_DESCRIPTION"));
                    bidderNumberTextField.setText(bidData.get("BIDDER_ID"));
                    bidAmountTextField.setText(bidData.get("BID_AMOUNT"));
                });
            }
            executor.shutdown();
        });
    }

    protected void submitBid(JFXDialog dialog) {
        if (validateFields()) {
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(() -> {
                Bid bid = new Bid();
                boolean success = bid.submitBid(bidAmount, bidderNumber, itemNumber, null); // This needs to be updated. 
                Platform.runLater(() -> {
                    if (success) {
                        dialog.close();
                    } else {
                        new SnackbarNotifications().errorSnackbar("Bid failed to save.", vBox);
                    }
                });

                executor.shutdown();
            });
        } else {
            System.out.println("Not valid");
            Alert alert = new Alerts().errorAlert("Missing Items", "Missing required items.", "Please correct the following items", requiredFields);
            alert.showAndWait();
        }
    }

    /**
     * Validate that the required fields have been filled in.
     *
     * @return
     */
    private boolean validateFields() {
        boolean isValid = false;

        // Assign the values
        assignValues();

        // Initialize the required fields array list
        // This needs to be done every time so that the list is empty upon submit.
        requiredFields = new ArrayList<>();

        if (itemNumber == null || itemNumber.isEmpty()) {
            requiredFields.add("Item Number");
            itemNumberTextField.setStyle("-fx-border-color: #ff0000");
            itemNumberLabel.setStyle("-fx-text-fill: #ff0000");
        }

        if (bidderNumber == null || bidderNumber.isEmpty()) {
            requiredFields.add("Bidder Number");
            bidderNumberTextField.setStyle("-fx-border-color: #ff0000");
            bidderNumberLabel.setStyle("-fx-text-fill: #ff0000");
        }

        if (bidAmount == null || bidAmount.isEmpty()) {
            requiredFields.add("Bid Amount");
            bidAmountTextField.setStyle("-fx-border-color: #ff0000");
            bidAmountLabel.setStyle("-fx-text-fill: #ff0000");
        }

        if (requiredFields == null || requiredFields.isEmpty()) {
            isValid = true;
        }

        return isValid;
    }

    @FXML
    protected void itemSearchAction(ActionEvent event) {
        getItemInformation(itemNumberTextField.getText(), this);
    }

    /**
     * Initialize any view properties. 
     */
    private void initViews() {
        // Disable everything except the item number textfield 
        itemNameTextField.setDisable(true);
        itemDescriptionTextArea.setDisable(true);
        bidderNumberTextField.setDisable(true);
        bidAmountTextField.setDisable(true);
    }

    /**
     * Set any action listeners
     */
    private void setListeners() {
        itemNumberTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                itemNameTextField.setDisable(false);
                itemDescriptionTextArea.setDisable(false);
                itemNameTextField.setEditable(false);
                itemDescriptionTextArea.setEditable(false);
                bidderNumberTextField.setDisable(true);
                bidAmountTextField.setDisable(true);
            } else {
                itemNameTextField.setDisable(true);
                itemDescriptionTextArea.setDisable(true);
                itemNameTextField.clear();
                itemDescriptionTextArea.clear();
                bidderNumberTextField.clear();
                bidAmountTextField.clear();
            }
        });

        itemNumberTextField.setOnKeyReleased((event) -> {
            if ((event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) && itemNumberTextField.getText() != null && !itemNumberTextField.getText().trim().isEmpty()) {
                getItemInformation(itemNumberTextField.getText(), this);
            }
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
        initViews();
        setListeners();
    }

}
