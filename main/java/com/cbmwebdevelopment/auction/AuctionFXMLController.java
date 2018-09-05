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
import com.cbmwebdevelopment.items.ViewItemsFXMLController;
//import com.cbmwebdevelopment.items.ViewItemsMain;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController.Attendees;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController.AuctionItem;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
    public ToolBar toolBar;
   // public StackPane stackPane = AuctionDashboardFXMLController.stackPane;
    public Button saveChangesButton;
    public ProgressIndicator progressIndicator;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewItemsFXML.fxml"));
            AnchorPane itemsPane =  loader.load();
            ViewItemsFXMLController viewItemsController = (ViewItemsFXMLController) loader.getController();
           // viewItemsController.auctionController = this;
            
            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        //ViewItemsMain viewItemsMain = new ViewItemsMain();
       // viewItemsMain.addItemsToAuction = true;
       // viewItemsMain.auctionController = this;
       // viewItemsMain.start(new Stage());
    }

    @FXML
    protected void saveChangesAction(ActionEvent event) {
        if (validateValues()) {
            saveAuction();
        } else {
            // Need to add some validation here
        }
    }

    private void assignValues() {
        id = auctionIdTextField.getText().trim().isEmpty() ? null : auctionIdTextField.getText();
        title = auctionTitleTextField.getText();
//        description = auctionDescriptionTextArea.getText();
        type = auctionTypeComboBox.getSelectionModel().getSelectedItem();
        dateTime = auctionDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        buildingName = buildingNameTextField.getText();
        primaryAddress = primaryAddressTextField.getText();
        secondaryAddress = secondaryAddressTextField.getText();
        city = cityTextField.getText();
        state = stateComboBox.getSelectionModel().getSelectedIndex() < 0 ? null : stateComboBox.getSelectionModel().getSelectedItem();
        postalCode = postalCodeTextField.getText();
        country = countryComboBox.getSelectionModel().getSelectedItem();
        isPublic = publicCheckBox.isSelected();
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
                AuctionFXMLController.auctionId = Integer.parseInt(auction.get("ID"));
                id = auction.get("ID");
                auctionIdTextField.setText(auction.get("ID"));
                auctionTitleTextField.setText(auction.get("NAME"));
                //auctionDescriptionTextArea.setText(auction.get("DESCRIPTION"));
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
     * Use threading to either save a new auction or update and existing
     * auction.
     */
    private void saveAuction() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            if (id == null) {
//                id = new Auction().saveNewAuction(this);
                isNew = false;
                if (id != null) {
                    Platform.runLater(() -> {
                        Notifications.create().text("Auction ID: " + auctionId + "\nStatus: Created").showInformation();
                        auctionIdTextField.setText(id);
                    });
                }
            } else {
            //    boolean updated = new Auction().saveExistingAuction(this);å
              //  if (updated) {
                //    Platform.runLater(() -> {
                  //      Notifications.create().text("Auction ID: " + auctionId + "\nStatus: Updated").showInformation();
                    //});
              // }
            }
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
        assignValues();

        if (title == null || title.trim().isEmpty()) {
            missingItems.add("Auction Title");
        }

        return missingItems.isEmpty();
    }

    /**
     * Assign the table controllers
     */
    private void setTableControllers() {
        // Table controllers
        auctionItemsTableViewController = new AuctionItemsTableViewController();
        attendeesTableController = new AttendeesTableController();

        auctionItemsTableViewController.tableController(auctionItemsTableView);
        attendeesTableController.tableController(auctionAttendeeTableView);
    }

    /**
     * Set values for combo boxes and other prefilled objects.
     */
    private void setValues() {
        // Assign combo box values
        auctionTypeComboBox.getItems().setAll(AUCTION_TYPES);
        auctionTypeComboBox.getSelectionModel().select(0);
        stateComboBox.getItems().setAll(STATES);
        countryComboBox.getItems().setAll(COUNTRIES);
        countryComboBox.getSelectionModel().select("United States of America");

        // Date and time picker default values to current date and time
        auctionDatePicker.setValue(LocalDate.now());
    }

    /**
     * Set action listeners
     */
    private void setListeners() {
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

    private void addMenuItems() {
        saveChangesButton = new Button("Save Changes");
        saveChangesButton.setOnAction(evt -> {

        });
        Separator verticalSeparator = new Separator();
        verticalSeparator.setOrientation(Orientation.VERTICAL);
        verticalSeparator.setScaleY(6.0);

        //toolBar.getItems().add(saveChangesButton);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        setValues();
        setListeners();
        setTableControllers();
        addMenuItems();
    }

}
