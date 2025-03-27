package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * The AdminSetupPage class handles the setup process for creating an administrator account.
 * This is used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {

    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Administrator Setup");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label userLabel = new Label("Enter Admin Username:");
        TextField userNameField = new TextField();
        userNameField.setMaxWidth(250);

        Label passLabel = new Label("Enter Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button setupButton = new Button("Setup Admin Account");

        setupButton.setOnAction(event -> {
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and password cannot be empty!");
                return;
            }

            try {
                // Create a new Admin User (Default Email Placeholder)
                User adminUser = new User(userName, password, "admin", "admin@example.com");

                // Register Admin in Database
                databaseHelper.register(adminUser);
                System.out.println("Administrator setup completed.");

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Admin Setup Completed");
                successAlert.setContentText("Admin account created successfully. Redirecting to login.");
                successAlert.showAndWait();

                // Navigate to Login Page
                new UserLoginPage(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
                errorLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        layout.getChildren().addAll(titleLabel, userLabel, userNameField, passLabel, passwordField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
