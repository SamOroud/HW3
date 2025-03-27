package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
    
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("User Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Input fields
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(a -> {
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();

            // Validate input fields
            if (userName.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and Password cannot be empty.");
                return;
            }

            try {
                User user = new User(userName, password, "", true);
                WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
                String role = databaseHelper.getUserRole(userName);

                if (role != null) {
                    user.setRole(role);
                    if (databaseHelper.login(user)) {
                        user.setFirstLogin(false);
                        welcomeLoginPage.show(primaryStage, user);
                    } else {
                        errorLabel.setText("Invalid Username or Password.");
                    }
                } else {
                    errorLabel.setText("User account does not exist.");
                }

            } catch (SQLException e) {
                showErrorAlert("Database Error", "An error occurred while accessing the database.");
                e.printStackTrace();
            }
        });

        // Forgot Password Button
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setOnAction(e -> new ForgotPasswordPage(databaseHelper).show(primaryStage));

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            User dummyUser = new User("Guest", "", "student", false); // Replace with actual user data if needed
            new WelcomeLoginPage(databaseHelper).show(primaryStage, dummyUser);
        });

        layout.getChildren().addAll(titleLabel, userNameField, passwordField, loginButton, errorLabel, forgotPasswordButton, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }

    // Show an error pop-up
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
