package application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;

public class QAHomePage {
    private final DatabaseHelper databaseHelper;
    private final User user;
    private Label selectedQuestionTitle;
    private Label selectedQuestionText;
    private VBox answersList;
    private Question selectedQuestion;
    private ListView<Question> searchResultsListView;
    private VBox clarificationsVBox;
    private Tab clarificationsTab;
    private Button testDeleteButton;
    private HBox buttonBox;

    public QAHomePage(DatabaseHelper databaseHelper, User user) {
    	this.databaseHelper = databaseHelper;
        this.user = user;

        this.selectedQuestionTitle = new Label("Question");
        this.selectedQuestionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        this.selectedQuestionText = new Label("Question Content");
        this.selectedQuestionText.setWrapText(true);
        this.selectedQuestionText.setStyle(
            "-fx-padding: 10px; " +
            "-fx-border-color: gray; " +
            "-fx-border-radius: 3px; " +
            "-fx-prompt-text-fill: gray; " +
            "-fx-background-color: white;"
        );

        this.answersList = new VBox(10);
        this.answersList.setStyle(
            "-fx-border-color: gray; " +
            "-fx-padding: 10; " +
            "-fx-background-color: white;"
        );
        this.answersList.setPrefWidth(400);
        this.answersList.setMinWidth(400);

        this.buttonBox = new HBox(10);
        this.buttonBox.setAlignment(Pos.CENTER);
        this.buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button createQuestion = new Button("Create Question");
        createQuestion.setOnAction(e -> openCreateQuestionPopup());
      
        Button createAnswer = new Button("Create Answer");
        createAnswer.setOnAction(e -> openCreateAnswerPopup());
        
        this.buttonBox.getChildren().addAll(createQuestion, createAnswer);
    }

    public void show(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Left panel for search functionality
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new javafx.geometry.Insets(10));

        VBox searchArea = new VBox(10);
        searchArea.setAlignment(Pos.TOP_CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Search answers and questions...");
        searchField.setPrefWidth(400);

        // Centered Buttons HBox
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button searchQuestionsBtn = new Button("Search Questions");
        Button searchAnswersBtn = new Button("Search Answers");
        Button backButton = new Button("Back");

        searchQuestionsBtn.setOnAction(e -> searchQuestions(searchField.getText()));
        searchAnswersBtn.setOnAction(e -> searchAnswers(searchField.getText()));
        
        //Back button
        backButton.setOnAction(e -> {
            StudentHomePage studentHomePage = new StudentHomePage(databaseHelper, user);
            studentHomePage.show(primaryStage);
        });
        buttonsBox.getChildren().addAll(searchQuestionsBtn, searchAnswersBtn);

        searchArea.getChildren().addAll(searchField, buttonsBox);
        searchResultsListView = new ListView<>();
        searchResultsListView.setPrefHeight(250);
        searchResultsListView.setCellFactory(lv -> new ListCell<Question>() {  	
        	
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        searchResultsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectedQuestion = newVal;
                    updateQuestionDisplay();
                    updateAnswersList();
                }
            });

      
        leftPanel.getChildren().addAll(
            searchArea,
            new Label("Search Results:"), 
            searchResultsListView
        );
        
        HBox backButtonContainer = new HBox();
        backButtonContainer.setPadding(new Insets(0, 0, 15, 20)); 
        backButtonContainer.getChildren().add(backButton);
        

        BorderPane leftPanelWithBackButton = new BorderPane();
        leftPanelWithBackButton.setCenter(leftPanel);
        leftPanelWithBackButton.setBottom(backButtonContainer);

        // Right panel setup
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new javafx.geometry.Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPrefWidth(420);  
        VBox.setMargin(rightPanel, new Insets(35, 0, 0, 0));

        selectedQuestionTitle = new Label("Question");
        selectedQuestionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        selectedQuestionText = new Label("");
        selectedQuestionText.setWrapText(true);
        selectedQuestionText.setStyle(
            "-fx-padding: 10px; " +
            "-fx-border-color: gray; " +
            "-fx-border-radius: 3px; " +
            "-fx-prompt-text-fill: gray; " +
            "-fx-background-color: white;"
        );
        selectedQuestionText.setText("Question Content");
        
