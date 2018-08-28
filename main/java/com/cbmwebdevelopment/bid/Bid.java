/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import static com.cbmwebdevelopment.main.Values.CURRENCY_FORMAT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.tablecontrollers.ViewBidsTableViewController.Bids;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author cmeehan
 */
public class Bid {

    /**
     * Check if the item has already been won. Returns true if the item has been
     * won.
     *
     * @param id - Item ID Number
     * @return
     */
    private boolean bidExists(String id) {
        boolean exists = false;
        String sql = "SELECT ID FROM BIDS WHERE ITEM_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return exists;
    }

    protected HashMap<String, String> getBid(String bidId) {
        HashMap<String, String> data = new HashMap<>();
        String sql = "SELECT BIDS.ID, BIDS.AMOUNT AS 'BID_AMOUNT', BIDS.ITEM_ID AS 'ITEM_ID', AUCTION_ITEMS.DESCRIPTION AS 'ITEM_DESCRIPTION', AUCTION_ITEMS.NAME AS 'ITEM_NAME', BIDS.USER_ID AS 'BIDDER_ID' FROM BIDS INNER JOIN AUCTION_ITEMS ON AUCTION_ITEMS.ID = BIDS.ITEM_ID WHERE BIDS.ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, bidId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                data.put("ITEM_ID", rs.getString("ITEM_ID"));
                data.put("ITEM_NAME", rs.getString("ITEM_NAME"));
                data.put("ITEM_DESCRIPTION", rs.getString("ITEM_DESCRIPTION"));
                data.put("BIDDER_ID", rs.getString("BIDDER_ID"));
                data.put("BID_AMOUNT", CURRENCY_FORMAT.format(rs.getDouble("BID_AMOUNT")));
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    /**
     *
     * @param auctionId
     * @return
     */
    protected ObservableList<Bids> getBids(String auctionId) {
        ObservableList<Bids> data = FXCollections.observableArrayList();
        String sql = "SELECT BIDS.ID AS 'ID', AUCTION_ITEMS.NAME AS 'ITEM_NAME', CONCAT(IF(BIDDERS.PREFIX != '-', CONCAT(BIDDERS.PREFIX, ' '), ''), BIDDERS.FIRST_NAME, ' ', BIDDERS.LAST_NAME, IF(BIDDERS.SUFFIX != '-', CONCAT(', ', BIDDERS.SUFFIX), '')) AS 'NAME', BIDS.AMOUNT AS 'AMOUNT' FROM BIDS INNER JOIN BIDDERS ON BIDDERS.ID = BIDS.USER_ID INNER JOIN AUCTION_ITEMS ON AUCTION_ITEMS.ID = BIDS.ITEM_ID";
        if (auctionId != null) {
            sql += " WHERE AUCTION_ITEMS.AUCTION_ID = ?";
        }
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            if (auctionId != null) {
                ps.setString(1, String.valueOf(auctionId));
            }
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.addAll(new Bids(rs.getString("ID"), rs.getString("ITEM_NAME"), rs.getString("NAME"), CURRENCY_FORMAT.format(rs.getDouble("AMOUNT"))));
                } while (rs.next());
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    /**
     * Get the item ID that was won.
     *
     * @param id
     * @return
     */
    protected String getItem(String id) {
        String itemId = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ITEM_ID FROM BIDS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                itemId = rs.getString("ITEM_ID");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        return itemId;
    }

    /**
     * Submit the winning bid to the database.
     *
     * @param controller
     */
    protected void submitBid(BidFXMLController controller) {
        Connection conn = new DBConnection().connect();
        String sql;
        if (bidExists(controller.itemNumber)) {
            sql = "UPDATE BIDS SET AMOUNT = ?, USER_ID = ? WHERE ITEM_ID = ?";
        } else {
            sql = "INSERT INTO BIDS (AMOUNT, USER_ID, ITEM_ID) VALUES (?,?,?)";
        }
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, controller.bidAmount);
            ps.setString(2, controller.bidderNumber);
            ps.setString(3, controller.itemNumber);
            ps.executeUpdate();

            ps.close();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Bid Submitted");
            alert.setHeaderText("The bid has been successfully submitted");
            alert.setContentText("The bid was successfully submitted.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Databae Error");
            alert.setHeaderText("Error submitting the bid to the database");
            alert.setContentText("Error Message:\n" + ex.getMessage());
            alert.showAndWait();
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public HashMap<Integer, List<String>> winningBidInformation(String id) {
        HashMap<Integer, List<String>> list = new HashMap<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT AUCTION_ITEMS.NAME AS 'ITEM NAME', AUCTION_ITEMS.DESCRIPTION AS 'ITEM DESCRIPTION', BIDS.AMOUNT AS 'WINNING AMOUNT', CONCAT(BIDDERS.FIRST_NAME, ' ', BIDDERS.LAST_NAME) AS 'WINNER NAME', BIDDERS.ID AS 'BIDDER ID' FROM BIDS INNER JOIN AUCTION_ITEMS ON BIDS.ITEM_ID = AUCTION_ITEMS.ID INNER JOIN BIDDERS ON BIDS.USER_ID = BIDDERS.ID WHERE AUCTION_ITEMS.AUCTION_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer integer = 1;
                do {
                    list.put(integer, new ArrayList<String>() {
                        {
                            add(rs.getString("ITEM NAME"));
                            add(rs.getString("ITEM DESCRIPTION"));
                            add(CURRENCY_FORMAT.format(rs.getDouble("WINNING AMOUNT")));
                            add(rs.getString("WINNER NAME"));
                            add(rs.getString("BIDDER ID"));
                        }
                    });
                    integer++;
                } while (rs.next());
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
        return list;
    }
}
