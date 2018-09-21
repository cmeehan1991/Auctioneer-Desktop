/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.controlsfx.control.Notifications;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.Values;
import static com.cbmwebdevelopment.main.Values.USERS_LINK;
import com.cbmwebdevelopment.tablecontrollers.UsersTableController.AllUsers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class Users {

    /**
     * Add the new user to the database
     *
     * @param username
     * @param prefix
     * @param firstName
     * @param lastName
     * @param suffix
     * @param telephone
     * @param email
     * @param streetAddress
     * @param secondaryAddress
     * @param city
     * @param state
     * @param postalCode
     */
    public String addNewUser(String username, String prefix, String firstName, String lastName, String suffix, String telephone, String email, String streetAddress, String secondaryAddress, String city, String state, String postalCode) {

        // Random password
        String password = MainApp.randomPasswordGenerator(16);
        String userId = null;
        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("add_new_user", "UTF-8");
            data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("prefix", "UTF-8") + "=" + URLEncoder.encode(prefix, "UTF-8");
            data += "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("suffix", "UTF-8") + "=" + URLEncoder.encode(suffix, "UTF-8");
            data += "&" + URLEncoder.encode("telephone", "UTF-8") + "=" + URLEncoder.encode(telephone, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("street_address", "UTF-8") + "=" + URLEncoder.encode(streetAddress, "UTF-8");
            data += "&" + URLEncoder.encode("secondary_address", "UTF-8") + "=" + URLEncoder.encode(secondaryAddress, "UTF-8");
            data += "&" + URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8");
            data += "&" + URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
            data += "&" + URLEncoder.encode("postal_code", "UTF-8") + "=" + URLEncoder.encode(postalCode, "UTF-8");
            data += "&" + URLEncoder.encode("organization_id", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("ORGANIZATION_ID"), "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONObject jsonObj = new JSONObject(reader.readLine());
            userId = jsonObj.getString("ID");

            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return userId;
    }

    private String addSearchTerms() {
        String terms = " AND (ID = ? OR FIRST_NAME = ? OR LAST_NAME = ? OR USERNAME = ? OR ID LIKE ? OR FIRST_NAME LIKE ? OR LAST_NAME LIKE ? OR USERNAME LIKE ?)";
        return terms;
    }

    /**
     * Returns an observable list of the user id and user for all registered
     * users.
     *
     * userId - name
     *
     * @return
     */
    public ObservableList<String> getAllUsers() {
        ObservableList<String> users = FXCollections.observableArrayList(); // The observable list to be returned

        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_all_users", "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONArray jsonArr = new JSONArray(reader.readLine());
            if (jsonArr.length() > 0) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    users.add(jsonObj.getString("ID") + " - " + jsonObj.getString("NAME"));
                }
            }

            reader.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return users;
    }

    /**
     * Get the user ID based from the username.
     *
     * @param uname
     * @return
     */
    public String getUserId(String username) {
        String userId = null;
        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_user_id", "UTF-8");
            data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONObject jsonObj = new JSONObject(reader.readLine());
            userId = jsonObj.getString("ID");

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return userId;
    }

    /**
     * Reset the user's password.
     *
     * @param userId
     * @param username
     * @return
     */
    public boolean resetPassword(String username, String userId) {
        boolean success = false;
        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("update_password", "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(MainApp.randomPasswordGenerator(8), "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("reset", "UTF-8") + "=" + URLEncoder.encode("true", "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.readLine());
            success = jsonObj.getBoolean("SUCCESS");

            writer.close();
            reader.close();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return success;
    }

    /**
     * Get an observable list of all users. If there are terms then the list
     * will be filtered based on the search terms using the parseSearchTerms
     * method.
     *
     * @param terms
     * @return
     */
    public ObservableList<AllUsers> searchAllUsers(String terms) {
        ObservableList<AllUsers> users = FXCollections.observableArrayList();

        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("search_all_users", "UTF-8");
            data += "&" + URLEncoder.encode("organization_id", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("ORGANIZATION_ID"), "UTF-8");
            data += "&" + URLEncoder.encode("terms", "UTF-8") + "=" + URLEncoder.encode(terms, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArr = new JSONArray(reader.readLine());
            if (jsonArr.length() > 0) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    users.addAll(new AllUsers(jsonObj.getInt("ID"), jsonObj.getString("NAME"), jsonObj.getString("USERNAME")));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return users;
    }

    /**
     * Sends an email notification to the new user with their temporary password
     * and username.
     *
     * @param password
     * @param name
     * @param username
     * @param email
     * @param key
     */
    protected void sendEmailNotification(String password, String name, String username, String email, String key) {
        // Set the host and port information 
        String smtpHost = "mail.cbmwebdevelopment.com";
        int smtpPort = 25;

        // Set the sender, message content and subject
        String sender = "connor.meehan@cbmwebdevelopment.com";
        String messageContent = name + ",\n\n" + "\tYou have been added as a user to The Pregnancy Center of Hilton Head\'s Auctioneer auction management application. Please use the below information to sign in. You will be prompted to change your password once you have successfully signed in." + "\n\n" + "username: " + username + "\n" + "password: " + password + "\n\n" + "DO NOT respond to this message. This message was automatically generated. Please contact your systems administrator with any questions. If you feel you received this message by accident please simply ignore it.";
        String subject = "Auctioneer New User";

        // Mailer properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        Session session = Session.getDefaultInstance(properties, null);

        try {
            // Construct the text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(messageContent);

            // Construct the mime multi part
            MimeMultipart mimeMultiPart = new MimeMultipart();
            mimeMultiPart.addBodyPart(textBodyPart);

            // Create the sender/recipient addresses
            InternetAddress iaSender = new InternetAddress(sender);
            InternetAddress iaRecipient = new InternetAddress(email);

            // Construct the mime message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setSender(iaSender);
            mimeMessage.setFrom(iaSender);
            mimeMessage.setRecipient(Message.RecipientType.TO, iaRecipient);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(mimeMultiPart);

            Transport.send(mimeMessage);
            //Alert alert = new Alert()
        } catch (MessagingException ex) {
            System.err.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Notification Failed");
            alert.setHeaderText("Email Notification Failed");
            alert.setContentText("The email notification failed to send to the user.");
            alert.showAndWait();
        }
    }

    /**
     * Sets the user information in the controller
     *
     * @param controller
     * @param id
     */
    public HashMap<String, String> getUserInformation(String userId) {
        HashMap<String, String> userInfo = new HashMap<>();
        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_user_information", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.readLine());
            
            userInfo.put("ID", jsonObj.getString("ID"));
            userInfo.put("PREFIX", jsonObj.getString("PREFIX"));
            userInfo.put("FIRST_NAME", jsonObj.getString("FIRST_NAME"));
            userInfo.put("LAST_NAME", jsonObj.getString("LAST_NAME"));
            userInfo.put("SUFFIX", jsonObj.getString("SUFFIX"));
            userInfo.put("PRIMARY_ADDRESS", jsonObj.getString("PRIMARY_ADDRESS"));
            userInfo.put("SECONDARY_ADDRESS", jsonObj.getString("SECONDARY_ADDRESS"));
            userInfo.put("CITY", jsonObj.getString("CITY"));
            userInfo.put("STATE", jsonObj.getString("STATE"));
            userInfo.put("POSTAL_CODE", jsonObj.getString("POSTAL_CODE"));
            userInfo.put("TELEPHONE", jsonObj.getString("TELEPHONE"));
            userInfo.put("EMAIL", jsonObj.getString("EMAIL"));
            userInfo.put("USERNAME", jsonObj.getString("USERNAME"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return userInfo;
    }

    /**
     * Update the user information. Return True if the user's information was
     * updated successfully. Otherwise return false.
     *
     * @param username
     * @param prefix
     * @param userId
     * @param lastName
     * @param firstName
     * @param telephone
     * @param suffix
     * @param primaryAddress
     * @param email
     * @param secondaryAddress
     * @param state
     * @param city
     * @param postalCode
     * @return boolean value - true if the user was updated.
     */
    public boolean updateUserInformation(String username, String prefix, String firstName, String lastName, String suffix, String telephone, String email, String primaryAddress, String secondaryAddress, String city, String state, String postalCode, String userId) {
        boolean response = false;

        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("update_user_information", "UTF-8");
            data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("prefix", "UTF-8") + "=" + URLEncoder.encode(prefix, "UTF-8");
            data += "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("suffix", "UTF-8") + "=" + URLEncoder.encode(suffix, "UTF-8");
            data += "&" + URLEncoder.encode("telephone", "UTF-8") + "=" + URLEncoder.encode(telephone, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("street_address", "UTF-8") + "=" + URLEncoder.encode(primaryAddress, "UTF-8");
            data += "&" + URLEncoder.encode("secondary_address", "UTF-8") + "=" + URLEncoder.encode(secondaryAddress, "UTF-8");
            data += "&" + URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8");
            data += "&" + URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
            data += "&" + URLEncoder.encode("postal_code", "UTF-8") + "=" + URLEncoder.encode(postalCode, "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObject = new JSONObject(reader.readLine());

            response = jsonObject.getBoolean("SUCCESS");

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return response;

    }

    /**
     * Update the user's password with their new password.
     *
     * @param userId
     * @param password
     * @throws Exception
     */
    public boolean updateUserPassword(String userId, String password) throws Exception {
        boolean success = false;
        try {
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("update_password", "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("reset", "UTF-8") + "=" + URLEncoder.encode("false", "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.readLine());
            success = jsonObj.getBoolean("SUCCESS");

            writer.close();
            reader.close();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return success;
    }

    /**
     * Check if the username has been taken
     *
     * @param username
     * @param userId
     * @return
     */
    public boolean userExists(String username, String userId) {
        boolean userExists = false;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT COUNT(ID) as 'COUNT' FROM USERS WHERE USERNAME = ?";
        if (userId != null) {
            sql += " AND ID != ?";
        }
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            if (userId != null) {
                ps.setString(2, userId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("COUNT") > 0) {
                    userExists = true;
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
        return userExists;
    }

    /**
     * Allow the user to sign in to the Auctioneer application. If the user is
     * new they will be prompted to set a new password before the application
     * opens.
     *
     * @param controller
     * @param username
     * @param password
     */
    protected void userSignIn(UserSignInFXMLController controller, String username, String password) {
        Connection conn = new DBConnection().connect();

        String sql = "SELECT ID, ORGANIZATION_ID, RESET_PASSWORD FROM USERS WHERE USERNAME = ? AND PASSWORD = MD5(?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean resetPassword = rs.getBoolean("RESET_PASSWORD");
                controller.userId = rs.getString("ID");
                Values.ORGANIZATION_ID = rs.getString("ORGANIZATION_ID");
                if (resetPassword) {
                    controller.makeFadeOut();
                } else {
                    MainApp main = new MainApp();
                    Values.IS_SIGNED_IN = true;
                    Values.USER_ID = rs.getString("ID");
                    main.start(new Stage());

                    Stage currentStage = (Stage) controller.usernameLabel.getScene().getWindow();
                    currentStage.close();
                }
            } else {
                controller.passwordLabel.setStyle("-fx-fill-color:#ff0000;");
                controller.userPasswordPasswordField.setStyle("-fx-border-color:#ff0000;");
                controller.shakeScene();
            }
        } catch (Exception ex) {
            ArrayList<String> error = new ArrayList<>();
            error.add(ex.getMessage());
            Alert alert = new Alerts().errorAlert("SQL Error", "Login Error", "Error Message:", error);
            alert.showAndWait();
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ArrayList<String> error = new ArrayList<>();
                error.add(ex.getMessage());
                Alert alert = new Alerts().errorAlert("Connection closed error", "Connetion close error", "Error Message:", error);
                alert.showAndWait();
                System.err.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
}
