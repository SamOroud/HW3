package application;

import java.util.Scanner;
import databasePart1.DatabaseHelper;


/**
 * The AskQuestionTester class automates the testing of inserting a question 
 * into the database and verifying its existence
 */

public class AskQuestionTester {
	

	private final DatabaseHelper databaseHelper;
	/**
     * constructor for AskQuestionTester.
     *
     *
     * @param databaseHelper An instance of DatabaseHelper to interact with the database.
     */
	this.databaseHelper = databaseHelper;

    public AskQuestionTester(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
 
    }
	
    static int numPassed = 0; //Counter For passed tests
    static int numFailed = 0; //Counter For failed tests

    
    /**
     * the main method that initializes and runs the automated test
     * 
     * @param args
     */
    
    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation");

        //test case: Asking a question and verifying its existence
        performTestCase(1, "This Question Tester is Working", true);

        System.out.println("____________________________________________________________________________");
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    
    /**
     * executes a test case by inserting a question into the database and verifying its existence
     *
     * @param testCase The test case number
     * @param questionText The question text to be inserted
     * @param expectedPass Expected outcome of the test (true if question should exist, false otherwise)
     */
    private static void performTestCase(int testCase, String questionText, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Question: \"" + questionText + "\"");
        System.out.println("______________");

        //call the database helper to insert the question
        boolean isAsked = databaseHelper.insertQuestions(questionText);

        //See if the question exists in the database
        boolean exists = databaseHelper.searchQuestions(questionText);

        //determine result
        if (exists == expectedPass) {
            System.out.println("***Success*** The question <" + questionText + "> was correctly processed.");
            numPassed++;
        } else {
            System.out.println("***Failure*** The question <" + questionText + "> was not found when expected.");
            numFailed++;
        }
    }
}


