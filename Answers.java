package application;

import java.util.*;

public class Answers {
    private final Map<String, List<Answer>> answersMap;  // Maps question ID to list of answers

    public Answers() {
        this.answersMap = new HashMap<>();
    }

    public void addAnswer(Answer answer) {
        if (answer == null || answer.getQuestionId() == null) {
            throw new IllegalArgumentException("Invalid answer.");
        }
        answersMap.computeIfAbsent(answer.getQuestionId(), k -> new ArrayList<>()).add(answer);
    }

    public List<Answer> getAnswersForQuestion(String questionId) {
        return answersMap.getOrDefault(questionId, new ArrayList<>());
    }

    public void removeAnswer(String questionId, String answerId) {
        List<Answer> answerList = answersMap.get(questionId);
        if (answerList != null) {
            answerList.removeIf(a -> a.getId().equals(answerId));
        }
    }
    
    public List<Answer> searchAnswersForQuestion(String questionId, String keyword) {
        return answersMap.getOrDefault(questionId, new ArrayList<>())
                         .stream()
                         .filter(a -> a.getContent().toLowerCase().contains(keyword.toLowerCase()))
                         .toList();
    }
    

    @Override
    public String toString() {
        return "Answers: " + answersMap;
    }
}
