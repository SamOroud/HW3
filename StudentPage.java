package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentPage {

	
	 public void show(Stage primaryStage) {
	    	VBox layout = new VBox();
	    	
		    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		    
		    // label to display the welcome message for the student
		    Label studentLabel = new Label("Hello, Student!");
		    
		    studentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		    layout.getChildren().add(studentLabel);
		    Scene studentScene = new Scene(layout, 800, 400);

		    // Set the scene to primary stage
		    primaryStage.setScene(studentScene);
		    primaryStage.setTitle("Student Page");
		    
		
		    
	    }
	
	
	
	
	
}
