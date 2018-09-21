/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import static com.cbmwebdevelopment.main.Values.AUCTION_FILTERS;
import static com.cbmwebdevelopment.main.Values.AUCTION_TYPES;
import static com.cbmwebdevelopment.main.Values.COUNTRIES;
import static com.cbmwebdevelopment.main.Values.STATES;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController.Attendees;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController.AuctionItem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController.Auctions;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class AuctionController implements Initializable {

    @FXML
    PrefixSelectionComboBox<String> filterAuctionTableComboBox, auctionTypeComboBox, stateComboBox, countryComboBox;

    @FXML
    TableView<Auctions> auctionTableView;

    @FXML
    public JFXTabPane auctionTabPane;

    @FXML
    TableView<AuctionItem> auctionItemsTableView;

    @FXML
    TableView<Attendees> auctionAttendeeTableView;

    @FXML
    TextField auctionIdTextField, auctionTitleTextField, buildingNameTextField, primaryAddressTextField, secondaryAddressTextField,
            cityTextField, postalCodeTextField;

    @FXML
    TextArea auctionDescriptionTextArea;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    Button backButton;

    @FXML
    DatePicker auctionDatePicker;

    @FXML
    AnchorPane auctionsPane;

    @FXML
    ToolBar toolBar;

    @FXML
    CheckBox publicCheckBox;

    private AuctionFXMLController auctionController;
    private AuctionItemsTableViewController auctionItemsTableViewController;
    private AttendeesTableController attendeesTableController;
    private AuctionsTableViewController auctionsTableController;
    private ArrayList<String> missingItems;
    public ScrollPane scrollPane;
    public StackPane stackPane;
    public static Integer auctionId;

    @FXML
    protected void addAttendeeAction(ActionEvent event) {
        AttendeesDialog attendeesDialog = new AttendeesDialog();
        JFXDialog dialog = attendeesDialog.attendeesDialog(auctionIdTextField.getText(), stackPane);
        
        dialog.setOnDialogClosed(evt -> {
            refreshBidderTable(auctionIdTextField.getText());
        });
        
        dialog.show();
    }

    /**
     * Save the changes made
     *
     * @param event
     */
    @FXML
    protected void saveChangesAction(ActionEvent event) {
        if (validateValues()) {
            saveAuction();
        } else {
            // Need to add some validation here
            System.out.println("Not valid");
        }
    }

    @FXML
    protected void addAuctionItemAction(ActionEvent even) throws IOException {
        AuctionItemsDialog auctionItemsDialog = new AuctionItemsDialog();
        JFXDialog dialog = auctionItemsDialog.auctionItemsDialog(auctionIdTextField.getText(), stackPane);
        
        dialog.setOnDialogClosed(evt -> {
            refreshItemsTable(auctionIdTextField.getText());
        });
        
        dialog.show();
    }

    @FXML
    protected void addNewAuction(ActionEvent event) {
        auctionTabPane.getTabs().get(1).setDisable(false);
        auctionTabPane.getSelectionModel().select(1);
    }

    /**
     *
     * @param auctionId
     */
    public void getAuction(String auctionId) {
        showProgressIndicator();
        // Use and executor to get the auction information
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            HashMap<String, String> auction = new Auction().getAuction(auctionId);

            // Get the auction items and attendees
            ObservableList<AuctionItem> data = new Auction().getAuctionItems(auction.get("ID"));
            ObservableList<Attendees> attendeesList = new Auction().getAttendees(auctionId);

            Platform.runLater(() -> {
                auctionTabPane.getSelectionModel().select(1);

                auctionTabPane.getTabs().forEach(tab -> {
                    tab.setDisable(false);
                });
                AuctionController.auctionId = Integer.parseInt(auction.get("ID"));
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
                publicCheckBox.setSelected(Boolean.parseBoolean(auction.get("PUBLIC")));
                auctionTypeComboBox.getSelectionModel().select(capitalizeFirstLetter(auction.get("TYPE")));
                auctionDatePicker.setValue(LocalDate.parse(auction.get("AUCTION_DATE")));
                auctionItemsTableView.getItems().setAll(data);
                auctionAttendeeTableView.getItems().setAll(attendeesList);
                hideProgressIndicator();
            });
            executor.shutdown();
        });
    }

    /**
     * Validate the required fields before saving
     *
     * @return
     */
    private boolean validateValues() {
        missingItems = new ArrayList<>();

        if (auctionTitleTextField.getText().trim().isEmpty()) {
            missingItems.add("Auction Title");
        }

        if (auctionTypeComboBox.getSelectionModel().getSelectedItem() == null || auctionTypeComboBox.getSelectionModel().isEmpty()) {
            missingItems.add("Auction Type");
        }

        if (auctionDatePicker.getValue() == null) {
            missingItems.add("Auction Date");
        }

        return missingItems.isEmpty();
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

    /*
    * Shows the progress indicator
     */
    protected void showProgressIndicator() {
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setVisible(true);
    }

    /*
    * Hides the progress indicator
     */
    protected void hideProgressIndicator() {
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0);
    }

    /**
     * Refresh the bidder table for the individual auction
     *
     * @param auctionId
     */
    public void refreshBidderTable(String auctionId) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<Attendees> attendeesList = new Auction().getAttendees(auctionId);
            Platform.runLater(() -> {
                auctionAttendeeTableView.getItems().setAll(attendeesList);
            });
            executor.shutdown();
        });
    }

    /**
     * Refresh the items table for the individual auction
     *
     * @param auctionId
     */
    public void refreshItemsTable(String auctionId) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<AuctionItem> auctionItems = new Auction().getAuctionItems(auctionId);
            Platform.runLater(() -> {
                auctionItemsTableView.getItems().setAll(auctionItems);
            });
            executor.shutdown();
        });
    }

    /**
     * Handle saving the auction
     */
    private void saveAuction() {
        showProgressIndicator();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            if (auctionIdTextField.getText() == null || auctionIdTextField.getText().trim().isEmpty()) {

                Auction auction = new Auction();
                String id = auction.saveNewAuction(auctionTitleTextField.getText(), auctionDescriptionTextArea.getText(), primaryAddressTextField.getText(), secondaryAddressTextField.getText(), cityTextField.getText(), stateComboBox.getSelectionModel().getSelectedItem(), postalCodeTextField.getText(), countryComboBox.getSelectionModel().getSelectedItem(), buildingNameTextField.getText(), publicCheckBox.isSelected(), auctionTypeComboBox.getSelectionModel().getSelectedItem(), auctionDatePicker.getValue().toString());
                if (id != null) {
                    Platform.runLater(() -> {
                        auctionTabPane.getTabs().forEach(tab -> {
                            if (tab.isDisable()) {
                                tab.setDisable(false);
                            }
                        });
                        Notifications.create().text("Auction ID: " + id + "\nStatus: Created").showInformation();
                        auctionIdTextField.setText(id);
                        hideProgressIndicator();
                    });
                }
            } else {

                Auction auction = new Auction();
                boolean updated = auction.saveExistingAuction(auctionIdTextField.getText(), auctionTitleTextField.getText(), auctionDescriptionTextArea.getText(), primaryAddressTextField.getText(), secondaryAddressTextField.getText(), cityTextField.getText(), stateComboBox.getSelectionModel().getSelectedItem(), postalCodeTextField.getText(), countryComboBox.getSelectionModel().getSelectedItem(), buildingNameTextField.getText(), publicCheckBox.isSelected(), auctionTypeComboBox.getSelectionModel().getSelectedItem(), auctionDatePicker.getValue().toString());
                System.out.println(updated);
                if (updated) {
                    Platform.runLater(() -> {

                        Notifications.create().text("Auction ID: " + auctionIdTextField.getText() + "\nStatus: Updated").showInformation();
                        hideProgressIndicator();
                    });
                }
            }
            executor.shutdown();
        });
    }

    private void getAuctions(String filterBy) {
        showProgressIndicator();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<Auctions> data = new Auction().getAuctions(filterBy);
            Platform.runLater(() -> {
                auctionTableView.setItems(data);
                hideProgressIndicator();

            });
            executor.shutdown();
        });
    }

    private void setTableControllers() {
        // Table controllers
        auctionItemsTableViewController = new AuctionItemsTableViewController();
        attendeesTableController = new AttendeesTableController();
        auctionsTableController = new AuctionsTableViewController();

        auctionItemsTableViewController.tableController(auctionItemsTableView);
        attendeesTableController.tableController(auctionAttendeeTableView);
        auctionsTableController.tableView(auctionTableView);

        // Set the table controller
        getAuctions("-");
    }

    private void initViews() {
        hideProgressIndicator();

        // Set the filter by combo box
        filterAuctionTableComboBox.setItems(AUCTION_FILTERS);
        filterAuctionTableComboBox.getSelectionModel().select(0);

        // Assign combo box values
        auctionTypeComboBox.getItems().setAll(AUCTION_TYPES);
        auctionTypeComboBox.getSelectionModel().select(0);
        stateComboBox.getItems().setAll(STATES);
        countryComboBox.getItems().setAll(COUNTRIES);
        countryComboBox.getSelectionModel().select("United States of America");

        // Date and time picker default values to current date and time
        auctionDatePicker.setValue(LocalDate.now());

        setTableControllers();

    }

    private void setListeners() {
        filterAuctionTableComboBox.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null && (!nv.isEmpty())) {
                getAuctions(nv);
            }
        });

        // click listener on table controller
        auctionTableView.setRowFactory(tv -> {
            TableRow<Auctions> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Auctions auctions = row.getItem();

                    String id = String.valueOf(auctions.getId());
                    getAuction(id);
                    auctionTabPane.getSelectionModel().select(1);
                }
            });
            return row;
        });

        // Country combo box listener. Will disable state box if value is chosen 
        // other than United States of America. This is also the default
        // value. 
        countryComboBox.selectionModelProperty().addListener((obs, ov, nv) -> {
            if (!nv.equals("United States of America")) {
                stateComboBox.getSelectionModel().select(-1);
                stateComboBox.setDisable(true);
            } else {
                stateComboBox.setDisable(false);
                stateComboBox.getSelectionModel().select(0);
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
