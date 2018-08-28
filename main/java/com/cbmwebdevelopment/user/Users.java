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
import java.sql.Statement;
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
import com.cbmwebdevelopment.tablecontrollers.UsersTableController.AllUsers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class Users {

    /**
     * Add the new user to the database
     *
     * @param controller
     */
    public void addNewUser(UserInformationController controller) {

        // Random password
        String password = MainApp.randomPasswordGenerator(16);

        String sql = "INSERT INTO USERS (USERNAME, PASSWORD, PREFIX, FIRST_NAME, LAST_NAME, SUFFIX, TELEPHONE, EMAIL, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, RESET_PASSWORD) VALUES(?,MD5(?),?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
        		Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, controller.username);
            ps.setString(2, password);
            ps.setString(3, controller.prefix);
            ps.setString(4, controller.firstName);
            ps.setString(5, controller.lastName);
            ps.setString(6, controller.suffix);
            ps.setString(7, controller.telephone);
            ps.setString(8, controller.email);
            ps.setString(9, controller.streetAddress);
            ps.setString(10, controller.secondaryAddress);
            ps.setString(11, controller.city);
            ps.setString(12, controller.state);
            ps.setString(13, controller.postalCode);
            ps.setBoolean(14, true);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                String key = String.valueOf(rs.getInt(1));
                sendEmailNotification(password, controller.firstName + " " + controller.lastName, controller.username, controller.email, key);
                Alert alert = new Alerts().informationAlert("User Added", "The user was successfully added.", controller.firstName + " " + controller.lastName + " was successfully added.");
                alert.showAndWait();
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
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
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, CONCAT(FIRST_NAME, ' ', LAST_NAME) AS 'NAME' FROM USERS";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    users.add(rs.getString("ID") + " - " + rs.getString("NAME"));
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
        return users;
    }

    /**
     * Get the user ID based from the username.
     *
     * @param uname
     * @return
     */
    private String getUserId(String uname) {
        String uId = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID FROM USERS WHERE USERNAME = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                uId = rs.getString("ID");
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
        return uId;
    }

    /**
     * Reset the user's password.
     *
     * @param controller
     * @param username
     */
    public void resetPassword(UserInformationController controller, String username) {
        Connection conn = new DBConnection().connect();
        String userId = username == null ? controller.userId : getUserId(username);
        System.out.println(username);
        System.out.println(getUserId(username));
        String password = MainApp.randomPasswordGenerator(16);
        String sql = "UPDATE USERS SET PASSWORD = MD5(?), RESET_PASSWORD = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, password);
            ps.setBoolean(2, true);
            ps.setString(3, userId);
            System.out.println(ps.toString());
            int rs = ps.executeUpdate();
            if (rs > 0) {
                sendEmailNotification(password, controller.firstName + " " + controller.lastName, controller.username, controller.email, userId);
                Notifications.create().text("Password updated for " + controller.firstName).showInformation();
            }else {
            		System.out.println("Password Not Updated");
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
     *  Get an observable list of all users. 
     *  If there are terms then the list will be filtered based on the search terms using 
     *  the parseSearchTerms method. 
     * @param terms
     * @return
     */
    public ObservableList<AllUsers> searchAllUsers(String terms){
    		ObservableList<AllUsers> data = FXCollections.observableArrayList();
    		Connection conn = new DBConnection().connect();
    		String sql = "SELECT ID, CONCAT(FIRST_NAME, ' ', LAST_NAME) AS 'NAME', USERNAME FROM USERS WHERE ORGANIZATION_ID = ?";
    		
    		if(terms != null) {
    			sql += addSearchTerms();
    		}
    		
    		try {
    			PreparedStatement ps = conn.prepareStatement(sql);
    			ps.setString(1, Values.ORGANIZATION_ID);
    			if(terms != null) {
    				ps.setString(2, terms);
    				ps.setString(3, terms);
    				ps.setString(4, terms);
    				ps.setString(5, terms);
    				ps.setString(6, "%" + terms + "%");
    				ps.setString(7, "%" + terms + "%");
    				ps.setString(8, "%" + terms + "%");
    				ps.setString(9, "%" + terms + "%");
    			}
    			ResultSet rs = ps.executeQuery();
    			if(rs.next()) {
    				do {
    					data.addAll(new AllUsers(rs.getInt("ID"), rs.getString("NAME"), rs.getString("USERNAME")));
    				}while(rs.next());
    			}
    		}catch(SQLException ex) {
    			System.err.println(ex.getMessage());
    		}finally {
    			try {
    				conn.close();
    			}catch(SQLException ex) {
    				System.err.println(ex.getMessage());
    			}
    		}
    		
    		return data;
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
    public void setUserInformation(UserInformationController controller, String id) {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, USERNAME, PREFIX, FIRST_NAME, LAST_NAME, SUFFIX, TELEPHONE, EMAIL, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE FROM USERS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                controller.userIdLabel.setText(rs.getString("ID"));
                controller.prefixComboBox.getSelectionModel().select(rs.getString("PREFIX"));
                controller.firstNameTextField.setText(rs.getString("FIRST_NAME"));
                controller.lastNameTextField.setText(rs.getString("LAST_NAME"));
                controller.suffixComboBox.getSelectionModel().select(rs.getString("SUFFIX"));
                controller.streetAddressTextField.setText(rs.getString("PRIMARY_ADDRESS"));
                controller.secondaryAddressTextField.setText(rs.getString("SECONDARY_ADDRESS"));
                controller.cityTextField.setText(rs.getString("CITY"));
                controller.stateComboBox.getSelectionModel().select(rs.getString("STATE"));
                controller.postalCodeTextField.setText(rs.getString("POSTAL_CODE"));
                controller.telephoneTextField.setText(rs.getString("TELEPHONE"));
                controller.emailTextField.setText(rs.getString("EMAIL"));
                controller.usernameTextField.setText(rs.getString("USERNAME"));
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
     * Update the user information
     * @param controller 
     */
    public void updateUserInformation(UserInformationController controller) {
        Connection conn = new DBConnection().connect();
        String sql = "UPDATE USERS SET USERNAME = ?, PREFIX = ?, FIRST_NAME = ?, LAST_NAME = ?, SUFFIX = ?, TELEPHONE = ?, EMAIL = ?, PRIMARY_ADDRESS = ?, SECONDARY_ADDRESS = ?, CITY = ?, STATE = ?, POSTAL_CODE = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, controller.username);
            ps.setString(2, controller.prefix);
            ps.setString(3, controller.firstName);
            ps.setString(4, controller.lastName);
            ps.setString(5, controller.suffix);
            ps.setString(6, controller.telephone);
            ps.setString(7, controller.email);
            ps.setString(8, controller.streetAddress);
            ps.setString(9, controller.secondaryAddress);
            ps.setString(10, controller.city);
            ps.setString(11, controller.state);
            ps.setString(12, controller.postalCode);
            ps.setInt(13, Integer.parseInt(controller.userId));
            int rs = ps.executeUpdate();
            if(rs > 0){
               Notifications.create().text("User information saved!").position(Pos.TOP_RIGHT).show();
            }else{
                Notifications.create().text("User information NOT saved!").position(Pos.TOP_RIGHT).showWarning();
            }
        } catch (SQLException ex) {
            ArrayList<String> error = new ArrayList<>();
            error.add(ex.getMessage());
            error.add(ex.getSQLState());
            new Alerts().errorAlert("Error", "Error saving user information", "Error information:", error).showAndWait();
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
     * Update the user's password with their new password.
     *
     * @param userId
     * @param password
     * @param controller
     * @throws Exception
     */
    public void updateUserPassword(String userId, String password, UserSignInFXMLController controller) throws Exception {
        Connection conn = new DBConnection().connect();
        String sql = "UPDATE USERS SET PASSWORD = MD5(?), RESET_PASSWORD = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, password);
            ps.setBoolean(2, false);
            ps.setString(3, userId);
            int rs = ps.executeUpdate();
            if (rs > 0) {
                MainApp main = new MainApp();
                Values.IS_SIGNED_IN = true;
                Values.USER_ID = userId;
                main.start(new Stage());

                Stage currentStage = (Stage) controller.confirmNewPasswordField.getScene().getWindow();
                currentStage.close();
            } else {
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
