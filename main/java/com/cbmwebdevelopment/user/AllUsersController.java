package com.cbmwebdevelopment.user;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbmwebdevelopment.tablecontrollers.UsersTableController;
import com.cbmwebdevelopment.tablecontrollers.UsersTableController.AllUsers;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AllUsersController implements Initializable {

	@FXML
	TextField searchTextField;

	@FXML
	ProgressIndicator progressIndicator;

	@FXML
	TableView<AllUsers> allUsersTableView;

	private UsersTableController tableController;
	protected FXInternalWindow window;

	private void getUsers(String search) {
		if (!progressIndicator.isVisible()) {
			allUsersTableView.setDisable(true);
			progressIndicator.setVisible(true);
			progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		}

		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(() -> {
			Users users = new Users();
			ObservableList<AllUsers> data = users.searchAllUsers(search);
			Platform.runLater(() -> {
				allUsersTableView.setItems(data);
				allUsersTableView.setDisable(false);
				progressIndicator.setVisible(false);
				progressIndicator.setProgress(0.0);
			});
			executor.shutdown();
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Initialize and setup the table controller
		tableController = new UsersTableController();
		tableController.tableView(allUsersTableView);

		// Get the users
		getUsers(null);

		// Hide the progress indicator on load
		progressIndicator.setVisible(false);
		progressIndicator.setProgress(0.0);

		// Set the value change listener for the search field
		searchTextField.textProperty().addListener((obs, ov, nv) -> {
			if (nv != null && !nv.trim().isEmpty()) {
				getUsers(nv);
			} else {
				getUsers(null);
			}
		});

		// Set the click listener on the table
		allUsersTableView.setRowFactory(tv -> {
			TableRow<AllUsers> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					AllUsers allUsers = row.getItem();
					viewUser(String.valueOf(allUsers.getId()));
				}
			});
			return row;
		});
	}

	
	@FXML
	protected void searchForUserAction(ActionEvent event) {
		String searchTerms = searchTextField.getText().trim().isEmpty() ? null : searchTextField.getText();
		getUsers(searchTerms);
	}

	@FXML
	protected void selectUserAction(ActionEvent event) {
		boolean rowSelected = allUsersTableView.getSelectionModel() != null;
		if(rowSelected) {
			AllUsers selectedUser = allUsersTableView.getSelectionModel().getSelectedItem();
			viewUser(String.valueOf(selectedUser.getId()));
		}
	}

	private void viewUser(String id) {
		try {
			// Start the user information window
			UserInformationMain userMain = new UserInformationMain();
			userMain.newUser = false;
			userMain.userId = id;
			userMain.start(new Stage());
			// Close the current window
			window.close();
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}

}
