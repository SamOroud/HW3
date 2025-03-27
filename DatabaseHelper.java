package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import application.User;
import application.Answer;
import application.Question;
import application.Clarification;
public class DatabaseHelper {
    
    private static final String JDBC_DRIVER = "org.h2.Driver";   
    private static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  
    private static final String USER = "sa"; 
    private static final String PASS = ""; 

    private Connection connection;
    
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            createTables();
            updateTableIfNeed();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "email VARCHAR(255), " 
                + "oneTimePassword VARCHAR(255), "
                + "role VARCHAR(20))");

            statement.execute("CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "date VARCHAR(255), "
                + "isUsed BOOLEAN DEFAULT FALSE)");
            
         //Clarifications Table
            String clarificationsTable = "CREATE TABLE IF NOT EXISTS Clarifications ("
            	    + "id VARCHAR(255) PRIMARY KEY, "
            	    + "question_id VARCHAR(255), "
            	    + "author VARCHAR(255), "
            	    + "content TEXT, "
            	    + "is_addressed BOOLEAN DEFAULT FALSE, "
            	    + "FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE)";
            	statement.execute(clarificationsTable);
            	
         // Questions table
    	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
    	            + "id VARCHAR(255) PRIMARY KEY, "
    	            + "author VARCHAR(255), " 
    	            + "title VARCHAR(500), "
    	            + "content TEXT, "
    	            + "is_answered BOOLEAN DEFAULT FALSE)";
    	    statement.execute(questionsTable);

    	    // Answers table
    	    String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
    	            + "id VARCHAR(255) PRIMARY KEY, "
    	            + "question_id VARCHAR(255), "
    	            + "content TEXT, "
    	            + "author VARCHAR(255), "
    	            + "is_accepted BOOLEAN DEFAULT FALSE, "
    	            + "FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE)";
    	    statement.execute(answersTable);
    	
        }
    }

    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            return resultSet.next() && resultSet.getInt("count") == 0;
        }
    }

    public void register(User user) throws SQLException {
        String query = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }

    public boolean login(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserRole(String userName) {
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("role") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4);
        String formattedDateTime = LocalDateTime.now().plus(Duration.ofMinutes(10))
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String query = "INSERT INTO InvitationCodes (code, date) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.setString(2, formattedDateTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean validateInvitationCode(String code) {
        String query = "SELECT date FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime expiryTime = LocalDateTime.parse(rs.getString("date"),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (LocalDateTime.now().isBefore(expiryTime)) {
                        markInvitationCodeAsUsed(code);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteUser(String userName) {
        if ("admin".equalsIgnoreCase(getUserRole(userName))) {
            System.out.println("Cannot delete admin user.");
            return false;
        }

        String deleteQuery = "DELETE FROM cse360users WHERE userName = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setString(1, userName);
            return deleteStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setOneTimePassword(String userName, String oneTimePassword) {
        if (!doesUserExist(userName)) {
            System.out.println("User does not exist.");
            return;
        }

        String query = "UPDATE cse360users SET oneTimePassword = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, oneTimePassword);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean loginOneTimePassword(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND oneTimePassword = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    clearOneTimePassword(user.getUserName());
                    return true;
                }
            }
        }
        return false;
    }

    public void clearOneTimePassword(String userName) throws SQLException {
        String query = "UPDATE cse360users SET oneTimePassword = NULL WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getAllUsers() {
        String query = "SELECT userName, role, email FROM cse360users";
        StringBuilder usersList = new StringBuilder();
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                usersList.append("Username: ").append(rs.getString("userName"))
                         .append(", Role: ").append(rs.getString("role"))
                         .append(", Email: ").append(rs.getString("email"))
                         .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving users.";
        }

        return usersList.toString().isEmpty() ? "No users found." : usersList.toString();
    }
    
  //Delete Answer
  	public void deleteAnswer(String answerId) {
          try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Answers WHERE id = ?")) {
              pstmt.setString(1, answerId);
              pstmt.executeUpdate();
          } catch (SQLException e) {
              System.err.println("Error deleting answer: " + e.getMessage());
          }
      }
  	
  //Get List of all current questions
  	public List<Question> getAllQuestions() throws SQLException {
  	    List<Question> questions = new ArrayList<>();
  	    String query = "SELECT * FROM Questions";
  	    try (PreparedStatement pstmt = connection.prepareStatement(query);
  	         ResultSet rs = pstmt.executeQuery()) {
  	        while (rs.next()) {
  	            Question question = new Question(
  	                rs.getString("id"),
  	                rs.getString("author"),
  	                rs.getString("title"),
  	                rs.getString("content")
  	            );
  	            // Set the answered status
  	            question.setAnswered(rs.getBoolean("is_answered"));
  	            questions.add(question);
  	        }
  	    }
  	    return questions;
  	}
  	
  	
  //Get List of all Answers
  	public List<Answer> getAllAnswers() throws SQLException {
  	    List<Answer> answers = new ArrayList<>();
  	    String query = "SELECT * FROM Answers";
  	    try (PreparedStatement pstmt = connection.prepareStatement(query);
  	         ResultSet rs = pstmt.executeQuery()) {
  	        while (rs.next()) {
  	        	Answer answer = new Answer(
  	                rs.getString("id"),
  	                rs.getString("question_id"),
  	                rs.getString("content"),
  	                rs.getString("author")
  	            );
  	            answer.setAccepted(rs.getBoolean("is_accepted"));
  	            answers.add(answer);
  	        }
  	    }
  	    return answers;
  	}
  	
  	//Search for Answers
  	public List<Answer> searchAnswers(String searchTerm) throws SQLException {
  	    List<Answer> answers = new ArrayList<>();
  	    String query = "SELECT * FROM Answers WHERE LOWER(content) LIKE ?";
  	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
  	        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
  	        pstmt.setString(1, searchPattern);
  	        
  	        System.out.println(pstmt.toString());
  	        System.out.println("Executing answer search query with pattern: " + searchPattern);
  	        ResultSet rs = pstmt.executeQuery();
  	        
  	        while (rs.next()) {
  	            try {
  	                String id = rs.getString("id");
  	                String questionId = rs.getString("question_id");
  	                String content = rs.getString("content");
  	                String author = rs.getString("author");
  	                
  	                if (id != null && questionId != null) {
  	                    System.out.println("Found answer: ID=" + id + ", QuestionID=" + questionId);
  	                    Answer answer = new Answer(id, questionId, content, author);
  	                    answer.setAccepted(rs.getBoolean("is_accepted"));
  	                    answers.add(answer);
  	                }
  	            } catch (SQLException e) {
  	                System.err.println("Error reading answer from ResultSet: " + e.getMessage());
  	                
  	            }
  	        }
  	        
  	        System.out.println("Total answers found: " + answers.size());
  	        return answers;
  	    }
  	}
  	
  //Get Answer for a Question
  	public List<Answer> getAnswersByQuestionId(String questionId) {
          List<Answer> answers = new ArrayList<>();
          String query = "SELECT * FROM Answers WHERE question_id = ? ORDER BY is_accepted DESC";
          try (PreparedStatement pstmt = connection.prepareStatement(query)) {
              pstmt.setString(1, questionId);
              ResultSet rs = pstmt.executeQuery();
              while (rs.next()) {
                  String id = rs.getString("id");
                  String content = rs.getString("content");
                  String authorId = rs.getString("author");
                  Answer answer = new Answer(id, questionId, content, authorId);
                  answer.setAccepted(rs.getBoolean("is_accepted"));
                  answers.add(answer);
              }
          } catch (SQLException e) {
              System.err.println("Error retrieving answers: " + e.getMessage());
          }
          return answers;
      }
  	
  	//Search through the Questions
  	public List<Question> searchQuestions(String searchTerm) throws SQLException {
  	    List<Question> questions = new ArrayList<>();
  	    String query = "SELECT * FROM Questions WHERE LOWER(title) LIKE ? OR LOWER(content) LIKE ?";
  	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
  	        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
  	        pstmt.setString(1, searchPattern);
  	        pstmt.setString(2, searchPattern);
  	        
  	        ResultSet rs = pstmt.executeQuery();
  	        while (rs.next()) {
  	            Question question = new Question(
  	                rs.getString("id"),
  	                rs.getString("author"),
  	                rs.getString("title"),
  	                rs.getString("content")
  	            );
  	            question.setAnswered(rs.getBoolean("is_answered"));
  	            questions.add(question);
  	        }
  	    }
  	    return questions;
  	}
  	
  //Inserts Question
  	 public void insertQuestion(Question question) throws SQLException {
  		    String query = "INSERT INTO Questions (id, author, title, content, is_answered) VALUES (?, ?, ?, ?, ?)";
  		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
  		        pstmt.setString(1, question.getId());
  		        pstmt.setString(2, question.getAuthor());
  		        pstmt.setString(3, question.getTitle());
  		        pstmt.setString(4, question.getContent());
  		        pstmt.setBoolean(5, question.isAnswered());
  		        pstmt.executeUpdate();
  		    }
  		}
  	 
  	//Add New Answer
 	public void insertAnswer(Answer answer) throws SQLException {
 	    String query = "INSERT INTO Answers (id, question_id, content, author, is_accepted)"
 	                 + "VALUES (?, ?, ?, ?, ?)";
 	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
 	        pstmt.setString(1, answer.getId());
 	        pstmt.setString(2, answer.getQuestionId());
 	        pstmt.setString(3, answer.getContent());
 	        pstmt.setString(4, answer.getAuthor());
 	        pstmt.setBoolean(5, answer.isAccepted());
 	        pstmt.executeUpdate(); 
 	    }
 	}
  	 
  	//Gets Question ID
 	public Question getQuestionById(String questionId) throws SQLException {
 	    String query = "SELECT * FROM Questions WHERE id = ?";
 	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
 	        pstmt.setString(1, questionId);
 	        System.out.println("Looking up question with ID: " + questionId);
 	        
 	        ResultSet rs = pstmt.executeQuery();
 	        if (rs.next()) {
 	            Question question = new Question(
 	                rs.getString("id"),
 	                rs.getString("author"),
 	                rs.getString("title"),
 	                rs.getString("content")
 	            );
 	            question.setAnswered(rs.getBoolean("is_answered"));
 	            System.out.println("Found question: " + question.getTitle());
 	            return question;
 	        } else {
 	            System.out.println("No question found with ID: " + questionId);
 	        }
 	    }
 	    return null;
 	}
 	
 	//Edit Existing Answer
 	public void updateAnswer(Answer answer) {
 	    String query = "UPDATE Answers SET content = ?, is_accepted = ? WHERE id = ?";
 	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
 	        pstmt.setString(1, answer.getContent());
 	        pstmt.setBoolean(2, answer.isAccepted());
 	        pstmt.setString(3, answer.getId());
 	        pstmt.executeUpdate();
 	    } catch (SQLException e) {
 	        System.err.println("Error updating answer: " + e.getMessage());
 	    }
 	}
 	
 	//Set all answers for a question to not be accepted
 	public void resetAcceptedAnswers(String questionId) {
 	    String query = "UPDATE Answers SET is_accepted = FALSE WHERE question_id = ?";
 	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
 	        pstmt.setString(1, questionId);
 	        pstmt.executeUpdate();
 	    } catch (SQLException e) {
 	        System.err.println("Error resetting accepted answers: " + e.getMessage());
 	    }
 	}
 	
 	//Update Question
 	public void updateQuestion(Question question) {
 	    String query = "UPDATE Questions SET title = ?, content = ?, is_answered = ? WHERE id = ?";
 	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
 	        pstmt.setString(1, question.getTitle());
 	        pstmt.setString(2, question.getContent());
 	        pstmt.setBoolean(3, question.isAnswered());
 	        pstmt.setString(4, question.getId());
 	        pstmt.executeUpdate();
 	    } catch (SQLException e) {
 	        System.err.println("Error updating question: " + e.getMessage());
 	    }
 	}
 	
 	  //Mark an answer as accepted and update the question status
 	public void markAnswerAsAccepted(String answerId, String questionId) {
 	    try {
 	        // First reset all answers for this question
 	        resetAcceptedAnswers(questionId);
 	        
 	        // Then mark the specific answer as accepted
 	        String updateAnswerQuery = "UPDATE Answers SET is_accepted = TRUE WHERE id = ?";
 	        try (PreparedStatement pstmt = connection.prepareStatement(updateAnswerQuery)) {
 	            pstmt.setString(1, answerId);
 	            pstmt.executeUpdate();
 	        }
 	        
 	        // Finally, mark the question as answered
 	        String updateQuestionQuery = "UPDATE Questions SET is_answered = TRUE WHERE id = ?";
 	        try (PreparedStatement pstmt = connection.prepareStatement(updateQuestionQuery)) {
 	            pstmt.setString(1, questionId);
 	            pstmt.executeUpdate();
 	        }
 	    } catch (SQLException e) {
 	        System.err.println("Error marking answer as accepted: " + e.getMessage());
 	    }
 	}
 	
 	//Delete Question
  	public void deleteQuestion(String questionId) {
  	    try {
  	        // The database is set up with ON DELETE CASCADE, so deleting the question
  	        // should automatically delete all associated answers
  	        String query = "DELETE FROM Questions WHERE id = ?";
  	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
  	            pstmt.setString(1, questionId);
  	            int rowsAffected = pstmt.executeUpdate();
  	            if (rowsAffected > 0) {
  	                System.out.println("Question with ID " + questionId + " deleted successfully");
  	            } else {
  	                System.out.println("No question found with ID " + questionId);
  	            }
  	        }
  	    } catch (SQLException e) {
  	        System.err.println("Error deleting question: " + e.getMessage());
  	        throw new RuntimeException("Failed to delete question", e);
  	    }
  	}
  	
    //Clarifications/Comments associated 
    public void addClarificationRequest(String id, String questionId, String author, String content) throws SQLException {
        String query = "INSERT INTO Clarifications (id, question_id, author, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, questionId);
            pstmt.setString(3, author);
            pstmt.setString(4, content);
            pstmt.executeUpdate();
        }
    }

    public List<Clarification> getClarificationRequests(String questionId) throws SQLException {
        List<Clarification> requests = new ArrayList<>();
        String query = "SELECT * FROM Clarifications WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Clarification request = new Clarification(
                    rs.getString("id"),
                    rs.getString("question_id"),
                    rs.getString("author"),
                    rs.getString("content")
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving clarification: " + e.getMessage());
            throw e; // Re-throw the exception
        }
        return requests;
    }
    // Mark a clarification request as addressed
    public void markClarificationAddressed(String id, boolean addressed) throws SQLException {
        String query = "UPDATE Clarifications SET is_addressed = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, addressed);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating clarification status: " + e.getMessage());
        }
    }

    // Delete a clarification request
    public void deleteClarificationRequest(String id) {
        String query = "DELETE FROM Clarifications WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting clarification request: " + e.getMessage());
        }
    }
  
  //UpdatesTable if Needed
    private void updateTableIfNeed() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        

        boolean isAnsweredExists = false;
        try (ResultSet columns = metaData.getColumns(null, null, "QUESTIONS", "IS_ANSWERED")) {
            isAnsweredExists = columns.next();
        }
        

        if (!isAnsweredExists) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE Questions ADD COLUMN is_answered BOOLEAN DEFAULT FALSE");
                System.out.println("Added is_answered column to Questions table");
            } catch (SQLException e) {
                System.err.println("Error adding answered column: " + e.getMessage());
            }
        }
        
 
        boolean isAcceptedExists = false;
        try (ResultSet columns = metaData.getColumns(null, null, "ANSWERS", "IS_ACCEPTED")) {
            isAcceptedExists = columns.next();
        }
        
 
        if (!isAcceptedExists) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE Answers ADD COLUMN is_accepted BOOLEAN DEFAULT FALSE");
                System.out.println("Added is_accepted column to Answers table");
            } catch (SQLException e) {
                System.err.println("Error adding accepted column: " + e.getMessage());
            }
        }
    }
}