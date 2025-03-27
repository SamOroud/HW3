package application;

import java.util.*;

public class Questions {
    private final Map<String, Question> questionMap;
    private final Map<String, List<String>> relatedQuestions;
    private final Map<String, Integer> unreadMessageCount;

    public Questions() {
        this.questionMap = new HashMap<>();
        this.relatedQuestions = new HashMap<>();
        this.unreadMessageCount = new HashMap<>();
    }

    public void addQuestion(Question question) {
        if (question == null || questionMap.containsKey(question.getId())) {
            throw new IllegalArgumentException("Invalid or duplicate question.");
        }
        questionMap.put(question.getId(), question);
        unreadMessageCount.put(question.getId(), 0);
    }

    public Question getQuestion(String id) {
        return questionMap.get(id);
    }

 
    public List<Question> searchQuestionsByKeyword(String keyword) {
        List<Question> result = new ArrayList<>();
        for (Question q : questionMap.values()) {
            if (q.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                q.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(q);
            }
        }
        return result;
    }

    public void removeQuestion(String id) {
        questionMap.remove(id);
        relatedQuestions.remove(id);
        unreadMessageCount.remove(id);
    }

    @Override
    public String toString() {
        return "Questions:\n" + questionMap.values();
    }
}
