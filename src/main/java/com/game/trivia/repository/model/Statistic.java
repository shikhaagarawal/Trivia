package com.game.trivia.repository.model;

public class Statistic {

    private String correctAnswer;
    private String choice;
    private int playerCount;

    public Statistic(String correctAnswer, String choice, int playerCount){
        this.correctAnswer = correctAnswer;
        this.choice = choice;
        this.playerCount = playerCount;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
