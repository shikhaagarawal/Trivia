package com.game.trivia.dao.model;

public class Player {
    private String playerName;
    private String gameInstance;
    private boolean startGame;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGameInstance() {
        return gameInstance;
    }

    public void setGameInstance(String gameInstance) {
        this.gameInstance = gameInstance;
    }

    public boolean isStartGame() {
        return startGame;
    }

    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
    }
}
