/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import com.cbmwebdevelopment.items.Item;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController.AllItems;
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
public class AuctionItemsDialog {

    private final ItemsTableController itemsController = new ItemsTableController();
    private ObservableList<AllItems> allItems = FXCollections.observableArrayList();
    private boolean added = false;
    private TableView<AllItems> allItemsTable;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Create a JFX Dialog that allows the user to add allItems to an auction.
     *
     * @param auctionId
     * @param stackPane
     * @return
     */
    public JFXDialog auctionItemsDialog(String auctionId, StackPane stackPane) {
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

        VBox content = new VBox();

        // Initalize the allItems tableview and
        allItemsTable = new TableView<>();
        // Set the table controller
        itemsController.itemsTable(allItemsTable);
        // Search for and return all allItems
        searchAllItems(null);

        // Create a confirmation button that will add the attendees to the auction. 
        JFXButton confirmButton = new JFXButton("Add Item(s)");
        confirmButton.setOnAction(evt -> {
            ObservableList<AllItems> selectedAllItems = allItemsTable.getSelectionModel().getSelectedItems();
            try {
                boolean close = addItem(auctionId, selectedAllItems).get();
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
        content.getChildren().addAll(searchField(), allItemsTable);

        // Set the dialog content
        dialogContent.setHeading(new Label("Add Item(s)"));
        dialogContent.setBody(content);
        dialogContent.setActions(confirmButton, closeButton);

        // Allow the dialog to be closed on overlay click
        dialog.setOverlayClose(true);

        return dialog;
    }

    /**
     * Display an error notification advising the user that the selected
     * Items(s) were not added to the auction.
     *
     * @param stackPane
     * @return
     */
    private JFXDialog errorNotification(StackPane stackPane) {

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);

        content.setBody(new Label("Some of the selected items were not added. Please check the auction's items and try again."));

        JFXButton closeButton = new JFXButton("OK");
        closeButton.setDefaultButton(true);
        closeButton.setOnAction(evt -> {
            dialog.close();
        });

        content.setActions(closeButton);

        return dialog;
    }

    /**
     * Add the selected allItems(s) to the auction as attendees.
     *
     * @param auctionId
     * @param selectedAllItems
     * @return
     */
    private Future<Boolean> addItem(String auctionId, ObservableList<AllItems> selectedAllItems) {
        return executor.submit(() -> {
            Auction auction = new Auction();
            selectedAllItems.forEach(allItems -> {
                String itemId = String.valueOf(allItems.getId());
                boolean attendeeAdded = auction.addItem(auctionId, itemId);

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
     * Search for the allItems concurrently based on string inputs.
     *
     * @param terms
     */
    private void searchAllItems(String terms) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Item items = new Item();
            allItems = items.getAuctionItems(terms, null);
            Platform.runLater(() -> {
                allItemsTable.getItems().setAll(allItems);
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
            searchAllItems(nv);
        });
        return textField;
    }
}
