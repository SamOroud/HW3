package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the student
 * and provides navigation to the Q&A section.
 */
public class StudentHomePage {
    
	private final DatabaseHelper databaseHelper;
	private final User user;

	
	public StudentHomePage(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper = databaseHelper;
		this.user = user;
		
	}
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display Hello Student
        Label userLabel = new Label("Hello, Student!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });
        
        // Back Button
        Button backButton = new Button("Back"); 
        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });
        
        // Q&A Button
        Button qaButton = new Button("Q & A");
        qaButton.setOnAction(e -> {
            new QAHomePage(databaseHelper, user).show(primaryStage);
        });
        
        layout.getChildren().addAll(userLabel, qaButton, logoutButton, backButton);
        Scene studentScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(studentScene);
        primaryStage.setTitle("Student Home Page");
    }
}
