package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;
import javafx.scene.control.ButtonType;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays admin functionalities like user deletion and navigation.
 */

public class AdminHomePage {
    
    private final DatabaseHelper databaseHelper;
    private final User user;

    public AdminHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;  // Properly initialize user
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Welcome Label
        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            databaseHelper.closeConnection(); // Close DB connection for safety
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Back Button
        Button backButton = new Button("Back"); 
        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

        // Back Button
        Button qaButton = new Button("QAHomePage"); 
        qaButton.setOnAction(e -> {
            new QAHomePage(databaseHelper, user).show(primaryStage);
        });
        
        // User Deletion Field
        TextField userNameDeletionField = new TextField();
        userNameDeletionField.setPromptText("Enter username to delete");
        userNameDeletionField.setMaxWidth(250);

        // Delete User Button
        Button deleteButton = new Button("Delete User");
        deleteButton.setOnAction(a -> {
            String userNameDeletion = userNameDeletionField.getText();

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want to delete this user?");
            alert.setContentText("User: " + userNameDeletion);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deletionSuccess = databaseHelper.deleteUser(userNameDeletion);
                if (deletionSuccess) {
                    errorLabel.setText("User successfully deleted.");
                } else {
                    errorLabel.setText("Failed to delete user. Please ensure the username is correct.");
                }
            }
        });

        // Display Users Button with Output Label
        Button printUsers = new Button("Display all Users");
        Label usersListLabel = new Label("User List: ");
        TextArea usersDisplay = new TextArea();
        usersDisplay.setEditable(false);
        usersDisplay.setWrapText(true);
        usersDisplay.setMaxWidth(300);
        usersDisplay.setMaxHeight(200);

        printUsers.setOnAction(a -> {
            String users = databaseHelper.getAllUsers();  // Ensure `getAllUsers()` exists in DatabaseHelper.java
            usersDisplay.setText(users);
        });

        // Adding Components to Layout
        layout.getChildren().addAll(adminLabel, userNameDeletionField, deleteButton, errorLabel, printUsers, usersListLabel, usersDisplay, qaButton, backButton, logoutButton);
        
        // Set Scene
        Scene adminScene = new Scene(layout, 800, 400);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}
