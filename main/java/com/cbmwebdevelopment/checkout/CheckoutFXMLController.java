/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.checkout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.bidder.Bidder;
import com.cbmwebdevelopment.output.ItemReceipt;
import com.cbmwebdevelopment.tablecontrollers.CheckoutItemTableViewController;
import com.cbmwebdevelopment.tablecontrollers.CheckoutItemTableViewController.ItemData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class CheckoutFXMLController implements Initializable {

    @FXML
    PrefixSelectionComboBox<String> auctionListComboBox;

    @FXML
    public TextField bidderIdTextField, bidderNameTextField, totalItemsTextField, totalAmountTextField;

    @FXML
    public TextArea billingAddressTextArea;

    @FXML
    public TableView<ItemData> bidderItemsTableView;

    @FXML
    public ProgressIndicator progressIndicator;

    @FXML
    ToolBar actionToolbar;

    @FXML
    TabPane bidderCheckoutTabPane;

    private String auctionId, bidderId;
    public CheckoutItemTableViewController tableController;
    private HashMap<String, String> auctionNames;
    private ObservableList<String> auctionIds;

    @FXML
    protected void emailInvoiceAction(ActionEvent event) throws IOException, FileNotFoundException, ParseException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set recipient");
        dialog.setHeaderText("Add a single recipient or a semi-colon (;) separated list of recipients.");
        dialog.setContentText("Recipient(s):");
        dialog.getEditor().setText(new Bidder().getEmail(bidderIdTextField.getText()));
        Optional<String> result = dialog.showAndWait();

        if (result.get() != null && !result.get().trim().isEmpty()) {
            ItemReceipt itemReceipt = new ItemReceipt(bidderIdTextField.getText(), bidderNameTextField.getText(), totalItemsTextField.getText(), totalAmountTextField.getText(), billingAddressTextArea.getText(), bidderItemsTableView);
            itemReceipt.emailPDF(bidderIdTextField.getText(), result.get());
        }
    }

    private void getAuctions() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            auctionNames = new Auction().getAuctionNames();
            ObservableList<String> names = FXCollections.observableArrayList();
            auctionIds = FXCollections.observableArrayList();
            auctionNames.entrySet().forEach((entry) -> {
                names.add(entry.getValue());
                auctionIds.add(entry.getKey());
            });

            Platform.runLater(() -> {
                auctionListComboBox.getItems().setAll(names);
            });
            executor.shutdown();
        });
    }

    @FXML
    protected void getBidderAction(ActionEvent even) {
        getCheckoutInformation(bidderIdTextField.getText(), auctionId);
    }

    protected void getCheckoutInformation(String bidderId, String auctionId) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Checkout checkout = new Checkout();
            HashMap<String, String> bidderData = checkout.getUserInformation(bidderId, auctionId);
            ObservableList<ItemData> data = checkout.getItems(bidderId, auctionId);
            if (bidderData != null) {
                Platform.runLater(() -> {
                    actionToolbar.setDisable(false);
                    this.bidderId = bidderId;
                    bidderIdTextField.setText(bidderId);
                    bidderNameTextField.setText(bidderData.get("bidderName"));
                    billingAddressTextArea.setText(bidderData.get("billingAddress"));
                    totalAmountTextField.setText(bidderData.get("totalAmountDue"));
                    totalItemsTextField.setText(bidderData.get("totalItems"));;
                    bidderItemsTableView.setItems(data);
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                });
            } else {
                Platform.runLater(() -> {
                    actionToolbar.setDisable(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                    Notifications.create().title("Bidder does not exist").text("The bidder does not exits.").showWarning();
                });
            }
            executor.shutdown();
        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0.0);
        getAuctions();

        // Set value listener on auction combo box
        auctionListComboBox.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                auctionId = auctionIds.get(auctionListComboBox.getSelectionModel().getSelectedIndex());
                bidderCheckoutTabPane.setDisable(false);
            } else {
                auctionId = null;
                bidderCheckoutTabPane.setDisable(true);
            }
        });

        // Table controller
        tableController = new CheckoutItemTableViewController();
        tableController.tableView(bidderItemsTableView);

        // Set bidder ID textfield listeners 
        bidderIdTextField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.TAB) {
                getCheckoutInformation(bidderIdTextField.getText(), auctionId);
            }
        });
        bidderNameTextField.requestFocus();
    }

    @FXML
    protected void printReceipt(ActionEvent event) throws IOException {
        ItemReceipt itemReceipt = new ItemReceipt(bidderIdTextField.getText(), bidderNameTextField.getText(), totalItemsTextField.getText(), totalAmountTextField.getText(), billingAddressTextArea.getText(), bidderItemsTableView);
        itemReceipt.printReceipt(bidderIdTextField.getText());
    }

    @FXML
    protected void saveInvoiceAction(ActionEvent event) throws FileNotFoundException, IOException, ParseException {
        ItemReceipt itemReceipt = new ItemReceipt(bidderIdTextField.getText(), bidderNameTextField.getText(), totalItemsTextField.getText(), totalAmountTextField.getText(), billingAddressTextArea.getText(), bidderItemsTableView);
        itemReceipt.saveAsPDF();
    }

}
