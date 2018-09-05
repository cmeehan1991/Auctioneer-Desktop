package com.cbmwebdevelopment.main;

import com.cbmwebdevelopment.auction.AuctionController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainFXMLController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private StackPane stackPane;
    
    @FXML
    private VBox navigationItemsBox;

    @FXML
    private Button auctionDashboardButton, bidsListButton, bidderListButton, itemListButton;

    @FXML
    private MenuBar menuBar;

    @FXML
    ScrollPane scrollPane;

    public static ScrollPane mainScrollPane;
    @FXML
    protected void auctionDashboardAction(ActionEvent event) {
        setActiveItem(auctionDashboardButton);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionsFXML.fxml"));
            AnchorPane anchorPane = loader.load();
            AuctionController controller = (AuctionController) loader.getController();
            controller.scrollPane = scrollPane;
            controller.stackPane = stackPane;
            
            setScrollPane(anchorPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void viewAuctionItemsListAction(ActionEvent event) {
        setActiveItem(itemListButton);
        try {
            AnchorPane anchorPane = new FXMLLoader().load(getClass().getResource("/fxml/ViewItemsFXML.fxml"));
            setScrollPane(anchorPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void viewBidderListAction(ActionEvent event) {
        setActiveItem(bidderListButton);
        try {
            AnchorPane anchorPane = new FXMLLoader().load(getClass().getResource("/fxml/BidderListFXML.fxml"));
            setScrollPane(anchorPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void viewBidsListAction(ActionEvent event) {
        setActiveItem(bidsListButton);
        try {
            AnchorPane anchorPane = new FXMLLoader().load(getClass().getResource("/fxml/ViewBidsFXML.fxml"));
            
            setScrollPane(anchorPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void checkoutAction(ActionEvent event) {

    }


    /*
    * Change the main content view and change the active button's class. 
     */
    private void setActiveItem(Button clickedButton) {

        // Remove the style classs of active
        ObservableList<Node> items = navigationItemsBox.getChildren();
        items.forEach(item -> {
            ObservableList<String> styles = item.getStyleClass();
            if (styles.contains("active")) {
                item.getStyleClass().remove("active");
            }
        });

        // Add the style class of active to the clicked button
        clickedButton.getStyleClass().add("active");
    }

    private void setInitialContent() {
        if (!auctionDashboardButton.getStyleClass().contains("active")) {
            auctionDashboardButton.getStyleClass().add("active");
        }
        try {
            AnchorPane auctionDashboard = new FXMLLoader().load(getClass().getResource("/fxml/AuctionsFXML.fxml"));
            setScrollPane(auctionDashboard);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    /**
     * Set the scroll pane content
     */
    private void setScrollPane(AnchorPane node) {
        scrollPane.setContent(null);

        double spWidth = scrollPane.getWidth();
        double spHeight = scrollPane.getHeight();

        double nodeHeight = node.getHeight();
        double nodeWidth = node.getWidth();
        
        if (spWidth != 0) {
            if (spWidth > nodeWidth) {
                node.setPrefWidth(spWidth - 2);
            }

            if (spHeight > nodeHeight) {
                node.setPrefHeight(spHeight - 2);
            }
        }else{
            node.setPrefWidth(1050);
            node.setPrefHeight(760);
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(node);
        
        mainScrollPane = scrollPane;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setInitialContent();
    }
}
