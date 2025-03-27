package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * UserHomePage class represents the main interface for a logged-in student user.
 * It allows navigation, viewing account details, and logging out.
 */
public class UserHomePage {
	
    private final DatabaseHelper databaseHelper;
    private final User user;

    public UserHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome, " + user.getUserName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label roleLabel = new Label("Role: " + user.getRole());

        // Button to navigate to the Invitation Page (only for admins)
        Button inviteButton = new Button("Invite Users");
        inviteButton.setOnAction(e -> new InvitationPage().show(databaseHelper, primaryStage));
        inviteButton.setVisible("admin".equals(user.getRole())); // Show only for Admins

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));

        layout.getChildren().addAll(welcomeLabel, roleLabel, inviteButton, logoutButton);
        Scene userScene = new Scene(layout, 800, 400);

        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Home Page");
    }
}
