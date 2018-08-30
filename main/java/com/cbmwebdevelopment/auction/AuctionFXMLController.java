/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import static com.cbmwebdevelopment.main.Values.AUCTION_TYPES;
import static com.cbmwebdevelopment.main.Values.COUNTRIES;
import static com.cbmwebdevelopment.main.Values.STATES;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.bidder.BidderListMain;
import com.cbmwebdevelopment.items.ViewItemsMain;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController.Attendees;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController.AuctionItem;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class AuctionFXMLController implements Initializable {

    public static int auctionId;

    @FXML
    TextField auctionIdTextField, auctionTitleTextField, buildingNameTextField, primaryAddressTextField, secondaryAddressTextField,
            cityTextField, postalCodeTextField;

    @FXML
    TextArea auctionDescriptionTextArea;

    @FXML
    PrefixSelectionComboBox<String> auctionTypeComboBox, stateComboBox, countryComboBox;

    @FXML
    CheckBox publicCheckBox;

    @FXML
    DatePicker auctionDatePicker;

    @FXML
    TableView<AuctionItem> auctionItemsTableView;

    @FXML
    TableView<Attendees> auctionAttendeeTableView;

    @FXML
    ProgressIndicator progressIndicator;

    protected String id, title, description, type, dateTime, buildingName, primaryAddress, secondaryAddress, city, postalCode, state,
            country;
    protected boolean isPublic;
    private ArrayList<String> missingItems;
    public boolean isNew;
    private AuctionItemsTableViewController auctionItemsTableViewController;
    private AttendeesTableController attendeesTableController;

    @FXML
    protected void addAttendeeAction(ActionEvent event) throws IOException {
        BidderListMain bidderListMain = new BidderListMain();
        bidderListMain.addAuctionBidder = true;
        bidderListMain.auctionId = String.valueOf(auctionId);
        bidderListMain.auctionController = this;
        bidderListMain.start(new Stage());
    }

    @FXML
    protected void addAuctionItemAction(ActionEvent even) throws IOException {
        System.out.println(auctionId);
        ViewItemsMain viewItemsMain = new ViewItemsMain();
        viewItemsMain.addItemsToAuction = true;
        viewItemsMain.auctionController = this;
        viewItemsMain.start(new Stage());
    }

    private void assignValues() {
        this.id = auctionIdTextField.getText().trim().isEmpty() ? null : auctionIdTextField.getText();
        this.title = auctionTitleTextField.getText();
        this.description = auctionDescriptionTextArea.getText();
        this.type = auctionTypeComboBox.getSelectionModel().getSelectedItem();
        this.dateTime = auctionDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.buildingName = buildingNameTextField.getText();
        this.primaryAddress = primaryAddressTextField.getText();
        this.secondaryAddress = secondaryAddressTextField.getText();
        this.city = cityTextField.getText();
        this.state = stateComboBox.getSelectionModel().getSelectedIndex() < 0 ? null
                : stateComboBox.getSelectionModel().getSelectedItem().toString();
        this.postalCode = postalCodeTextField.getText();
        this.country = countryComboBox.getSelectionModel().getSelectedItem().toString();
        this.isPublic = publicCheckBox.isSelected();
    }

    /**
     * Returns a string with the first letter being capitalized. Ex.: live ->
     * Live
     *
     * @param val
     * @return
     */
    private String capitalizeFirstLetter(String val) {
        StringBuilder sb = new StringBuilder();
        sb.append(val.substring(0, 1).toUpperCase());
        sb.append(val.substring(1).toLowerCase());
        return sb.toString();
    }

    @FXML
    protected void createNewAuctionAction(ActionEvent event) throws IOException {
       
    }

    public void getAuction(String auctionId) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        // Use and executor to get the auction information
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            HashMap<String, String> auction = new Auction().getAuction(auctionId);

            // Get the auction items and attendees
            ObservableList<AuctionItem> data = new Auction().getAuctionItems(auction.get("ID"));
            ObservableList<Attendees> attendeesList = new Auction().getAttendees(auctionId);

            Platform.runLater(() -> {
                AuctionFXMLController.auctionId = Integer.parseInt(auction.get("ID"));
                this.id = auction.get("ID");
                auctionIdTextField.setText(auction.get("ID"));
                auctionTitleTextField.setText(auction.get("NAME"));
                auctionDescriptionTextArea.setText(auction.get("DESCRIPTION"));
                primaryAddressTextField.setText(auction.get("PRIMARY_ADDRESS"));
                secondaryAddressTextField.setText(auction.get("SECONDARY_ADDRESS"));
                cityTextField.setText(auction.get("CITY"));
                stateComboBox.getSelectionModel().select(auction.get("STATE"));
                postalCodeTextField.setText(auction.get("POSTAL_CODE"));
                countryComboBox.getSelectionModel().select(auction.get("COUNTRY"));
                buildingNameTextField.setText(auction.get("BUILDING_NAME"));
                publicCheckBox.setSelected(Boolean.parseBoolean(auction.get("PUBLIC").toString()));
                auctionTypeComboBox.getSelectionModel().select(capitalizeFirstLetter(auction.get("TYPE")));
                auctionDatePicker.setValue(LocalDate.parse(auction.get("AUCTION_DATE")));
                auctionItemsTableView.getItems().setAll(data);
                auctionAttendeeTableView.getItems().setAll(attendeesList);
                progressIndicator.setVisible(false);
                progressIndicator.setProgress(0.0);
            });
            executor.shutdown();
        });
    }

    /**
     * Initializes the controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {
        // Hide the progress indicator on load
        progressIndicator.setVisible(false);

        // Assign combo box values
        auctionTypeComboBox.getItems().setAll(AUCTION_TYPES);
        auctionTypeComboBox.getSelectionModel().select(0);
        stateComboBox.getItems().setAll(STATES);
        countryComboBox.getItems().setAll(COUNTRIES);
        countryComboBox.getSelectionModel().select("United States of America");
        countryComboBox.selectionModelProperty().addListener((obs, ov, nv) -> {
            if (!nv.equals("United States of America")) {
                stateComboBox.getSelectionModel().select(-1);
                stateComboBox.setDisable(true);
            } else {
                stateComboBox.setDisable(false);
                stateComboBox.getSelectionModel().select(0);
            }
        });

        // Date and time picker default values to current date and time
        auctionDatePicker.setValue(LocalDate.now());

        // Table controllers
        auctionItemsTableViewController = new AuctionItemsTableViewController();
        attendeesTableController = new AttendeesTableController();

        auctionItemsTableViewController.tableController(auctionItemsTableView);
        attendeesTableController.tableController(auctionAttendeeTableView);
    }

    public void refreshBidderTable(String auctionId) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<Attendees> attendeesList = new Auction().getAttendees(auctionId);
            Platform.runLater(() -> {
                auctionAttendeeTableView.getItems().setAll(attendeesList);
                progressIndicator.setVisible(false);
                progressIndicator.setProgress(0.0);
            });
            executor.shutdown();
        });
    }

    public void refreshItemsTable(String auctionId) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<AuctionItem> auctionItems = new Auction().getAuctionItems(auctionId);
            Platform.runLater(() -> {
                auctionItemsTableView.getItems().setAll(auctionItems);
                progressIndicator.setVisible(false);
                progressIndicator.setProgress(0.0);
            });
            executor.shutdown();
        });
    }

    /**
     * Use threading to either save a new auction or update and existing
     * auction.
     */
    private void saveAuction() {
        progressIndicator.setVisible(true);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            if (id == null) {
                id = new Auction().saveNewAuction(this);
                isNew = false;
                if (id != null) {
                    Platform.runLater(() -> {
                        Notifications.create().text("Auction ID: " + auctionId + "\nStatus: Created").showInformation();
                        auctionIdTextField.setText(id);
                        progressIndicator.setProgress(0);
                        progressIndicator.setVisible(false);
                    });
                }
            } else {
                boolean updated = new Auction().saveExistingAuction(this);
                if (updated) {
                    Platform.runLater(() -> {
                        Notifications.create().text("Auction ID: " + auctionId + "\nStatus: Updated").showInformation();
                        progressIndicator.setVisible(false);
                        progressIndicator.setProgress(0);
                    });
                }
            }
            executor.shutdown();
        });
    }

    @FXML
    protected void saveAuctionAction(ActionEvent event) {
        if (validateValues()) {
            saveAuction();
        } else {

        }
    }

    private boolean validateValues() {
        missingItems = new ArrayList<>();
        assignValues();

        if (title == null || title.trim().isEmpty()) {
            missingItems.add("Auction Title");
        }

        return missingItems.isEmpty();
    }

}
