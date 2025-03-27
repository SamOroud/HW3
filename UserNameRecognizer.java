package application;

public class UserNameRecognizer {
    
    public static String userNameRecognizerErrorMessage = "";
    public static int userNameRecognizerIndexofError = -1;
    
    private static int state = 0, nextState = 0, userNameSize = 0;
    private static boolean running;
    private static String inputLine = "";
    private static char currentChar;
    private static int currentCharNdx;

    public static String checkForValidUserName(String input) {
        if (input.isEmpty()) {
            userNameRecognizerIndexofError = 0;
            return "*** ERROR *** The input is empty";
        }

        state = 0;
        inputLine = input;
        currentCharNdx = 0;
        currentChar = input.charAt(0);
        running = true;
        userNameSize = 0;

        while (running) {
            switch (state) {
                case 0:
                    if (Character.isUpperCase(currentChar)) {
                        nextState = 1;
                        userNameSize++;
                    } else {
                        return setError("*** ERROR *** Username must start with an uppercase letter", 0);
                    }
                    break;

                case 1:
                    if (Character.isLetterOrDigit(currentChar)) {
                        nextState = 1;
                        userNameSize++;
                    } else if (currentChar == '_') {
                        nextState = 2;
                        userNameSize++;
                    } else {
                        return setError("*** ERROR *** Invalid character detected", currentCharNdx);
                    }
                    if (userNameSize > 16) {
                        return setError("*** ERROR *** Username exceeds 16 characters", currentCharNdx);
                    }
                    break;

                case 2:
                    if (Character.isLetterOrDigit(currentChar)) {
                        nextState = 1;
                        userNameSize++;
                    } else {
                        return setError("*** ERROR *** Underscore must be followed by a letter or digit", currentCharNdx);
                    }
                    break;
            }

            moveToNextCharacter();
            state = nextState;
        }

        if (state == 1 && input.charAt(0) != '_' && input.charAt(input.length() - 1) != '_') {
            return ""; 
        } else {
            return setError("*** ERROR *** Invalid username format", currentCharNdx);
        }
    }

    private static void moveToNextCharacter() {
        currentCharNdx++;
        if (currentCharNdx < inputLine.length())
            currentChar = inputLine.charAt(currentCharNdx);
        else
            running = false;
    }

    private static String setError(String errorMessage, int errorIndex) {
        userNameRecognizerErrorMessage = errorMessage;
        userNameRecognizerIndexofError = errorIndex;
        running = false;
        return errorMessage;
    }
}
