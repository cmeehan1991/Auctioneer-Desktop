/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bid;

import static com.cbmwebdevelopment.main.Values.CURRENCY_FORMAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cbmwebdevelopment.main.Values.BID_LINK;
import com.cbmwebdevelopment.tablecontrollers.ViewBidsTableViewController.Bids;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

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
        try {
            // Establish connection;
            URL url = new URL(BID_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Set the arguments
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("bid_exists", "UTF-8");
            data += "&" + URLEncoder.encode("item_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

            // Write to the output stream
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObject = new JSONObject(reader.readLine());
            exists = jsonObject.getBoolean("EXISTS");

            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return exists;
    }

    /**
     * Get the bid information
     *
     * @param bidId
     * @return
     */
    protected HashMap<String, String> getBid(String bidId) {
        HashMap<String, String> bidData = new HashMap<>();

        try {
            URL url = new URL(BID_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_bid", "UTF-8");
            data += "&" + URLEncoder.encode("bid_id", "UTF-8") + "=" + URLEncoder.encode(bidId, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONObject results = new JSONObject(reader.readLine());
            if (results.getBoolean("SUCCESS")) {
                bidData.put("ITEM_ID", results.getString("ITEM_ID"));
                bidData.put("ITEM_NAME", results.getString("ITEM_NAME"));
                bidData.put("ITEM_DESCRIPTION", results.getString("ITEM_DESCRIPTION"));
                bidData.put("BIDDER_ID", results.getString("BIDDER_ID"));
                bidData.put("BID_AMOUNT", CURRENCY_FORMAT.format(results.getDouble("BID_AMOUNT")));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return bidData;
    }

    /**
     *
     * @param auctionId
     * @return
     */
    protected ObservableList<Bids> getBids(String auctionId) {
        ObservableList<Bids> bidData = FXCollections.observableArrayList();
        try {
            URL url = new URL(BID_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_bids", "UTF-8");
            data += "&" + URLEncoder.encode("organization_id", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("ORGANIZATION_ID"), "UTF-8");
            data += "&" + URLEncoder.encode("auction_id", "UTF-8") + "=" + URLEncoder.encode(auctionId, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArray = new JSONArray(reader.readLine());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                bidData.add(new Bids(jsonObject.getString("ID"), jsonObject.getString("ITEM_NAME"), jsonObject.getString("NAME"), jsonObject.getString("BID_AMOUNT")));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return bidData;
    }

    /**
     * Submit the winning bid to the database.
     *
     * @param bidAmount
     * @param bidderId
     * @param itemNumber
     * @param bidId
     * @return
     */
    protected boolean submitBid(String bidAmount, String bidderId, String itemNumber, String bidId) {
        boolean submitted = false;
        try {
            URL url = new URL(BID_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = "&" + URLEncoder.encode("bid_amount", "UTF-8") + "=" + URLEncoder.encode(bidAmount, "UTF-8");
            data += "&" + URLEncoder.encode("bidder_id", "UTF-8") + "=" + URLEncoder.encode(bidderId, "UTF-8");

            if (bidId != null) {
                data += "&" + URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("update_bid", "UTF-8");
                data += "&" + URLEncoder.encode("bid_id", "UTF-8") + "=" + URLEncoder.encode(bidId, "UTF-8");

            } else {
                data += "&" + URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("new_bid", "UTF-8");
                data += "&" + URLEncoder.encode("item_number", "UTF-8") + "=" + URLEncoder.encode(itemNumber, "UTF-8");
            }

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.readLine());

            submitted = jsonObj.getBoolean("SUCCESS");

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return submitted;
    }

    public HashMap<Integer, List<String>> winningBidInformation(String id) {
        HashMap<Integer, List<String>> list = new HashMap<>();
        try {
            URL url = new URL(BID_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("winning_bid", "UTF-8");
            data += "&" + URLEncoder.encode("auction_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONArray jsonArr = new JSONArray(reader.readLine());
            if (jsonArr.length() > 0) {
                Integer i = 1;
                for (int c = 0; c < jsonArr.length(); c++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(c);
                    list.put(i, new ArrayList<String>() {
                        {
                            add(jsonObj.getString("ITEM NAME"));
                            add(jsonObj.getString("ITEM DESCRIPTION"));
                            add(CURRENCY_FORMAT.format(jsonObj.getDouble("WINNING AMOUNT")));
                            add(jsonObj.getString("WINNER NAME"));
                            add(jsonObj.getString("BIDDER ID"));
                        }
                    });
                }
                i++;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } 
        return list;
    }
}