        selectedQuestionText.setStyle(selectedQuestionText.getStyle() + "-fx-text-fill: gray;");
        
        answersList = new VBox(10);
        answersList.setStyle(
            "-fx-border-color: gray; " +
            "-fx-padding: 10; " +
            "-fx-background-color: white;"
        );
        answersList.setPrefWidth(400);
        answersList.setMinWidth(400);
        
        VBox placeholderContent = new VBox(5);

        Label authorPlaceholder = new Label("Author...");
        authorPlaceholder.setStyle(
            "-fx-text-fill: gray; " +
            "-fx-font-style: italic;"
        );

        Label answerPlaceholder = new Label("Answer...");
        answerPlaceholder.setStyle(
            "-fx-text-fill: gray; " +
            "-fx-font-style: italic;"
        );
        
        placeholderContent.getChildren().addAll(authorPlaceholder, answerPlaceholder);
        answersList.getChildren().add(placeholderContent);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
 
        buttonBox.getChildren().clear();
        Button createQuestion = new Button("Create Question");
        createQuestion.setOnAction(e -> openCreateQuestionPopup());
      
        Button createAnswer = new Button("Create Answer");
        createAnswer.setOnAction(e -> openCreateAnswerPopup());
        
        buttonBox.getChildren().addAll(createAnswer, createQuestion);

        TabPane rightTabPane = new TabPane();
        
        Tab questionsTab = new Tab("Questions");
        VBox questionsContent = new VBox(10);
        
        if (testDeleteButton == null) {
            testDeleteButton = new Button("Delete Selected Question");
            testDeleteButton.setStyle("-fx-background-color: #880000; -fx-text-fill: white;");
            testDeleteButton.setPrefWidth(200);
            testDeleteButton.setOnAction(e -> {
                if (selectedQuestion != null) {
                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDialog.setTitle("Confirm Delete");
                    confirmDialog.setHeaderText("Delete Question");
                    confirmDialog.setContentText("Are you sure you want to delete this question and all its answers?");
                    
                    confirmDialog.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                databaseHelper.deleteQuestion(selectedQuestion.getId());
                                
                                Platform.runLater(() -> {
                                    try {
                                        List<Question> questions = databaseHelper.getAllQuestions();
                                        searchResultsListView.getItems().clear();
                                        searchResultsListView.getItems().addAll(questions);
                                        
                                        selectedQuestion = null;
                                        updateQuestionDisplay();
                                        updateAnswersList();
                                    } catch (SQLException ex) {
                                        showError("Error refreshing questions: " + ex.getMessage());
                                    }
                                });
                            } catch (Exception ex) {
                                showError("Error deleting question: " + ex.getMessage());
                            }
                        }
                    });
                } else {
                    showError("Please select a question first");
                }
            });
        }
        
        questionsContent.getChildren().addAll(
            selectedQuestionTitle,
            new Separator(),
            answersList,          
            buttonBox
        );
        questionsTab.setContent(questionsContent);

        clarificationsTab = new Tab("Comments");
        clarificationsVBox = new VBox(10);
        clarificationsVBox.setPadding(new Insets(10));
        clarificationsTab.setContent(clarificationsVBox);
        
        rightTabPane.getTabs().addAll(questionsTab, clarificationsTab);
        
        Button addCommentButton = new Button("Add Comment");
        addCommentButton.setOnAction(e -> openAddClarificationPopup());
        
        HBox addCommentContainer = new HBox(addCommentButton);
        addCommentContainer.setAlignment(Pos.CENTER);
        addCommentContainer.setPadding(new Insets(10, 0, 0, 0));
        
        VBox rightPanelContainer = new VBox(10);
        rightPanelContainer.getChildren().addAll(rightTabPane, addCommentContainer);
        
        root.setCenter(rightPanelContainer);
        
        searchResultsListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedQuestion = newVal;
                        updateQuestionDisplay();
                        updateAnswersList();
                        displayClarifications();
                    }
                });
        
        root.setLeft(leftPanelWithBackButton);
        root.setCenter(rightPanel);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Q&A Home Page");
        
        Platform.runLater(() -> {
            try {
                List<Question> questions = databaseHelper.getAllQuestions();
                searchResultsListView.getItems().clear();
                searchResultsListView.getItems().addAll(questions);
            } catch (SQLException e) {
                showError("Error loading questions: " + e.getMessage());
            }
        });
        
