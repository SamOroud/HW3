package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OneTimePasswordPage {

    private final DatabaseHelper databaseHelper;

    public OneTimePasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("One-Time Password Reset");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label userLabel = new Label("Enter Username:");
        TextField userNameField = new TextField();
        userNameField.setMaxWidth(250);

        Label otpLabel = new Label("Enter One-Time Password:");
        TextField oneTimePasswordField = new TextField();
        oneTimePasswordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button resetPasswordButton = new Button("Set One-Time Password");
        Button backButton = new Button("Back to Admin Home");

        // Reset Password Action
        resetPasswordButton.setOnAction(a -> {
            String userName = userNameField.getText().trim();
            String oneTimePassword = oneTimePasswordField.getText().trim();

            // Validate Input
            if (userName.isEmpty() || oneTimePassword.isEmpty()) {
                errorLabel.setText("Username and One-Time Password cannot be empty.");
                return;
            }

            // Check if User Exists
            if (!databaseHelper.doesUserExist(userName)) {
                errorLabel.setText("User does not exist. Please enter a valid username.");
                return;
            }

            // Set OTP in Database
            databaseHelper.setOneTimePassword(userName, oneTimePassword);

            // Show Success Alert
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("One-Time Password set successfully for " + userName + ".");
            successAlert.showAndWait();

            // Clear Fields After Successful Reset
            userNameField.clear();
            oneTimePasswordField.clear();
            errorLabel.setText("");
        });

        // Back Button Action (Redirect to Admin Home)
        backButton.setOnAction(e -> {
            User adminUser = new User("admin", "password", "admin", false);
            new AdminHomePage(databaseHelper, adminUser).show(primaryStage);
        });


        layout.getChildren().addAll(titleLabel, userLabel, userNameField, otpLabel, oneTimePasswordField, resetPasswordButton, backButton, errorLabel);
        Scene otpScene = new Scene(layout, 800, 400);

        primaryStage.setScene(otpScene);
        primaryStage.setTitle("One-Time Password Reset");
    }
}
