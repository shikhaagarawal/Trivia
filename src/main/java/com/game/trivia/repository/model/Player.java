package com.game.trivia.repository.model;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public class Player {
    private String playerName;
    private String sessionId;
    private long gameId;
    private boolean startGame;
    private int selectedAnswer;
    private boolean playing = true;
    private LocalTime answerReceivedAt;
    private List<Statistic> stats;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public boolean isStartGame() {
        return startGame;
    }

    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public LocalTime getAnswerReceivedAt() {
        return answerReceivedAt;
    }

    public void setAnswerReceivedAt(LocalTime answerReceivedAt) {
        this.answerReceivedAt = answerReceivedAt;
    }

    public List<Statistic> getStats() {
        return stats;
    }

    public void setStats(List<Statistic> stats) {
        this.stats = stats;
    }
}
