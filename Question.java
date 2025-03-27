package application;

public class Question {
    private final String id;
    private String author;
    private String title;
    private String content;
    private boolean answered;  // Added for the answer marking feature

    public Question(String id, String author, String title, String content) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty ID field");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty title");
        }
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty question field");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty author field");
        }

        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.answered = false;
    }

    // Validation: Ensures fields are non-empty
    public boolean validate() {
        return author != null && !author.trim().isEmpty() &&
               title != null && !title.trim().isEmpty() &&
               content != null && !content.trim().isEmpty();
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title;
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Question field cannot be empty");
        }
        this.content = content;
    }
    
    // Added getter and setter for the answered status
    public boolean isAnswered() { return answered; }
    public void setAnswered(boolean answered) { this.answered = answered; }

    @Override
    public String toString() {
        return "Question: " + id +
               "\nAuthor: " + author +
               "\nTitle: " + title +
               "\nContent: " + content +
               (answered ? "\n[ANSWERED]" : "");
    }
}