//        if (testDeleteButton != null) {
//            questionsContent.getChildren().add(testDeleteButton);
//        }

        questionsTab.setContent(questionsContent);
        
        rightPanel.getChildren().addAll(selectedQuestionTitle, selectedQuestionText, answersList, buttonBox, testDeleteButton,  addCommentButton, clarificationsVBox);
        
        if (selectedQuestion != null) {
        	rightPanel.getChildren().add(0, new Label(selectedQuestion.getTitle()));
        }
        
        primaryStage.show();
    }

    private void searchQuestions(String query) {
        try {
            List<Question> results = databaseHelper.searchQuestions(query);
            Platform.runLater(() -> {
                searchResultsListView.getItems().clear();
                searchResultsListView.getItems().addAll(results);
            });
        } catch (SQLException e) {
            showError("Error searching questions: " + e.getMessage());
        }
    }

    private void searchAnswers(String query) {
        try {
            
            List<Answer> results = new ArrayList<>();
            for (Answer answer : databaseHelper.getAllAnswers()) {
            	if (answer.getContent().toLowerCase().contains(query.toLowerCase())) {
            		results.add(answer);
            	}
            }
            
            if (results == null || results.isEmpty()) {
                Platform.runLater(() -> {
                    searchResultsListView.getItems().clear();
                    showError("No answers found matching your search");
                });
                return;
            }
            
            System.out.println("Found " + results.size() + " matching answers");
            
            Set<String> questionIds = results.stream()
                .map(Answer::getQuestionId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());
                
            if (questionIds.isEmpty()) {
                Platform.runLater(() -> {
                    searchResultsListView.getItems().clear();
                    showError("No valid questions found for the matching answers");
                });
                return;
            }
            
            System.out.println("Unique question IDs found: " + questionIds);
            
            List<Question> questions = new ArrayList<>();
            for (String qId : questionIds) {
                System.out.println("Looking up question with ID: " + qId);
                Question question = databaseHelper.getQuestionById(qId);
                if (question != null) {
                    questions.add(question);
                    System.out.println("Found question: " + question.getTitle());
                } else {
                    System.out.println("No question found for ID: " + qId);
                }
            }
            
            final List<Question> finalQuestions = questions;
            
            Platform.runLater(() -> {
                searchResultsListView.getItems().clear();
                if (finalQuestions.isEmpty()) {
                    System.out.println("No questions found to display");
                    showError("No questions found with matching answers");
                } else {
                    System.out.println("Displaying " + finalQuestions.size() + " questions");
                    searchResultsListView.getItems().addAll(finalQuestions);
                }
            });
            
        } catch (SQLException e) {
            System.err.println("SQL Error during search: " + e.getMessage());
            Platform.runLater(() -> {
                showError("Error searching answers: " + e.getMessage());
            });
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                showError("An unexpected error occurred: " + e.getMessage());
            });
        }
    }

    private void updateQuestionDisplay() {
        if (selectedQuestion != null) {
        	 selectedQuestionTitle.setText(selectedQuestion.getTitle());
             selectedQuestionText.setText(selectedQuestion.getContent());
        } else {
            selectedQuestionTitle.setText("Question");
            selectedQuestionText.setText("Question Content");
            selectedQuestionText.setStyle(
                "-fx-padding: 10px; " +
                "-fx-border-color: gray; " +
                "-fx-border-radius: 3px; " +
                "-fx-background-color: white; " +
                "-fx-text-fill: gray;"  
            );
            selectedQuestionText.setOnMouseClicked(null);
        }
    }  
    
    private void openCreateQuestionPopup() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Create New Question");

        VBox form = new VBox(10);
        form.setPadding(new javafx.geometry.Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Enter title...");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter details...");
        contentArea.setPrefRowCount(5);

        Button createBtn = new Button("Create Question");
        createBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            
            if (title.isEmpty() || content.isEmpty()) {
                showError("Both a title and its contents are required");
                return;
            }

            try {
                Question newQuestion = new Question(
                    UUID.randomUUID().toString(),
                    user.getUserName(),
                    title,
                    content
                );
                
                databaseHelper.insertQuestion(newQuestion);
                searchQuestions("");
                popup.close();
            } catch (SQLException ex) {
                showError("Problem creating question: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
            new Label("Question Title:"),
            titleField,
            new Label("Question Content:"),
            contentArea,
            createBtn
        );

        Scene scene = new Scene(form, 500, 400);
        popup.setScene(scene);
        popup.show();
    }
    
    private void openCreateAnswerPopup() {
        if (selectedQuestion == null) {
            showError("Please first select a question");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Create Answer");

        VBox form = new VBox(10);
        form.setPadding(new javafx.geometry.Insets(20));

        TextArea textArea = new TextArea();
        textArea.setPromptText("Type your answer here...");

        Button createBtn = new Button("Create");
        createBtn.setOnAction(e -> {
            String content = textArea.getText();
            if (!content.isEmpty()) {
                try {
                    Answer newAnswer = new Answer(
                        UUID.randomUUID().toString(),
                        selectedQuestion.getId(),
                        content,
                        user.getUserName()
                    );
                    databaseHelper.insertAnswer(newAnswer);
                    updateAnswersList();
                    popup.close();
                } catch (SQLException ex) {
                    showError("Problem creating answer: " + ex.getMessage());
                }
            }
        });

        form.getChildren().addAll(new Label("Answer:"), textArea, createBtn);
        popup.setScene(new Scene(form, 400, 300));
        popup.show();
    }

    private void updateAnswersList() {
        Platform.runLater(() -> {
            answersList.getChildren().clear();
            
            // Check if no question is selected or question has no ID
            if (selectedQuestion == null || selectedQuestion.getId() == null) {
                addPlaceholderContent();
                return;
            }

            List<Answer> answers = databaseHelper.getAnswersByQuestionId(selectedQuestion.getId());

            // If no answers, add placeholder
            if (answers.isEmpty()) {
                addPlaceholderContent();
            } else {
                // Sort answers: accepted answers first
                List<Answer> sortedAnswers = new ArrayList<>(answers);
                sortedAnswers.sort((a1, a2) -> {
                    if (a1.isAccepted() && !a2.isAccepted()) return -1;
                    if (!a1.isAccepted() && a2.isAccepted()) return 1;
                    return 0;
                });

                // Create answer cards
                sortedAnswers.forEach(this::createAnswerCard);
            }
        });
    }

    private void addPlaceholderContent() {
        VBox placeholderContent = new VBox(5);
        
        Label answerPlaceholder = new Label("Answer...");
        answerPlaceholder.setStyle(
            "-fx-text-fill: gray; " +
            "-fx-font-style: italic;"
        );
        
        Label authorPlaceholder = new Label("Author...");
        authorPlaceholder.setStyle(
            "-fx-text-fill: gray; " +
            "-fx-font-style: italic;"
        );
        
        placeholderContent.getChildren().addAll(authorPlaceholder,answerPlaceholder);
        answersList.getChildren().add(placeholderContent);
    }

    private void createAnswerCard(Answer answer) {
        VBox answerCard = new VBox(5);
        
        // Check if this answer is for an answered question
        boolean isAnsweredQuestion = selectedQuestion != null && selectedQuestion.isAnswered();
        boolean isFirstAnswer = false;
        
        if (selectedQuestion != null) {
            List<Answer> answers = databaseHelper.getAnswersByQuestionId(selectedQuestion.getId());
            isFirstAnswer = !answers.isEmpty() && answers.get(0).getId().equals(answer.getId());
        }
        
        if (isAnsweredQuestion && isFirstAnswer) {
            answerCard.setStyle(
                "-fx-border-color: gold; " + 
                "-fx-border-width: 2; " +
                "-fx-padding: 10; " + 
                "-fx-background-color: lightyellow; " +
                "-fx-cursor: hand;"
            );
            
            Label answeredLabel = new Label("*Answered*");
            answeredLabel.setStyle("-fx-text-fill: goldenrod; -fx-font-weight: bold;");
            
            Label authorLabel = new Label("Author: " + answer.getAuthor());
            authorLabel.setStyle("-fx-font-weight: bold;");
            
            Label contentLabel = new Label(answer.getContent());
            contentLabel.setWrapText(true);
            
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            
            if (user.getUserName().equals(answer.getAuthor())) {
                Button editBtn = new Button("Edit");
                editBtn.setOnAction(e -> openEditAnswerPopup(answer));
                
                Button deleteBtn = new Button("Delete");
                deleteBtn.setOnAction(e -> {
                    databaseHelper.deleteAnswer(answer.getId());
                    updateAnswersList();
                });
                
                buttons.getChildren().addAll(editBtn, deleteBtn);
            }
            
            answerCard.getChildren().addAll(answeredLabel, authorLabel, contentLabel, buttons);
        } else {
            answerCard.setStyle(
                "-fx-border-color: lightgray; " + 
                "-fx-padding: 10; " + 
                "-fx-background-color: white; " +
                "-fx-cursor: hand;"
            );
            
            Label authorLabel = new Label("Author: " + answer.getAuthor());
            authorLabel.setStyle("-fx-font-weight: bold;");
            
            Label contentLabel = new Label(answer.getContent());
            contentLabel.setWrapText(true);
            
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            
            if (user.getUserName().equals(answer.getAuthor())) {
                Button editBtn = new Button("Edit");
                editBtn.setOnAction(e -> openEditAnswerPopup(answer));
                
                Button deleteBtn = new Button("Delete");
                deleteBtn.setOnAction(e -> {
                    databaseHelper.deleteAnswer(answer.getId());
                    updateAnswersList();
                });
                
                buttons.getChildren().addAll(editBtn, deleteBtn);
            }
            
            answerCard.getChildren().addAll(authorLabel, contentLabel, buttons);
        }
        
        answerCard.setOnMouseClicked(e -> {
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Answer by " + answer.getAuthor());
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            
            Label titleLabel = new Label("Answer to: " + selectedQuestion.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            Label answerAuthorLabel = new Label("Posted by: " + answer.getAuthor());
            answerAuthorLabel.setStyle("-fx-font-weight: bold;");
            
            TextArea answerContent = new TextArea(answer.getContent());
            answerContent.setWrapText(true);
            answerContent.setEditable(false);
            answerContent.setPrefHeight(200);
            
            HBox buttonsBox = new HBox(10);
            buttonsBox.setAlignment(Pos.CENTER);
            
            Button closeButton = new Button("Close");
            closeButton.setOnAction(event -> popup.close());
            
            Button markAsAnswerButton = new Button("Answers Question");
            markAsAnswerButton.setOnAction(event -> {
                if (selectedQuestion != null) {
                    selectedQuestion.setAnswered(true);
                    try {
                        databaseHelper.updateQuestion(selectedQuestion);
                        answer.setAccepted(true);
                        databaseHelper.updateAnswer(answer);
                        updateAnswersList();
                        popup.close();
                    } catch (Exception ex) {
                        showError("Error marking answer as accepted: " + ex.getMessage());
                    }
                }
            });
            
            buttonsBox.getChildren().addAll(closeButton, markAsAnswerButton);
            content.getChildren().addAll(titleLabel, answerAuthorLabel, answerContent, buttonsBox);
            
            Scene scene = new Scene(content, 500, 300);
            popup.setScene(scene);
            popup.show();
        });
        
        answersList.getChildren().add(answerCard);
    }

    private void openEditAnswerPopup(Answer answer) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Edit Answer");

        VBox form = new VBox(10);
        form.setPadding(new javafx.geometry.Insets(20));

        TextArea textArea = new TextArea(answer.getContent());
        
        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            String newContent = textArea.getText();
            if (!newContent.isEmpty()) {
                answer.setContent(newContent);
                databaseHelper.updateAnswer(answer);
                updateAnswersList();
                popup.close();
            }
        });

        form.getChildren().addAll(new Label("Edit Answer:"), textArea, saveBtn);
        popup.setScene(new Scene(form, 400, 300));
        popup.show();
    }

    private void displayClarifications() {
        if (clarificationsVBox == null) {
            clarificationsVBox = new VBox(10);
            clarificationsVBox.setPadding(new Insets(10));
        }

        if (selectedQuestion == null) {
            clarificationsVBox.getChildren().clear();
            Label noQuestionLabel = new Label("No question selected");
            clarificationsVBox.getChildren().add(noQuestionLabel);
            return;
        }

        Platform.runLater(() -> {
            clarificationsVBox.getChildren().clear();

            try {
                List<Clarification> clarifications = databaseHelper.getClarificationRequests(selectedQuestion.getId());

                if (clarifications == null || clarifications.isEmpty()) {
                    Label noCommentsLabel = new Label("No comments yet");
                    clarificationsVBox.getChildren().add(noCommentsLabel);
                } else {
                    for (Clarification clarification : clarifications) {
                        if (clarification != null) {
                            VBox clarificationBox = createClarificationView(clarification);
                            if (clarificationBox != null) {
                                clarificationsVBox.getChildren().add(clarificationBox);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                showError("Error loading comments: " + e.getMessage());
            }
        });
    }
    
    private void openClarificationDetailPopup(Clarification clarification) {
    	Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Clarification Details");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label authorLabel = new Label("By: " + clarification.getAuthor());
        Label contentLabel = new Label(clarification.getContent());
        contentLabel.setWrapText(true);

        if (selectedQuestion != null &&
            (user.getUserName().equals(selectedQuestion.getAuthor()) ||
             "admin".equalsIgnoreCase(user.getRole()))) {

            Button markAddressedButton = new Button(
                clarification.isAddressed() ? "Mark as Unaddressed" : "Mark as Addressed"
            );

            markAddressedButton.setOnAction(e -> {
                try {
                    boolean newStatus = !clarification.isAddressed();
                    databaseHelper.markClarificationAddressed(clarification.getId(), newStatus);

                    clarification.setAddressed(newStatus);
                    

                    displayClarifications();

                    popup.close();
                } catch (SQLException ex) {
                    showError("Error updating clarification status: " + ex.getMessage());
                }
            });

            content.getChildren().addAll(authorLabel, contentLabel, markAddressedButton);
        } else {
            content.getChildren().addAll(authorLabel, contentLabel);
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popup.close());
        content.getChildren().add(closeButton);

        Scene scene = new Scene(content, 400, 300);
        popup.setScene(scene);
        popup.show();
    }
    
    private VBox createClarificationView(Clarification clarification) {
    	 if (clarification == null) {
    	        return null;
    	    }

    	    VBox box = new VBox(5);
    	    box.setStyle(
    	        "-fx-border-color: lightgray; " +
    	        "-fx-border-width: 1; " +
    	        "-fx-padding: 10; " +
    	        "-fx-background-color: white; " +
    	        "-fx-cursor: hand;"
    	    );

    	    Label authorLabel = new Label(clarification.getAuthor() != null ? clarification.getAuthor() : "Unknown");
    	    authorLabel.setStyle("-fx-font-weight: bold;");

    	    Label contentLabel = new Label(clarification.getContent() != null ? clarification.getContent() : "");
    	    contentLabel.setWrapText(true);

    	    box.getChildren().addAll(authorLabel, contentLabel);
    	    box.setOnMouseClicked(e -> openClarificationDetailPopup(clarification));
    	    return box;
    	}
    
    private void openAddClarificationPopup() {
        if (selectedQuestion == null) {
            showError("Please select a question first");
            return;
        }
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add Comment");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextArea clarificationTextArea = new TextArea();
        clarificationTextArea.setPromptText("Enter your comment...");
        clarificationTextArea.setPrefRowCount(5);

        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            String content = clarificationTextArea.getText().trim();
            
            if (content.isEmpty()) {
                showError("Comment cannot be empty");
                return;
            }
            try {
                String clarificationId = UUID.randomUUID().toString();
                
                databaseHelper.addClarificationRequest(
                    clarificationId, 
                    selectedQuestion.getId(), 
                    user.getUserName(), 
                    content
                );
                displayClarifications();
                popup.close();
            } catch (SQLException ex) {
                showError("Problem adding comment: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
            new Label("Add Comment:"),
            clarificationTextArea,
            doneButton
        );

        Scene scene = new Scene(form, 400, 300);
        popup.setScene(scene);
        popup.show();
    }
    
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
