package com.game.trivia.repository.model;

public class Statistic {

    private String choice;
    private int playerCount;

    public Statistic(String choice, int playerCount) {
        this.choice = choice;
        this.playerCount = playerCount;
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
