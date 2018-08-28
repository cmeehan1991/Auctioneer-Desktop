/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.checkout;

import static com.cbmwebdevelopment.main.Values.CURRENCY_FORMAT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.tablecontrollers.CheckoutItemTableViewController.ItemData;
import javafx.collections.*;
/**
 *
 * @author cmeehan
 */
public class Checkout {

	/**
     * Get the items that the user won. Returns an observable list to populate
     * the table with.
     *
     * @param id
     * @return
     */
    public ObservableList<ItemData> getItems(String id, String auctionId) {
        ObservableList<ItemData> data = FXCollections.observableArrayList();
        String sql = "SELECT BIDS.ITEM_ID AS 'ITEM_ID', BIDS.AMOUNT AS 'ITEM_AMOUNT', AUCTION_ITEMS.NAME AS 'ITEM_NAME', AUCTION_ITEMS.DESCRIPTION AS'ITEM_DESCRIPTION' FROM BIDS JOIN AUCTION_ITEMS ON AUCTION_ITEMS.ID = BIDS.ITEM_ID WHERE BIDS.USER_ID = ?";
        
        if(auctionId != null){
            sql += " AND AUCTION_ITEMS.AUCTION_ID = ?";
        }
        
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            if(auctionId != null){
                ps.setString(2, auctionId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                //controller.totalAmountTextField.setText(CURRENCY_FORMAT.format(Double.parseDouble(rs.getString("TOTAL_AMOUNT"))));
                do {
                    data.addAll(new ItemData(rs.getString("ITEM_ID"), rs.getString("ITEM_NAME"), rs.getString("ITEM_DESCRIPTION"), CURRENCY_FORMAT.format(Double.parseDouble(rs.getString("ITEM_AMOUNT")))));
                } while (rs.next());
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } 
        return data;
    }

    /**
	 * Get the user and item information for checkout. 
	 * @param userId
	 * @param controller
	 */
    public HashMap<String, String> getUserInformation(String userId, String auctionId) {
    		HashMap<String, String> data = new HashMap<>();
        System.out.println(userId);
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, IF(PREFIX != '-', PREFIX, '') AS 'PREFIX', CONCAT(FIRST_NAME, ' ', LAST_NAME) AS 'NAME', IF(SUFFIX != '-', SUFFIX, '') AS 'SUFFIX', PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE FROM BIDDERS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("PREFIX") + " " + rs.getString("NAME") + " " + rs.getString("SUFFIX");
                String billingAddress = rs.getString("PRIMARY_ADDRESS") + "\n";
                if (rs.getString("SECONDARY_ADDRESS") != null) {
                    billingAddress += rs.getString("SECONDARY_ADDRESS") + "\n";
                }
                billingAddress += rs.getString("CITY") + ", " + rs.getString("STATE") + " " + rs.getString("POSTAL_CODE");
                
            		data.put("bidderId", rs.getString("ID"));
            		data.put("bidderName", name);
            		data.put("billingAddress", billingAddress);
            		data.put("totalAmountDue", totalAmountDue(userId, auctionId));
            		data.put("totalItems", totalItems(rs.getString("ID")));
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
        return data;
    }

    /**
     * Gets the total amount due that the bidder owes. 
     * @param id
     * @return
     */
    public String totalAmountDue(String id, String auctionId) {
        String totalAmountDue = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT SUM(BIDS.AMOUNT) AS 'TOTAL_AMOUNT' FROM BIDS INNER JOIN AUCTION_ITEMS ON BIDS.ITEM_ID = AUCTION_ITEMS.ID WHERE BIDS.USER_ID = ? AND AUCTION_ITEMS.AUCTION_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, auctionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalAmountDue = CURRENCY_FORMAT.format(rs.getDouble("TOTAL_AMOUNT"));
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
        return totalAmountDue;
    }

    /**
     * Gets the total number of items that the bidder won.
     * @param id
     * @return
     */
    public String totalItems(String id) {
        String totalItems = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT COUNT(DISTINCT BIDS.ITEM_ID) AS 'COUNT' FROM BIDS WHERE BIDS.USER_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalItems = rs.getString("COUNT");
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
        return totalItems;
    }
}
