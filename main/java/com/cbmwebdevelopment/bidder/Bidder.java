/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.tablecontrollers.BidderAuctionHistoryTableController.BidderAuctionHistory;
import com.cbmwebdevelopment.tablecontrollers.BidderAuctionsTableController.BidderAuctions;
import com.cbmwebdevelopment.tablecontrollers.BiddersTableController.Bidders;

import javafx.collections.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author cmeehan
 */
public class Bidder {

	/**
     * Returns a HashMap of bidder information to be printed onto an excel table.
     * @return 
     */
	public HashMap<Integer, List<String>> getAllBidders(){
        HashMap<Integer, List<String>> bidders = new HashMap<>();
        String sql = "SELECT ID, CONCAT(IF(PREFIX IS NOT NULL, CONCAT(PREFIX, ' '), ''), FIRST_NAME, ' ', LAST_NAME, IF(SUFFIX IS NOT NULL AND SUFFIX != '', CONCAT(', ', SUFFIX), '')) AS 'NAME', TELEPHONE, EMAIL, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE FROM BIDDERS";
        try{
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    bidders.put(rs.getInt("ID"), new ArrayList<String>(){{add(rs.getString("NAME"));add(rs.getString("TELEPHONE"));add(rs.getString("EMAIL"));add(rs.getString("PRIMARY_ADDRESS"));add(rs.getString("SECONDARY_ADDRESS"));add(rs.getString("CITY"));add(rs.getString("STATE"));add(rs.getString("POSTAL_CODE"));}});
                }while(rs.next());
            }
            conn.close();
        }catch(SQLException ex){
            System.err.println(ex.getMessage());
        }
        
        return bidders;        
    }
	
	/**
	 * 
	 * @return
	 */
	    public HashMap<Integer, List<String>> getAuctionBidders(String auctionId){
	        HashMap<Integer, List<String>> bidders = new HashMap<>();
	
	        String sql = "SELECT ID, CONCAT(IF(PREFIX IS NOT NULL, CONCAT(PREFIX, ' '), ''), FIRST_NAME, ' ', LAST_NAME, IF(SUFFIX IS NOT NULL AND SUFFIX != '', CONCAT(', ', SUFFIX), '')) AS 'NAME', TELEPHONE, EMAIL, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE FROM AUCTION_BIDDERS LEFT JOIN BIDDERS ON BIDDERS.ID = AUCTION_BIDDERS.BIDDER_ID WHERE AUCTION_BIDDERS.AUCTION_ID = ?";
	        try{
	            Connection conn = new DBConnection().connect();
	            PreparedStatement ps = conn.prepareStatement(sql);
	            ps.setString(1, auctionId);
	            ResultSet rs = ps.executeQuery();
	            if(rs.next()){
	                do{
	                    bidders.put(rs.getInt("ID"), new ArrayList<String>(){{add(rs.getString("NAME"));add(rs.getString("TELEPHONE"));add(rs.getString("EMAIL"));add(rs.getString("PRIMARY_ADDRESS"));add(rs.getString("SECONDARY_ADDRESS"));add(rs.getString("CITY"));add(rs.getString("STATE"));add(rs.getString("POSTAL_CODE"));}});
	                }while(rs.next());
	            }
	            conn.close();
	        }catch(SQLException ex){
	            System.err.println(ex.getMessage());
	        }        
	        return bidders;        
	    }
	
    public ObservableList<BidderAuctionHistory> getBidderAuctionHistory(String id){
		System.out.println("Getting auction history");
		ObservableList<BidderAuctionHistory> data = FXCollections.observableArrayList();
		String sql = "SELECT AUCTION_BIDDERS.AUCTION_ID AS 'ID', AUCTIONS.NAME AS 'AUCTION', AUCTIONS.AUCTION_DATE AS 'AUCTION_DATE', COUNT(AUCTION_ITEMS.ID) AS 'ITEMS_WON' FROM AUCTION_BIDDERS LEFT JOIN AUCTION_ITEMS ON AUCTION_ITEMS.WINNER_ID = AUCTION_BIDDERS.BIDDER_ID AND AUCTION_ITEMS.AUCTION_ID = AUCTION_BIDDERS.AUCTION_ID INNER JOIN AUCTIONS ON AUCTIONS.ID = AUCTION_BIDDERS.AUCTION_ID WHERE AUCTION_BIDDERS.BIDDER_ID = ? AND AUCTIONS.AUCTION_DATE < NOW() GROUP BY AUCTION_BIDDERS.AUCTION_ID ORDER BY AUCTIONS.AUCTION_DATE DESC";
		try {
			System.out.println("try");
			Connection conn = new DBConnection().connect();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("has history");
				do {
					System.out.println("history");
					data.add(new BidderAuctionHistory(rs.getInt("ID"), rs.getString("AUCTION"), rs.getDate("AUCTION_DATE").toLocalDate(), rs.getInt("ITEMS_WON")));
				}while(rs.next());
			}
			conn.close();
		}catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return data;
	}

    /**
	 * Get an observable list of the current and upcoming auctions that the bidder is participating in.
	 * @param id
	 * @return
	 */
	public ObservableList<BidderAuctions> getBidderAuctions(String id){
		ObservableList<BidderAuctions> data = FXCollections.observableArrayList();
		String sql = "SELECT AUCTION_BIDDERS.AUCTION_ID AS 'ID', AUCTIONS.NAME AS 'AUCTION', AUCTIONS.AUCTION_DATE AS 'AUCTION_DATE' FROM AUCTION_BIDDERS INNER JOIN AUCTIONS ON AUCTIONS.ID = AUCTION_BIDDERS.AUCTION_ID WHERE AUCTION_BIDDERS.BIDDER_ID = ? AND AUCTIONS.AUCTION_DATE >= NOW() GROUP BY AUCTION_BIDDERS.AUCTION_ID ORDER BY AUCTIONS.AUCTION_DATE DESC";
		try {
			Connection conn = new DBConnection().connect();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				do {
					data.add(new BidderAuctions(rs.getInt("ID"), rs.getString("AUCTION"), rs.getDate("AUCTION_DATE").toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
				}while(rs.next());
			}
			conn.close();
		}catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return data;
	}

    /**
     * Returns an observable list of the bidders
     * @param terms
     * @return 
     */
    protected ObservableList<Bidders> getBidders(String terms) {
        ObservableList<Bidders> data = FXCollections.observableArrayList();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, CONCAT(FIRST_NAME, ' ', LAST_NAME) AS 'NAME', CITY, STATE FROM BIDDERS";
        if(terms != null){
            sql += " WHERE FIRST_NAME = ? OR LAST_NAME = ? OR ID = ? OR CITY = ? OR STATE = ? OR FIRST_NAME LIKE ? OR LAST_NAME LIKE ?";
        }
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if(terms!= null){ // Parse the search terms if there are any
                ps.setString(1, terms);
                ps.setString(2, terms);
                ps.setString(3, terms);
                ps.setString(4, terms);
                ps.setString(5, terms);
                ps.setString(6, "%" + terms + "%");
                ps.setString(7, "%" + terms + "%");
            }
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    data.addAll(new Bidders(rs.getInt("ID"), rs.getString("NAME"), rs.getString("CITY"), rs.getString("STATE")));
                }while(rs.next());
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
     * Get the bidder's email address.
     *
     * @param id
     * @return
     */
    public String getEmail(String id) {
        String userEmail = null;
        if (id == null) {
            Alert alert = new Alerts().errorAlert("Invalid ID", "The Bidder ID is not valid.", "Please check the ", null);
            alert.showAndWait();
            return null;
        }

        Connection conn = new DBConnection().connect();
        String sql = "SELECT EMAIL FROM BIDDERS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userEmail = rs.getString("EMAIL");
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

        return userEmail;

    }

    /**
     * Get an existing bidder
     *
     * @param id
     * @return
     */
    public HashMap<String, String> getUser(String id) {
        HashMap<String, String> user = new HashMap<>();

        Connection conn = new DBConnection().connect();

        String sql = "SELECT ID, PREFIX, FIRST_NAME, LAST_NAME, SUFFIX, TYPE, TELEPHONE, EMAIL, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE FROM BIDDERS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.put("id", rs.getString("ID"));
                user.put("prefix", rs.getString("PREFIX"));
                user.put("firstName", rs.getString("FIRST_NAME"));
                user.put("lastName", rs.getString("LAST_NAME"));
                user.put("suffix", rs.getString("SUFFIX"));
                user.put("type", rs.getString("TYPE"));
                user.put("telephone", rs.getString("TELEPHONE"));
                user.put("email", rs.getString("EMAIL"));
                user.put("primaryAddress", rs.getString("PRIMARY_ADDRESS"));
                user.put("secondaryAddress", rs.getString("SECONDARY_ADDRESS"));
                user.put("city", rs.getString("CITY"));
                user.put("state", rs.getString("STATE"));
                user.put("postalCode", rs.getString("POSTAL_CODE"));
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

        return user;
    }
    
/**
 * Save a new bidder
 *
 * @param args
 * @param controller
 */
protected void saveBidder(HashMap<String, String> args, BidderFXMLController controller) {
    Connection conn = new DBConnection().connect();
    String sql = "INSERT INTO BIDDERS(PREFIX, FIRST_NAME, LAST_NAME, SUFFIX, TYPE, TELEPHONE, EMAIL, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    try {
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, args.get("prefix"));
        ps.setString(2, args.get("firstName"));
        ps.setString(3, args.get("lastName"));
        ps.setString(4, args.get("suffix"));
        ps.setString(5, "BIDDER");
        ps.setString(6, args.get("telephone"));
        ps.setString(7, args.get("email"));
        ps.setString(8, args.get("primaryAddress"));
        ps.setString(9, args.get("secondaryAddress"));
        ps.setString(10, args.get("city"));
        ps.setString(11, args.get("state"));
        ps.setString(12, args.get("postalCode"));
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            controller.bidderIdTextField.setText(rs.getString(1));
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Bidder Saved");
            alert.setHeaderText("Bidder saved to database");
            alert.setContentText("Bidder " + rs.getString(1) + " has been added.");
            alert.showAndWait();
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
}
    
    
    /**
     * Update an existing bidder
     *
     * @param args
     */
    protected void updateBidder(HashMap<String, String> args, BidderFXMLController controller) {
        Connection conn = new DBConnection().connect();
        String sql = "UPDATE BIDDERS SET PREFIX = ?, FIRST_NAME = ?, LAST_NAME = ?, SUFFIX = ?, TYPE = ?, TELEPHONE = ?, EMAIL = ?, PRIMARY_ADDRESS = ?, SECONDARY_ADDRESS = ?, CITY = ?, STATE = ?, POSTAL_CODE = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, args.get("prefix"));
            ps.setString(2, args.get("firstName"));
            ps.setString(3, args.get("lastName"));
            ps.setString(4, args.get("suffix"));
            ps.setString(5, "BIDDER");
            ps.setString(6, args.get("telephone"));
            ps.setString(7, args.get("email"));
            ps.setString(8, args.get("primaryAddress"));
            ps.setString(9, args.get("secondaryAddress"));
            ps.setString(10, args.get("city"));
            ps.setString(11, args.get("state"));
            ps.setString(12, args.get("postalCode"));
            ps.setString(13, args.get("id"));
            int update = ps.executeUpdate();
            if (update > 0) {
                new Alerts().informationAlert("Bidder Updated", "Bidder " + args.get("id") + " updated", "The bidder has been successfully updated.").showAndWait();
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
    }
}
