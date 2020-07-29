package com.game.trivia.repository.model;

import java.util.List;

public class Question {
    private String questionId;
    private List<Statistic> stats;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List<Statistic> getStats() {
        return stats;
    }

    public void setStats(List<Statistic> stats) {
        this.stats = stats;
    }
}
