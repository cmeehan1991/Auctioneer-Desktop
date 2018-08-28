/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import static com.cbmwebdevelopment.main.MainApp.ERROR_LABEL;


import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.checkout.CheckoutMain;
import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.tablecontrollers.BidderAuctionHistoryTableController;
import com.cbmwebdevelopment.tablecontrollers.BidderAuctionHistoryTableController.BidderAuctionHistory;
import com.cbmwebdevelopment.tablecontrollers.BidderAuctionsTableController;
import com.cbmwebdevelopment.tablecontrollers.BidderAuctionsTableController.BidderAuctions;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class BidderFXMLController implements Initializable {

	@FXML
	TextField bidderIdTextField, firstNameTextField, lastNameTextField, streetAddressTextField, suiteTextField, cityTextField,
			postalCodeTextField, phoneNumberTextField, emailAddressTextField;

	@FXML
	PrefixSelectionComboBox<String> bidderPrefixComboBox, suffixComboBox, stateComboBox;

	@FXML
	TableView<BidderAuctions> bidderAuctionsTableView;
	
	@FXML
	TableView<BidderAuctionHistory> bidderAuctionHistoryTableView;

	@FXML
	Label firstNameLabel, lastNameLabel, streetAddressLabel, cityLabel, postalCodeLabel, phoneNumberLabel, emailAddressLabel;

	@FXML
	ProgressIndicator progressIndicator;
	
	@FXML
	TabPane userInformationTabPane;

	private BidderAuctionsTableController bidderAuctionsTableController;
	private BidderAuctionHistoryTableController bidderAuctionHistoryTableController;
	private ArrayList<String> missingItems;
	protected String id, prefix, firstName, lastName, suffix, streetAddress, suite, city, state, postalCode, phone, email;

	/**
	 * Assigns values to each variable from the input fields
	 */
	private void assignValues() {
		id = bidderIdTextField.getText();
		prefix = bidderPrefixComboBox.getSelectionModel().getSelectedItem().toString();
		firstName = firstNameTextField.getText();
		lastName = lastNameTextField.getText();
		suffix = suffixComboBox.getSelectionModel().getSelectedItem().toString();
		streetAddress = streetAddressTextField.getText();
		suite = suiteTextField.getText();
		city = cityTextField.getText();
		state = stateComboBox.getSelectionModel().getSelectedItem().toString();
		postalCode = postalCodeTextField.getText();
		phone = phoneNumberTextField.getText();
		email = emailAddressTextField.getText();
	}

	@FXML
	protected void checkOut(ActionEvent event) throws IOException {
		CheckoutMain checkoutMain = new CheckoutMain();
		checkoutMain.start(new Stage());
	}

	/**
	 * Get the bidder information
	 * 
	 * @param id
	 */
	public void getBidder(String id) {
		userInformationTabPane.setDisable(true);
		progressIndicator.setVisible(true);
		progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(() -> {
			Bidder bidder = new Bidder();
			HashMap<String, String> bidderInfo = bidder.getUser(id);
			ObservableList<BidderAuctions> bidderAuctions = bidder.getBidderAuctions(id);
			ObservableList<BidderAuctionHistory> bidderAuctionHistory = bidder.getBidderAuctionHistory(id);
			Platform.runLater(() -> {
				if (!bidderInfo.isEmpty()) {
					bidderIdTextField.setText(id);
					bidderPrefixComboBox.getSelectionModel().select(bidderInfo.get("prefix"));
					firstNameTextField.setText(bidderInfo.get("firstName"));
					lastNameTextField.setText(bidderInfo.get("lastName"));
					suffixComboBox.getSelectionModel().select(bidderInfo.get("suffix"));
					phoneNumberTextField.setText(bidderInfo.get("telephone"));
					emailAddressTextField.setText(bidderInfo.get("email"));
					streetAddressTextField.setText(bidderInfo.get("primaryAddress"));
					suiteTextField.setText(bidderInfo.get("secondaryAddress"));
					cityTextField.setText(bidderInfo.get("city"));
					stateComboBox.getSelectionModel().select(bidderInfo.get("state"));
					postalCodeTextField.setText(bidderInfo.get("postalCode"));
					bidderAuctionsTableView.setItems(bidderAuctions);
					bidderAuctionHistoryTableView.setItems(bidderAuctionHistory);
				} else {
					Notifications.create().text("The bidder ID you entered does not match anything on record. Please try again.")
							.showInformation();
				}
				progressIndicator.setVisible(false);
				progressIndicator.setProgress(0.0);
				userInformationTabPane.setDisable(false);
			});
			executor.shutdown();
		});
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Hide the progress indicator by default
		progressIndicator.setVisible(false);
		progressIndicator.setProgress(0.0);

		// Initialize the table controllers
		bidderAuctionsTableController = new BidderAuctionsTableController();
		bidderAuctionHistoryTableController = new BidderAuctionHistoryTableController();
		bidderAuctionsTableController.tableView(bidderAuctionsTableView);
		bidderAuctionHistoryTableController.tableView(bidderAuctionHistoryTableView);

		// Set the combobox values
		bidderPrefixComboBox.getItems().setAll(Values.PREFIXES);
		bidderPrefixComboBox.getSelectionModel().select(0);

		suffixComboBox.getItems().setAll(Values.SUFFIXES);
		suffixComboBox.getSelectionModel().select(0);

		stateComboBox.getItems().setAll(Values.STATES);
		stateComboBox.getSelectionModel().select(0);

	}

	@FXML
	protected void saveBidder(ActionEvent event) {
		if (validateFields()) {
			Bidder bidder = new Bidder();
			HashMap<String, String> args = new HashMap<>();
			args.put("id", id);
			args.put("prefix", prefix);
			args.put("suffix", suffix);
			args.put("firstName", firstName);
			args.put("lastName", lastName);
			args.put("primaryAddress", streetAddress);
			args.put("secondaryAddress", suite);
			args.put("city", city);
			args.put("state", state);
			args.put("postalCode", postalCode);
			args.put("telephone", phone);
			args.put("email", email);
			if (id == null || id.trim().isEmpty()) {
				bidder.saveBidder(args, this);
			} else {
				bidder.updateBidder(args, this);
			}

		} else {
			new Alerts().errorAlert("Missing Items", "Several required items are missing", "Please correct the following missing items:",
					missingItems).showAndWait();
		}
	}

	/**
	 * Validates the required fields.
	 *
	 * @return boolean
	 */
	private boolean validateFields() {
		// Assign all of the input values to their corresponding string.
		assignValues();

		missingItems = new ArrayList<>();

		if (firstName == null || firstName.isEmpty()) {
			missingItems.add("First Name");
			firstNameLabel.setStyle(ERROR_LABEL);
		}

		if (lastName == null || lastName.isEmpty()) {
			missingItems.add("Last Name");
			lastNameLabel.setStyle(ERROR_LABEL);
		}

		if (streetAddress == null || streetAddress.isEmpty()) {
			missingItems.add("Street Address");
			streetAddressLabel.setStyle(ERROR_LABEL);
		}

		if (city == null || city.isEmpty()) {
			missingItems.add("City");
			cityLabel.setStyle(ERROR_LABEL);
		}

		if (postalCode == null || postalCode.isEmpty()) {
			missingItems.add("Postal Code");
			postalCodeLabel.setStyle(ERROR_LABEL);
		}

		return missingItems.isEmpty();
	}

}
