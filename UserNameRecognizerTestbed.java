package application;

import java.util.Scanner;

/**
 * <p> Title: UserNameRecognizerTestbed </p>
 * 
 * <p> Description: A console-based testbed to verify usernames using FSM logic. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2024 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01 - Improved error handling, scanner usage, and efficiency.
 */
public class UserNameRecognizerTestbed {

    public static void main(String[] args) {
        System.out.println("Welcome to the UserName Recognizer Testbed\n");
        System.out.println("Please enter a UserName or press Enter to exit.");

        try (Scanner keyboard = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter username: ");
                String inputLine = keyboard.nextLine().trim();

                if (inputLine.isEmpty()) {
                    System.out.println("\n*** Empty input detected. Exiting program.");
                    break;
                }

                String errMessage = UserNameRecognizer.checkForValidUserName(inputLine);

                if (!errMessage.isEmpty()) {  // Use best practice for checking empty strings
                    System.out.println(errMessage);
                    System.out.println(inputLine);

                    // Show error position using an up-arrow (⬆)
                    int errorIndex = UserNameRecognizer.userNameRecognizerIndexofError;
                    if (errorIndex >= 0) {
                        System.out.println(" ".repeat(errorIndex) + "⬆ Error here");
                    }
                } else {
                    System.out.println("✅ Success! The username is valid.");
                }

                System.out.println("\nEnter another username or press Enter to exit.");
            }
        }
    }
}
