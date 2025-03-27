package application;

import java.sql.SQLException;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

public class ForgotPasswordPage {

    private final DatabaseHelper databaseHelper;

    public ForgotPasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Forgot Password");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label userLabel = new Label("Enter your Username:");
        TextField userNameField = new TextField();
        userNameField.setMaxWidth(250);

        Label otpLabel = new Label("Enter One-Time Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back to Login");

        loginButton.setOnAction(a -> {
            String userName = userNameField.getText().trim();
            String otp = passwordField.getText().trim();

            // Clear previous error messages
            errorLabel.setText("");

            // Input validation
            if (userName.isEmpty() || otp.isEmpty()) {
                errorLabel.setText("Username and One-Time Password cannot be empty.");
                return;
            }

            try {
                String role = databaseHelper.getUserRole(userName);
                
                if (role != null) {
                    User user = new User(userName, otp, role, false); // Assign correct role
                    if (databaseHelper.loginOneTimePassword(user)) {
                        databaseHelper.clearOneTimePassword(userName); // Clear OTP after login
                        new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                    } else {
                        errorLabel.setText("Invalid One-Time Password. Please try again.");
                    }
                } else {
                    errorLabel.setText("User account does not exist.");
                }
            } catch (SQLException e) {
                errorLabel.setText("Database error. Please try again.");
                e.printStackTrace();
            }
        });

        // Navigate back to login page
        backButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));

        layout.getChildren().addAll(titleLabel, userLabel, userNameField, otpLabel, passwordField, loginButton, backButton, errorLabel);
        Scene forgotPasswordScene = new Scene(layout, 800, 400);

        primaryStage.setScene(forgotPasswordScene);
        primaryStage.setTitle("Forgot Password");
        primaryStage.show();
    }
}
