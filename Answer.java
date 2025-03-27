package application;

public class Answer {
    private final String id;
    private final String questionId;
    private String content;
    private String author;
    private boolean accepted;  // Added for the answer marking feature

    public Answer(String id, String questionId, String content, String author) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty ID field");
        }
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty answer field");
        }
        if (questionId == null || questionId.isEmpty()) {
            throw new IllegalArgumentException("Question ID cannot be empty");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty Author field");
        }

        this.id = id;
        this.questionId = questionId;
        this.author = author;
        this.content = content;
        this.accepted = false;
    }

    // Ensures the answer has valid data
    public boolean validate() {
        return author != null && !author.trim().isEmpty() &&
               content != null && !content.trim().isEmpty();
    }

    public String getId() { return id; }
    public String getQuestionId() { return questionId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty");
        }
        this.content = content;
    }
    
    // Added getter and setter for the accepted status
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    @Override
    public String toString() {
        return "Answer: " + id +
               "\nQuestion ID: " + questionId +
               "\nAuthor: " + author +
               "\nContent: " + content +
               (accepted ? "\n[ACCEPTED ANSWER]" : "");
    }
}
