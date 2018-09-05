/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import static com.cbmwebdevelopment.main.Values.DATE_FORMAT;
import static com.cbmwebdevelopment.main.Values.ORGANIZATION_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;

import org.controlsfx.control.Notifications;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.items.ItemFXMLController;
import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.tablecontrollers.AttendeesTableController.Attendees;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController.AuctionItem;
import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController.Auctions;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author cmeehan
 */
public class Auction {

    /**
     * Add an attendee to the auction
     *
     * @param auctionId
     * @param bidderId
     * @return
     */
    public boolean addAttendee(int auctionId, int bidderId) {
        boolean added = false;
        String sql = "INSERT INTO AUCTION_BIDDERS (AUCTION_ID, BIDDER_ID) VALUES(?,?);";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, auctionId);
            ps.setInt(2, bidderId);
            added = ps.execute();
            conn.close();
        } catch (SQLException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding user to database." + "\n" + "Error Message: " + ex.getMessage());
            alert.showAndWait();
            System.err.println(ex.getMessage());
        }
        return added;
    }

    public boolean addItem(String auctionId, String itemId) {
        boolean added = false;
        String sql = "UPDATE AUCTION_ITEMS SET AUCTION_ID = ?, ORGANIZATION_ID = ? WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, auctionId);
            ps.setString(2, Values.ORGANIZATION_ID);
            ps.setString(3, itemId);
            int rs = ps.executeUpdate();
            if (rs >= 1) {
                added = true;
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return added;
    }

    /**
     * Check to see if the bidder already exists in the auction.
     *
     * @param auctionId
     * @param bidderId
     * @return
     */
    public boolean checkForBidder(String auctionId, String bidderId) {
        boolean exists = false;
        String sql = "SELECT COUNT(BIDDER_ID) AS 'COUNT' FROM AUCTION_BIDDERS WHERE BIDDER_ID = ? AND AUCTION_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, bidderId);
            ps.setString(2, auctionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("COUNT") > 0) {
                    exists = true;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return exists;
    }

    /**
     * Get the auction attendees to populate the attendees table.
     *
     * @param auctionId
     * @return
     */
    public ObservableList<Attendees> getAttendees(String auctionId) {
        ObservableList<Attendees> data = FXCollections.observableArrayList();
        String sql = "SELECT BIDDER_ID, CONCAT(BIDDERS.FIRST_NAME, ' ', BIDDERS.LAST_NAME) AS 'NAME' FROM AUCTION_BIDDERS LEFT JOIN BIDDERS ON BIDDERS.ID = AUCTION_BIDDERS.BIDDER_ID WHERE AUCTION_BIDDERS.AUCTION_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.add(new Attendees(rs.getInt("BIDDER_ID"), rs.getString("NAME"), new Hyperlink("Remove")));
                } while (rs.next());
            }
            conn.close();
        } catch (SQLException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error removing user from database." + "\n" + "Error Message: " + ex.getMessage());
            alert.showAndWait();
            System.err.println(ex.getMessage());
        }
        return data;
    }

    /**
     * Get the auction based on the ID. Returns a HashMap of values.
     *
     * @param id
     * @return
     */
    public HashMap<String, String> getAuction(String id) {
        HashMap<String, String> auction = new HashMap<>();

        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, NAME, DESCRIPTION, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, BUILDING_NAME, PUBLIC, TYPE, DATE_FORMAT(AUCTION_DATE, '%Y-%m-%d') AS 'AUCTION_DATE' FROM AUCTIONS WHERE ID = ? AND ORGANIZATION_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, Values.ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                auction.put("ID", rs.getString("ID"));
                auction.put("NAME", rs.getString("NAME"));
                auction.put("DESCRIPTION", rs.getString("DESCRIPTION"));
                auction.put("PRIMARY_ADDRESS", rs.getString("PRIMARY_ADDRESS"));
                auction.put("SECONDARY_ADDRESS", rs.getString("SECONDARY_ADDRESS"));
                auction.put("CITY", rs.getString("CITY"));
                auction.put("STATE", rs.getString("STATE"));
                auction.put("POSTAL_CODE", rs.getString("POSTAL_CODE"));
                auction.put("COUNTRY", rs.getString("COUNTRY"));
                auction.put("BUILDING_NAME", rs.getString("BUILDING_NAME"));
                auction.put("PUBLIC", String.valueOf(rs.getBoolean("PUBLIC")));
                auction.put("TYPE", rs.getString("TYPE"));
                auction.put("AUCTION_DATE", rs.getString("AUCTION_DATE"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());;
            }
        }
        return auction;
    }

    /**
     * Get an observable list of auction ids
     *
     * @return
     */
    public ObservableList<String> getAuctionIds() {
        ObservableList<String> data = FXCollections.observableArrayList();
        String sql = "SELECT ID FROM AUCTIONS WHERE ORGANIZATION_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Values.ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.add(rs.getString("ID"));
                } while (rs.next());
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    /**
     * Get the items that are associated with the selected auction.
     *
     * @param id
     * @return
     */
    public ObservableList<AuctionItem> getAuctionItems(String id) {
        ObservableList<AuctionItem> data = FXCollections.observableArrayList();
        String sql = "SELECT ID, NAME, DESCRIPTION FROM AUCTION_ITEMS WHERE AUCTION_ID = ? AND ORGANIZATION_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, Values.ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    Integer itemId = rs.getInt("ID");
                    String itemName = rs.getString("NAME");
                    String itemDescription = rs.getString("DESCRIPTION");
                    data.add(new AuctionItem(itemId, itemName, itemDescription));
                } while (rs.next());
                conn.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    /**
     * Gets an observable list of auction IDs and Names
     *
     * @param val
     * @return
     */
    public ObservableList<String> getAuctionList(String val) {
        ObservableList<String> auctionNames = FXCollections.observableArrayList();
        ObservableList<String> auctionIds = FXCollections.observableArrayList();
        String sql = null;
        if (val == null || val.trim().isEmpty()) {
            sql = "SELECT ID, NAME FROM AUCTIONS WHERE ORGANIZATION_ID = ? ORDER BY ID ASC";
        } else {
            sql = "SELECT ID, NAME FROM AUCTIONS WHERE ORGANIZATION_ID = ? AND (NAME = ? OR NAME LIKE ?) ORDER BY NAME ASC";
        }

        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Values.ORGANIZATION_ID);
            // Add search parameters if the search field is not empty
            if (val != null && !val.trim().isEmpty()) {
                ps.setString(2, val);
                ps.setString(3, "%" + val + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    auctionIds.add(rs.getString("ID"));
                    auctionNames.add(rs.getString("NAME"));
                } while (rs.next());

                // Set the auction IDs in the controller
                ItemFXMLController.auctionIds.setAll(auctionIds);
            }
            conn.close();
        } catch (SQLException ex) {
            Notifications.create().text("Error fetching results\nError: " + ex.getMessage());
            System.err.println(ex.getMessage());
        }
        return auctionNames;
    }

    /**
     * Get an observable list of auction names
     *
     * @return
     */
    public HashMap<String, String> getAuctionNames() {
        HashMap<String, String> data = new HashMap<>();
        String sql = "SELECT ID, NAME FROM AUCTIONS WHERE ORGANIZATION_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Values.ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.put(rs.getString("ID"), rs.getString("NAME"));
                } while (rs.next());
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    /**
     * Returns an observable list of the auction(s) Can return filtered values
     *
     * @param filterBy
     * @return
     */
    protected ObservableList<Auctions> getAuctions(String filterBy) {
        ObservableList<Auctions> data = FXCollections.observableArrayList();
        LocalDate localDate = LocalDate.now();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, NAME, AUCTION_DATE, TYPE FROM AUCTIONS WHERE ORGANIZATION_ID = ?";

        // If the results are being filtered
        if (!filterBy.equals("-")) {
            sql += " AND ";
            switch (filterBy) {
                case "Upcoming":
                    sql += "AUCTION_DATE >= " + DATE_FORMAT.format(localDate);
                    break;
                case "Current":
                    sql += "AUCTION_DATE = " + DATE_FORMAT.format(localDate);
                    break;
                case "Completed":
                    sql += "AUCTION_DATE < " + DATE_FORMAT.format(localDate);
                    break;
                case "Live Auction":
                    sql += "`TYPE` = 'live'";
                    break;
                case "Silent Auction":
                    sql += "`TYPE` = 'silent'";
                    break;
                default:
                    break;
            }
        }
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.add(new Auctions(rs.getInt("ID"), rs.getString("NAME"), rs.getString("AUCTION_DATE"), rs.getString("TYPE").substring(0, 1).toUpperCase() + rs.getString("TYPE").substring(1)));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            Platform.runLater(() -> {
                Notifications.create().text("Error fetching results" + "\n" + "Error Message: " + ex.getMessage()).showError();
            });
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return data;
    }

    /**
     * Remove an attendee from the auction
     *
     * @param id
     * @return
     */
    public boolean removeAttendee(int bidderId, int auctionId) {
        boolean removed = false;
        String sql = "DELETE FROM AUCTION_BIDDERS WHERE BIDDER_ID = ? AND AUCTION_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bidderId);
            ps.setInt(2, auctionId);
            int rs = ps.executeUpdate();
            System.out.println(ps);
            if (rs > 0) {
                removed = true;
            }
            conn.close();
        } catch (SQLException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error removing user from database." + "\n" + "Error Message: " + ex.getMessage());
            alert.showAndWait();
            System.err.println("Message: " + ex.getMessage());
        }

        return removed;
    }

    /**
     * Updates an existing auction event.
     *
     * @param id
     * @param title
     * @param description
     * @param postalCode
     * @param secondaryAddress
     * @param primaryAddress
     * @param buildingName
     * @param country
     * @param isPublic
     * @param state
     * @param city
     * @param type
     * @param dateTime
     * @return
     */
    public boolean saveExistingAuction(String id, String title, String description, String primaryAddress, String secondaryAddress, String city, String state, String postalCode, String country, String buildingName, boolean isPublic, String type, String dateTime) {
        System.out.println("Save existing auction");
        boolean updated = false;
        String sql = "UPDATE AUCTIONS SET NAME = ?, DESCRIPTION = ?, TYPE = ?, PRIMARY_ADDRESS = ?, SECONDARY_ADDRESS = ?, CITY = ?, STATE = ?, POSTAL_CODE = ?, COUNTRY = ?, BUILDING_NAME = ?, PUBLIC = ?, TYPE = ?, AUCTION_DATE = ? WHERE ID = ?";
        System.out.println(sql);
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setString(4, primaryAddress);
            ps.setString(5, secondaryAddress);
            ps.setString(6, city);
            ps.setString(7, state);
            ps.setString(8, postalCode);
            ps.setString(9, country);
            ps.setString(10, buildingName);
            ps.setBoolean(11, isPublic);
            ps.setString(12, type);
            ps.setString(13, dateTime);
            ps.setString(14, id);
            System.out.println(ps);
            int rs = ps.executeUpdate();
            System.out.println(rs);
            if (rs > 0) {
                updated = true;
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println(updated);
        return updated;
    }

    /**
     * Create a new auction event. Returns a string value of the unique auction
     * ID.
     *
     * @param title
     * @param description
     * @param primaryAddress
     * @param secondaryAddress
     * @param city
     * @param state
     * @param country
     * @param postalCode
     * @param buildingName
     * @param type
     * @param isPublic
     * @param dateTime
     * @return
     */
    public String saveNewAuction(String title, String description, String primaryAddress, String secondaryAddress, String city, String state, String postalCode, String country, String buildingName, boolean isPublic, String type, String dateTime) {
        String auctionId = null;
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO AUCTIONS (NAME, DESCRIPTION, ORGANIZATION_ID, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, BUILDING_NAME, PUBLIC, TYPE, AUCTION_DATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, Values.ORGANIZATION_ID);
            ps.setString(4, primaryAddress);
            ps.setString(5, secondaryAddress);
            ps.setString(6, city);
            ps.setString(7, state);
            ps.setString(8, postalCode);
            ps.setString(9, country);
            ps.setString(10, buildingName);
            ps.setBoolean(11, isPublic);
            ps.setString(12, type);
            ps.setString(13, dateTime);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                auctionId = rs.getString(1);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return auctionId;
    }
}
