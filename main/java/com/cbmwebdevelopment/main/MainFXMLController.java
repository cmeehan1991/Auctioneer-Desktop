package com.cbmwebdevelopment.main;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cbmwebdevelopment.auction.AuctionDashboardMain;
import com.cbmwebdevelopment.bid.BidMain;
import com.cbmwebdevelopment.bid.ViewBidsMain;
import com.cbmwebdevelopment.bidder.BidderListMain;
import com.cbmwebdevelopment.bidder.BidderMain;
import com.cbmwebdevelopment.checkout.CheckoutMain;
import com.cbmwebdevelopment.items.Item;
import com.cbmwebdevelopment.items.ItemMain;
import com.cbmwebdevelopment.items.ViewItemsMain;
import com.sibvisions.rad.ui.javafx.ext.FXToolBarRT39866;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;


public class MainFXMLController implements Initializable {


    @FXML
    public FXDesktopPane desktopPane;
    
    @FXML
    public FXToolBarRT39866 toolbar;
    
    @FXML
    public ToolBar mainToolbar;

    private BidMain bidMain;
    private ItemMain itemMain;
    private ViewBidsMain viewBidsMain;
    private ViewItemsMain viewItems;

    @FXML
    protected void addBid(ActionEvent event) throws IOException {
        bidMain = new BidMain();
        bidMain.isNew = true;
        bidMain.start(new Stage());
    }

    @FXML
    protected void addNewBidder(ActionEvent event) throws IOException {
        BidderMain bidderMain = new BidderMain();
        bidderMain.isNew = true;
        bidderMain.start(new Stage());
    }
    
    @FXML
    protected void addNewItem(ActionEvent event) throws IOException {
        itemMain = new ItemMain();
        itemMain.isNew = true;
        itemMain.start(new Stage());
    }

    @FXML
    protected void auctionDashboardAction(ActionEvent even) throws IOException{
        AuctionDashboardMain auctionDashboardMain = new AuctionDashboardMain();
        auctionDashboardMain.desktopPane = desktopPane;
        auctionDashboardMain.start(new Stage());
    }

    @FXML
    protected void bidderCheckout(ActionEvent event) throws IOException {

        CheckoutMain checkoutMain = new CheckoutMain();
        checkoutMain.start(new Stage());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    protected void showDashboardAction(ActionEvent event) {

    }

    @FXML
    protected void viewAllItems(ActionEvent event) throws IOException {
        viewItems = new ViewItemsMain();
        viewItems.start(new Stage());
    }

    @FXML
    protected void viewBids(ActionEvent event) throws IOException {
        viewBidsMain = new ViewBidsMain();
        viewBidsMain.start(new Stage());
    }

    @FXML
    protected void viewEditBidder(ActionEvent event) throws IOException {
        BidderListMain bidderListMain = new BidderListMain();
        bidderListMain.start(new Stage());

    }

    @FXML
    protected void viewEditItems(ActionEvent event) throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Item");
        dialog.setHeaderText("View/Edit an existing item");
        dialog.setContentText("Enter a valid item number");
        dialog.showAndWait();

        String itemId = dialog.getEditor().getText();
        if(itemId != null && !itemId.trim().isEmpty()) {
        		Item item = new Item();
        		if(item.itemExists(itemId)) {
                itemMain = new ItemMain();
                itemMain.itemNumber = itemId;
                itemMain.isNew = false;
                itemMain.desktopPane = desktopPane;
                itemMain.start(new Stage());
        		}
        }

    }
}
