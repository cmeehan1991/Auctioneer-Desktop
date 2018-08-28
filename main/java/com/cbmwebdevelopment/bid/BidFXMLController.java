/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.items.Item;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class BidFXMLController implements Initializable {

    @FXML
    public TextField itemNumberTextField, itemNameTextField, itemDescriptionTextField, bidderNumberTextField, bidAmountTextField;

    @FXML
    Label itemNumberLabel, itemNameLabel, itemDescriptionLabel, bidderNumberLabel, bidAmountLabel;

    @FXML
    public Button submitWinnerButton;

    @FXML
    public ProgressIndicator progressIndicator;

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
        itemDescription = itemDescriptionTextField.getText();
        bidderNumber = bidderNumberTextField.getText();
        bidAmount = bidAmountTextField.getText();
    }

    private void getItemInformation(String id, BidFXMLController controller) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                progressIndicator.setVisible(true);
                Item item = new Item();
                item.getItem(id, controller);
                executor.awaitTermination(2, TimeUnit.SECONDS);
                executor.shutdown();
            } catch (InterruptedException ex) {
                System.err.println("Process Interrupted: " + ex.getMessage());
            } finally {
                if (!executor.isShutdown()) {
                    executor.shutdownNow();
                    System.err.println("Forced shutdown");
                }
                progressIndicator.setVisible(false);
                bidderNumberTextField.setDisable(false);
                bidAmountTextField.setDisable(false);
                submitWinnerButton.setDisable(false);
                bidderNumberTextField.requestFocus();
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
        // Hide the progress indicator
        progressIndicator.setVisible(false);
        progressIndicator.toFront();

        // Disable everything except the item number textfield 
        itemNameTextField.setDisable(true);
        itemDescriptionTextField.setDisable(true);
        bidderNumberTextField.setDisable(true);
        bidAmountTextField.setDisable(true);
        submitWinnerButton.setDisable(true);

        itemNumberTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                itemNameTextField.setDisable(false);
                itemDescriptionTextField.setDisable(false);
                itemNameTextField.setEditable(false);
                itemDescriptionTextField.setEditable(false);
                bidderNumberTextField.setDisable(true);
                bidAmountTextField.setDisable(true);
                submitWinnerButton.setDisable(true);
            } else {
                itemNameTextField.setDisable(true);
                itemDescriptionTextField.setDisable(true);
                itemNameTextField.clear();
                itemDescriptionTextField.clear();
                bidderNumberTextField.clear();
                bidAmountTextField.clear();
            }
        });

        itemNumberTextField.setOnKeyReleased((event) -> {
            if ((event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) && itemNumberTextField.getText() != null && !itemNumberTextField.getText().trim().isEmpty()) {
                getItemInformation(itemNumberTextField.getText(), this);
            }
        });

        // TODO
    }

    @FXML
    protected void itemSearch(ActionEvent event) {
        System.out.println("Get item information");
        getItemInformation(itemNumberTextField.getText(), this);
    }

    public void loadBid(String bidId) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
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
                    itemDescriptionTextField.setText(bidData.get("ITEM_DESCRIPTION"));
                    bidderNumberTextField.setText(bidData.get("BIDDER_ID"));
                    bidAmountTextField.setText(bidData.get("BID_AMOUNT"));
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                });
            }
            executor.shutdown();
        });
    }

    @FXML
    protected void submitBid(ActionEvent event) throws ParseException {
        if (validateFields()) {
            Bid bid = new Bid();
            bid.submitBid(this);
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

}
