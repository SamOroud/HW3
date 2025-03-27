package application;


public class Clarification {
    private final String id;
    private final String questionId;
    private final String author;
    private String content;
    private boolean addressed;
    
    public Clarification(String id, String questionId, String author, String content) {
        this.id = id;
        this.questionId = questionId;
        this.author = author;
        this.content = content;
        this.addressed = false;
    }
    
   
    public Clarification(String id, String questionId, String author, String content, boolean addressed) {
        this.id = id;
        this.questionId = questionId;
        this.author = author;
        this.content = content;
        this.addressed = addressed;
    }
    
    // Getters
    public String getId() { return id; }
    public String getQuestionId() { return questionId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public boolean isAddressed() { return addressed; }
    
    // Setters
    public void setContent(String content) { this.content = content; }
    public void setAddressed(boolean addressed) { this.addressed = addressed; }
    
    @Override
    public String toString() {
        return "Clarification Request by " + author + 
               " on Question " + questionId + 
               ": " + content + 
               (addressed ? " (Addressed)" : " (Pending)");
    }
}
