/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.menus;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.bidder.BidderListMain;
import com.cbmwebdevelopment.bidder.BidderMain;
import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.output.BidderList;
import com.cbmwebdevelopment.output.ItemList;
import com.cbmwebdevelopment.output.WinningBids;
import com.cbmwebdevelopment.user.AllUsersMain;
import com.cbmwebdevelopment.user.UserInformationMain;
import java.util.HashMap;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class CustomMenuBar {

    public MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();

        // Create the primary menus
        Menu menuFile = new Menu("File");
        Menu menuData = new Menu("Data");
        Menu menuUsers = new Menu("Users");

        // Create the sub menus
        Menu subMenuNew = new Menu("New");
        Menu subMenuExport = new Menu("Export");

        // Create the menu items
        // File Menu Items
        MenuItem newItem = new MenuItem("Item");
        MenuItem newBidder = new MenuItem("Bidder");
        MenuItem signOut = new MenuItem("Sign Out");
        MenuItem close = new MenuItem("Close");

        // Data Menu Items
        MenuItem viewBidders = new MenuItem("View Bidders");
        MenuItem viewItems = new MenuItem("View Items");
        MenuItem bidderList = new MenuItem("Bidder List");
        MenuItem itemList = new MenuItem("Auction Items");
        MenuItem winnerList = new MenuItem("Winners");

        // Users Menu Items
        MenuItem newUser = new MenuItem("New");
        MenuItem editUser = new MenuItem("Edit");
        MenuItem viewUsers = new MenuItem("View All");

        // Set the item actions
        setActions(newItem, newBidder, signOut, close, viewBidders, viewItems, bidderList, itemList, winnerList, newUser, editUser,
                viewUsers);

        // Add the sub menus
        subMenuNew.getItems().setAll(newItem, new SeparatorMenuItem(), newBidder);
        subMenuExport.getItems().setAll(bidderList, itemList, winnerList);

        // Add the items to the menus
        menuFile.getItems().addAll(subMenuNew, new SeparatorMenuItem(), signOut, new SeparatorMenuItem(), close);
        menuData.getItems().addAll(viewBidders, viewItems, subMenuExport);
        menuUsers.getItems().addAll(viewUsers, new SeparatorMenuItem(), newUser, editUser);

        // Add the menus
        menuBar.getMenus().addAll(menuFile, menuData, menuUsers);

        return menuBar;
    }

    /**
     * Menu item order - newItem, newBidder, signOut, close, viewBidders,
     * viewItems, bidderList, itemList, winnerList, newUser, editUser, viewUsers
     *
     * @param args
     */
    private void setActions(MenuItem... args) {

        // Create a new item
        args[0].setOnAction((event) -> {
          /*  ItemMain itemMain = new ItemMain();
            itemMain.isNew = true;
            try {
                itemMain.start(new Stage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }*/
        });

        // Create a new bidder
        args[1].setOnAction((event) -> {
            BidderMain bidder = new BidderMain();
            bidder.isNew = true;
            try {
                bidder.start(new Stage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });

        args[2].setOnAction((event) -> {
            Values.IS_SIGNED_IN = false;
        });

        // Close the application
        args[3].setOnAction((event) -> {
            Platform.exit();
        });

        // View the bidders in the application
        args[4].setOnAction((event) -> {
            System.out.println("View Bidders");
            BidderListMain bidderListMain = new BidderListMain();
            try {
                bidderListMain.start(new Stage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        // View the items in the application
        args[5].setOnAction((event) -> {
//            ViewItemsMain main = new ViewItemsMain();
//            main.start(new Stage());
        });

        // Export a list of the bidders
        args[6].setOnAction((event) -> {
            ObservableList<String> auctionNames = FXCollections.observableArrayList();
            ObservableList<String> auctionIds = FXCollections.observableArrayList();
            HashMap<String, String> auctions = new Auction().getAuctionNames();
            auctions.forEach((key, value) -> {
                auctionIds.add(key);
                auctionNames.add(value);
            });

            Dialog<Integer> dialog = new Alerts().comboBoxAlert("Winning Bidders", "Export Winning Bidders", "Select an auction", auctionNames);
            Optional<Integer> selectedAuction = dialog.showAndWait();
            if (selectedAuction.isPresent()) {
                String selectedAuctionId = auctionIds.get(selectedAuction.get());
                BidderList bidderList = new BidderList(selectedAuctionId);
                bidderList.exportBiddersToExcel();
            }
        });

        // Export a list of the items
        args[7].setOnAction((event) -> {
            ObservableList<String> auctionNames = FXCollections.observableArrayList();
            ObservableList<String> auctionIds = FXCollections.observableArrayList();
            HashMap<String, String> auctions = new Auction().getAuctionNames();
            auctions.forEach((key, value) -> {
                auctionIds.add(key);
                auctionNames.add(value);
            });
            Dialog<Integer> dialog = new Alerts().comboBoxAlert("Winning Bidders", "Export Winning Bidders", "Select an auction", auctionNames);
            Optional<Integer> selectedAuction = dialog.showAndWait();
            if (selectedAuction.isPresent()) {
                String selectedAuctionId = auctionIds.get(selectedAuction.get());
                ItemList itemList = new ItemList(selectedAuctionId);
                itemList.exportWinningBidsToExcel();
            }
        });

        // Export a list of the winners
        args[8].setOnAction((event) -> {
            ObservableList<String> auctionNames = FXCollections.observableArrayList();
            ObservableList<String> auctionIds = FXCollections.observableArrayList();
            HashMap<String, String> auctions = new Auction().getAuctionNames();
            auctions.forEach((key, value) -> {
                auctionIds.add(key);
                auctionNames.add(value);
            });
            Dialog<Integer> dialog = new Alerts().comboBoxAlert("Winning Bidders", "Export Winning Bidders", "Select an auction", auctionNames);
            Optional<Integer> selectedAuction = dialog.showAndWait();
            if (selectedAuction.isPresent()) {
                String selectedAuctionId = auctionIds.get(selectedAuction.get());
                WinningBids winningBids = new WinningBids();
                winningBids.exportWinningBidsToExcel(selectedAuctionId);
            }
        });

        // Create a new user
        args[9].setOnAction((event) -> {
            UserInformationMain uMain = new UserInformationMain();
            uMain.newUser = true;
            try {
                uMain.start(new Stage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }

        });

        // Edit an existing user
        args[10].setOnAction((event) -> {
            AllUsersMain allUsersMain = new AllUsersMain();
            try {
                allUsersMain.start(new Stage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        args[11].setOnAction((event) -> {
            AllUsersMain allUsersMain = new AllUsersMain();
            try {
                allUsersMain.start(new Stage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}
