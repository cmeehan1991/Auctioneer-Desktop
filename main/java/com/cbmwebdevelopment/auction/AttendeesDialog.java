/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import com.cbmwebdevelopment.bidder.Bidder;
import com.cbmwebdevelopment.tablecontrollers.BiddersTableController;
import com.cbmwebdevelopment.tablecontrollers.BiddersTableController.Bidders;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author cmeehan
 */
public class AttendeesDialog {

    private final BiddersTableController biddersTableController = new BiddersTableController();
    private ObservableList<Bidders> bidders = FXCollections.observableArrayList();
    private boolean added = false;
    private TableView<Bidders> biddersTable;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Create a JFX Dialog that allows the user to add bidders to an auction.
     *
     * @param auctionId
     * @param stackPane
     * @return
     */
    public JFXDialog attendeesDialog(String auctionId, StackPane stackPane) {
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

        VBox content = new VBox();

        // Initalize the bidders tableview and
        biddersTable = new TableView<>();
        // Set the table controller
        biddersTableController.biddersTable(biddersTable);
        // Search for and return all bidders
        searchBidders(null);

        // Create a confirmation button that will add the attendees to the auction. 
        JFXButton confirmButton = new JFXButton("Add Attendee(s)");
        confirmButton.setOnAction(evt -> {
            ObservableList<Bidders> selectedBidders = biddersTable.getSelectionModel().getSelectedItems();
            try {
                boolean close = addBidder(auctionId, selectedBidders).get();
                if (close) {
                    dialog.close();
                } else {
                    errorNotification(stackPane).show();
                }
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println(ex.getMessage());
            }
        });

        // Close button that will simply close the dialog without any changes. 
        JFXButton closeButton = new JFXButton("Close");
        closeButton.setDefaultButton(false);
        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(evt -> {
            dialog.close();
        });

        // Add the content to the VBox
        content.getChildren().addAll(searchField(), biddersTable);

        // Set the dialog content
        dialogContent.setHeading(new Label("Add Attendee(s)"));
        dialogContent.setBody(content);
        dialogContent.setActions(confirmButton, closeButton);

        // Allow the dialog to be closed on overlay click
        dialog.setOverlayClose(true);

        return dialog;
    }

    /**
     * Display an error notification advising the user that the selected
     * bidder(s) were not added to the auction.
     *
     * @param stackPane
     * @return
     */
    private JFXDialog errorNotification(StackPane stackPane) {

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);

        content.setBody(new Label("Some of the selected bidders were not added. Please check the auction's bidders and try again."));

        JFXButton closeButton = new JFXButton("OK");
        closeButton.setDefaultButton(true);
        closeButton.setOnAction(evt -> {
            dialog.close();
        });

        content.setActions(closeButton);

        return dialog;
    }

    /**
     * Add the selected bidder(s) to the auction as attendees.
     *
     * @param auctionId
     * @param selectedBidders
     * @return
     */
    private Future<Boolean> addBidder(String auctionId, ObservableList<Bidders> selectedBidders) {
        return executor.submit(() -> {
            Auction auction = new Auction();
            selectedBidders.forEach(bidder -> {
                int bidderId = bidder.getId();
                boolean attendeeAdded = auction.addAttendee(Integer.parseInt(auctionId), bidderId);

                if (!attendeeAdded) {
                    added = false;
                    executor.shutdownNow();
                }
            });
            added = true;
            executor.shutdown();
            return added;
        });
    }

    /**
     * Search for the bidders concurrently based on string inputs.
     *
     * @param terms
     */
    private void searchBidders(String terms) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Bidder bidder = new Bidder();
            bidders = bidder.getBidders(terms);
            Platform.runLater(() -> {
                biddersTable.getItems().setAll(bidders);
            });
            executor.shutdown();
        });
    }

    /**
     * Create the search textfield with a listener on the text property
     *
     * @return
     */
    private TextField searchField() {
        TextField textField = new TextField();

        textField.textProperty().addListener((obs, ov, nv) -> {
            searchBidders(nv);
        });
        return textField;
    }
}
