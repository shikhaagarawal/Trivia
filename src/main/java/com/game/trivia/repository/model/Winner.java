package com.game.trivia.repository.model;

public class Winner {
    private int totalPlayers;
    private long totalLevels;
    private boolean winner;

    public Winner(int totalPlayers,long totalLevels,boolean winner){
        this.totalLevels = totalLevels;
        this.totalPlayers = totalPlayers;
        this.winner = winner;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public long getTotalLevels() {
        return totalLevels;
    }

    public void setTotalLevels(long totalLevels) {
        this.totalLevels = totalLevels;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}
