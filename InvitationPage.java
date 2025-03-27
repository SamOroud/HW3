package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * InvitationPage class allows an admin to generate an invitation code.
 */
public class InvitationPage {

    /**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Page Title
        Label titleLabel = new Label("Generate Invitation Code");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Invitation Code Label
        Label inviteCodeLabel = new Label("Click the button to generate a code.");
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        // Generate Code Button
        Button showCodeButton = new Button("Generate Code");

        // Copy to Clipboard Button
        Button copyButton = new Button("Copy Code");
        copyButton.setDisable(true); // Disabled until a code is generated

        // Back Button
        Button backButton = new Button("Back");
        
        // Generate Code Action
        showCodeButton.setOnAction(a -> {
            String invitationCode = databaseHelper.generateInvitationCode();
            if (invitationCode != null && !invitationCode.isEmpty()) {
                inviteCodeLabel.setText("Invitation Code: " + invitationCode);
                copyButton.setDisable(false);
            } else {
                inviteCodeLabel.setText("Error generating code. Please try again.");
                copyButton.setDisable(true);
            }
        });

        // Copy to Clipboard Action
        copyButton.setOnAction(a -> {
            String code = inviteCodeLabel.getText().replace("Invitation Code: ", "");
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(code);
            clipboard.setContent(content);

            Alert copiedAlert = new Alert(Alert.AlertType.INFORMATION);
            copiedAlert.setTitle("Copied");
            copiedAlert.setHeaderText(null);
            copiedAlert.setContentText("Invitation code copied to clipboard.");
            copiedAlert.showAndWait();
        });

        // Back Button Action (Redirects to Admin Home)
       
        backButton.setOnAction(e -> {
        	 User adminUser = new User("admin", "password", "admin", false);
        	 new AdminHomePage(databaseHelper, adminUser).show(primaryStage);
        });


        // Add UI Elements to Layout
        layout.getChildren().addAll(titleLabel, showCodeButton, inviteCodeLabel, copyButton, backButton);

        // Set Scene
        Scene inviteScene = new Scene(layout, 800, 400);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invitation Code Generator");
    }
}
