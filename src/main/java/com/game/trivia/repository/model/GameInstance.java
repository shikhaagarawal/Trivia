package com.game.trivia.repository.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "GameInstance")
public class GameInstance {

    @Id
    private String id;

    private long gameId;
    private Status status;
    private String winner;
    private List<Player> players = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private int level;
    private LocalTime gameFinsihedTime;
    private LocalTime gameStartTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == Status.FINISHED) {
            gameFinsihedTime = LocalTime.now();
        } else if (status == Status.PLAYING) {
            gameStartTime = LocalTime.now();
        }
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public LocalTime getGameFinsihedTime() {
        return gameFinsihedTime;
    }

    public void setGameFinsihedTime(LocalTime gameFinsihedTime) {
        this.gameFinsihedTime = gameFinsihedTime;
    }

    public LocalTime getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(LocalTime gameStartTime) {
        this.gameStartTime = gameStartTime;
    }
}
