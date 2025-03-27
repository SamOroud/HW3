package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access if no users exist in the database.
 */
public class FirstPage {

    private final DatabaseHelper databaseHelper;

    public FirstPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the first page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Welcome to the System");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label userLabel = new Label("Checking system setup...");
        userLabel.setStyle("-fx-font-size: 14px;");

        Button continueButton = new Button("Continue");
        continueButton.setDisable(true); // Initially disabled until we check database

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Background database check
        new Thread(() -> {
            try {
                if (databaseHelper.isDatabaseEmpty()) {
                    // If no users exist, enable the admin setup
                    userLabel.setText("Hello, You are the first user. Click 'Continue' to set up admin access.");
                    continueButton.setOnAction(e -> new AdminSetupPage(databaseHelper).show(primaryStage));
                } else {
                    // If users already exist, redirect to login page
                    userLabel.setText("Administrator already exists. Redirecting to login...");
                    continueButton.setText("Go to Login");
                    continueButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));
                }
                continueButton.setDisable(false);
            } catch (SQLException e) {
                errorLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        layout.getChildren().addAll(titleLabel, userLabel, continueButton, errorLabel);

        Scene firstPageScene = new Scene(layout, 800, 400);
        primaryStage.setScene(firstPageScene);
        primaryStage.setTitle("System Setup");
        primaryStage.show();
    }
}
