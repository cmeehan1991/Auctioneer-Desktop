package com.cbmwebdevelopment.main;

import static com.cbmwebdevelopment.main.Values.IS_SIGNED_IN;
import java.security.SecureRandom;

import com.sibvisions.rad.ui.javafx.ext.FXToolBarRT39866;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static FXToolBarRT39866 toolbar;
    public static ToolBar mainToolbar;
    public static final String ERROR_LABEL = "-fx-text-fill:#ff0000";
    public static FXDesktopPane desktopPane;

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static String randomPasswordGenerator(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()_";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public static void signOut() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        if (IS_SIGNED_IN) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
            BorderPane root = (BorderPane) loader.load();
            MainFXMLController controller = (MainFXMLController) loader.getController();

            Scene scene = new Scene(root);

            stage.setWidth(windowWidth());
            stage.setHeight(windowHeight());
            scene.setFill(Color.DARKGRAY);
            stage.setTitle("Auctioneer");
            stage.setScene(scene);
            stage.show();

            Platform.runLater(() -> {
                stage.setOnCloseRequest((value) -> {
                    Platform.exit();
                });
            });
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserSignInFXML.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Scene scene = new Scene(root);

            stage.setTitle("User Sign In | Auctioneer");
            stage.setScene(scene);
            stage.show();
        }
    }

    private double windowHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight() - 50;
    }

    private double windowWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth() - 100;
    }

}
