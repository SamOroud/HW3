package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.DatabaseHelper;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Button continueButton = new Button("Continue to your Page");

	    continueButton.setOnAction(a -> {
	    	if (user == null) {
                showErrorAlert("User Error", "User data is missing. Please log in again.");
                new UserLoginPage(databaseHelper).show(primaryStage);
                return;
            }

	    	String role = user.getRole();
	    	System.out.println("User Role: " + role);
	    	
	    	if ("admin".equals(role)) {
	    		new AdminHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    	else {
	    		if (user.isFirstLogin()) {
	    			new UserLoginPage(databaseHelper).show(primaryStage);
	    		} else {
	    			new UserHomePage(databaseHelper, user).show(primaryStage);
	    		}
	    	}
	    });

	    // Logout Button
	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> new UserLoginPage(databaseHelper).show(primaryStage));

	    // Quit Button
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit JavaFX application
	    });

	    // Invite Button for Admin
	    if (user != null && "admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> new InvitationPage().show(databaseHelper, primaryStage));
            layout.getChildren().add(inviteButton);
        }

	    layout.getChildren().addAll(welcomeLabel, continueButton, quitButton, logoutButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }

    // Display Error Alert
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
