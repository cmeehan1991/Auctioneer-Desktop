/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.controlsfx.control.Notifications;

import com.cbmwebdevelopment.bid.BidFXMLController;
import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController.AllItems;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Item {

    public ObservableList<AllItems> getAuctionItems(String terms, HashMap<String, String> filters) {

        // The obeservable list to be returned
        ObservableList<AllItems> data = FXCollections.observableArrayList();

        Connection conn = new DBConnection().connect();
        String sql = "SELECT AUCTION_ITEMS.ID, AUCTION_ITEMS.NAME, AUCTION_ITEMS.DESCRIPTION, IF(SILENT_AUCTION = TRUE, 'Silent Auction', 'Live Auction') AS 'TYPE',  IF(CLOSED = TRUE, 'Closed', 'Open') as 'STATUS' FROM AUCTION_ITEMS";

        if (terms != null || filters != null) {
            sql += " WHERE ";
        }

        if (terms != null) {
            sql += "NAME = ? OR DESCRIPTION = ? OR NAME LIKE ? OR DESCRIPTION LIKE ?";
        }

        if (filters != null) {
            if (terms != null) {
                sql += " AND ";
            }

            sql += " CLOSED = ? AND SILENT_AUCTION = ? AND LIVE_AUCTION = ?";
        }

        sql += " ORDER BY ID ASC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (terms != null) {
                ps.setString(1, terms);
                ps.setString(2, terms);
                ps.setString(3, "%" + terms + "%");
                ps.setString(4, "%" + terms + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.addAll(new AllItems(rs.getInt("ID"), rs.getString("NAME"), rs.getString("DESCRIPTION"), rs.getString("TYPE"),
                            rs.getString("STATUS")));
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
        return data;
    }

    public void getItem(String id, BidFXMLController controller) {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT NAME, DESCRIPTION, CLOSED FROM AUCTION_ITEMS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                controller.itemNameTextField.setText(rs.getString("NAME"));
                controller.itemDescriptionTextField.setText(rs.getString("DESCRIPTION"));
                if (rs.getBoolean("CLOSED")) {
                    controller.submitWinnerButton.setDisable(true);
                } else {
                    controller.submitWinnerButton.setDisable(false);
                }
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
     * Get the auction item. Returns a HashMap of the values.
     *
     * @param itemId
     * @return
     */
    protected HashMap<String, String> getValues(String itemId) {
        HashMap<String, String> results = new HashMap<>();

        Connection conn = new DBConnection().connect();
        String sql = "SELECT AUCTION_ITEMS.NAME AS 'NAME', AUCTION_ITEMS.DESCRIPTION, AUCTIONS.NAME as 'AUCTION', AUCTION_ITEMS.CATEGORY AS 'CATEGORY', AUCTION_ITEMS.HAS_RESERVE as 'HAS_RESERVE', AUCTION_ITEMS.RESERVE AS 'RESERVE', AUCTION_ITEMS.SILENT_AUCTION AS 'SILENT_AUCTION', AUCTION_ITEMS.LIVE_AUCTION AS 'LIVE_AUCTION', AUCTION_ITEMS.CLOSED AS 'CLOSED' FROM AUCTION_ITEMS INNER JOIN AUCTIONS ON AUCTION_ITEMS.AUCTION_ID = AUCTIONS.ID WHERE AUCTION_ITEMS.ID =? AND AUCTION_ITEMS.ORGANIZATION_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, itemId);
            ps.setString(2, Values.ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                results.put("name", rs.getString("NAME"));
                results.put("description", rs.getString("DESCRIPTION"));
                results.put("category", rs.getString("CATEGORY"));
                results.put("hasReserve", String.valueOf(rs.getBoolean("HAS_RESERVE")));
                results.put("reserve", rs.getString("RESERVE"));
                results.put("silentAuction", String.valueOf(rs.getBoolean("SILENT_AUCTION")));
                results.put("liveAuction", String.valueOf(rs.getBoolean("LIVE_AUCTION")));
                results.put("closed", String.valueOf(rs.getBoolean("CLOSED")));
                results.put("auction", rs.getString("AUCTION"));
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
        return results;
    }

    public boolean itemExists(String id) {
        boolean exists = false;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT COUNT(ID) FROM AUCTION_ITEMS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                exists = true;
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
        return exists;
    }

    @SuppressWarnings("serial")
    public HashMap<Integer, List<String>> itemInformation(String id) {
        HashMap<Integer, List<String>> list = new HashMap<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, NAME, DESCRIPTION, IF(SILENT_AUCTION = TRUE, 'Silent Auction', 'Live Auction') AS 'AUCTION_TYPE', IF(CLOSED = TRUE, 'Closed', '') AS 'CLOSED', CATEGORY FROM AUCTION_ITEMS WHERE AUCTION_ID = ? ORDER BY ID ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.put(rs.getInt("ID"), new ArrayList<String>() {
                        {
                            add(rs.getString("NAME"));
                            add(rs.getString("DESCRIPTION"));
                            add(rs.getString("AUCTION_TYPE"));
                            add(rs.getString("CLOSED"));
                            add(rs.getString("CATEGORY"));
                        }
                    });
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

    public void removeItem(int id) {
        Connection conn = new DBConnection().connect();
        String sql = "DELETE FROM AUCTION_ITEMS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
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
     * Save a new item or update an existing item. Notifies the user on
     * completion
     *
     * @param controller
     * @throws IOException
     */
    public void saveItem(ItemFXMLController controller) {
        Connection conn = new DBConnection().connect();

        String sql = null;
        if (controller.itemId.trim().isEmpty() || controller.itemId == null) {
            sql = "INSERT INTO AUCTION_ITEMS(NAME, DESCRIPTION, ORGANIZATION_ID, AUCTION_ID, CATEGORY, HAS_RESERVE, RESERVE, SILENT_AUCTION, LIVE_AUCTION) VALUES(?,?,?,?,?,?,?,?,?)";
            try {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, controller.itemName);
                ps.setString(2, controller.itemDescription);
                ps.setString(3, Values.ORGANIZATION_ID);
                ps.setString(4, controller.auctionId);
                ps.setString(5, controller.itemCategory);
                ps.setBoolean(6, controller.hasReserve);
                ps.setString(7, controller.minimumBid);
                ps.setBoolean(8, controller.silentAuction);
                ps.setBoolean(9, controller.liveAuction);
                ps.executeUpdate();
                ResultSet key = ps.getGeneratedKeys();
                if (key.next()) {
                    String id = String.valueOf(key.getInt(1));
                    controller.itemNumberTextField.setText(id);
                    Platform.runLater(() -> {
                        Notifications.create().text("Item Created\n" + "Item ID: " + id + "\n").show();
                    });
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
        } else {
            sql = "UPDATE AUCTION_ITEMS SET NAME = ?, DESCRIPTION = ?, ORGANIZATION_ID = ?, AUCTION_ID = ?, CATEGORY = ?, HAS_RESERVE = ?, RESERVE = ?, SILENT_AUCTION = ?, LIVE_AUCTION = ? WHERE ID = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, controller.itemName);
                ps.setString(2, controller.itemDescription);
                ps.setString(3, Values.ORGANIZATION_ID);
                ps.setString(4, controller.auctionId);
                ps.setString(5, controller.itemCategory);
                ps.setBoolean(6, controller.hasReserve);
                ps.setString(7, controller.minimumBid);
                ps.setBoolean(8, controller.silentAuction);
                ps.setBoolean(9, controller.liveAuction);
                ps.setString(10, controller.itemId);
                int rs = ps.executeUpdate();
                if (rs > 0) {
                    Platform.runLater(() -> {
                        Notifications.create().text("Item Saved\n" + "Item ID: " + controller.itemId + "\n").show();
                    });
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
}
