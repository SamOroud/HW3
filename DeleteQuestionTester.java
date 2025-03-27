package application;

import java.util.Scanner;
import databasePart1.DatabaseHelper;



public class DeleteQuestionTester {
	
	private final DatabaseHelper databaseHelper;
	this.databaseHelper = databaseHelper;

    public DeleteQuestionTester(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
 
    }
    
    static int numPassed = 0;
    static int numFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation");
        
        performTestCase(1, "Question1");
        performTestCase(2, "Question2");
        performTestCase(3, "Question3");
        
        System.out.println("____________________________________________________________________________");
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }
    
    private static void performTestCase(int testCase, String question) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Question: \"" + question + "\"");
        System.out.println("______________");
        
        //insert question into database
        databaseHelper.insertQuestion(question);

        //check to see if question was created
        boolean created = databaseHelper.searchQuestions(question);
        
        //delete the question
        databaseHelper.deleteQuestion(question);
        
        //check if the question still exists in the database
        boolean exists = databaseHelper.searchQuestions(question);
        
        if (!exists && created) {
            System.out.println("***Success*** The question <" + question + "> was created and deleted successfully.");
            numPassed++;
        } else {
            System.out.println("***Failure*** The question <" + question + "> was not (created) or (deleted) from the database!");
            numFailed++;
        }
    }
}
