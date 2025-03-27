package application;

import java.util.Scanner;
import databasePart1.DatabaseHelper;

public class AnswerQuestionTester {
	
	private final DatabaseHelper databaseHelper;
	this.databaseHelper = databaseHelper;

    public AnswerQuestionTester(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
 
    }

    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation");

       
      

        System.out.println("____________________________________________________________________________");
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    private static void performTestCase(int testCase, String question, String answer, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Question: \"" + question + "\"");
        System.out.println("Answer: \"" + answer + "\"");
        System.out.println("______________");

        //insert the questions into the database
        databaseHelper.insertQuestion(question);
        
        //ensure the question was inserted
        boolean questionCreated = databaseHelper.searchQuestions(question);
        
        //answer the questions
        databaseHelper.insertAnswer(question, answer);

        //check if the question exists
        boolean questionExists = databaseHelper.searchQuestions(question);
        boolean answerExists = databaseHelper.searchAnswers(question, answer);

        System.out.println();

        if (questionExists && answerExists && questionCreated) {
            if (expectedPass) {
                System.out.println("***Success*** The answer <" + answer + "> was correctly added to the question.");
                numPassed++;
            } else {
                System.out.println("***Failure*** The answer <" + answer + "> was not expected to be found, but it exists!");
                numFailed++;
            }
        } else {
            if (expectedPass) {
                System.out.println("***Failure*** Either the question or answer is missing. Test failed!");
                numFailed++;
            } else {
                System.out.println("***Success*** The answer was not found as expected. Test passed!");
                numPassed++;
            }
        }
    }
}

