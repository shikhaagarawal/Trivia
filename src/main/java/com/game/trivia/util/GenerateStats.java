package com.game.trivia.util;

import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.Player;

import java.util.HashMap;
import java.util.Map;

public class GenerateStats {
    private GameInstance currentGame;
    private int correctChoice;
    private int nextLevelPlayerCount;
    private int countChoice1;
    private int countChoice2;
    private int countChoice3;
    private int countChoice4;

    public GenerateStats(GameInstance currentGame, int correctChoice, int nextLevelPlayerCount) {
        this.currentGame = currentGame;
        this.correctChoice = correctChoice;
        this.nextLevelPlayerCount = nextLevelPlayerCount;
    }

    public int getNextLevelPlayerCount() {
        return nextLevelPlayerCount;
    }

    public int getCountChoice1() {
        return countChoice1;
    }

    public int getCountChoice2() {
        return countChoice2;
    }

    public int getCountChoice3() {
        return countChoice3;
    }

    public int getCountChoice4() {
        return countChoice4;
    }

    public GenerateStats invoke(Map<Long, Map<String, Player>> playerAnswers) {
        countChoice1 = 0;
        countChoice2 = 0;
        countChoice3 = 0;
        countChoice4 = 0;

        //Calculate Stats from all players and set status is playing or not
        for (Player currentPlayer : currentGame.getPlayers()) {
            Player cachedPlayerInfo = playerAnswers.getOrDefault(currentGame.getGameId(), new HashMap<>()).getOrDefault(currentPlayer.getSessionId(), null);
            if (null != cachedPlayerInfo) {
                currentPlayer.setSelectedAnswer(cachedPlayerInfo.getSelectedAnswer());
                currentPlayer.setAnswerReceivedAt(cachedPlayerInfo.getAnswerReceivedAt());
                if (currentPlayer.getSelectedAnswer() == correctChoice) {
                    currentPlayer.setSelectedCorrectAnswer(true);
                    currentPlayer.setPlaying(true);
                    nextLevelPlayerCount++;
                } else {
                    currentPlayer.setSelectedCorrectAnswer(false);
                    currentPlayer.setPlaying(false);
                }
                if (currentPlayer.getSelectedAnswer() == 1) countChoice1++;
                if (currentPlayer.getSelectedAnswer() == 2) countChoice2++;
                if (currentPlayer.getSelectedAnswer() == 3) countChoice3++;
                if (currentPlayer.getSelectedAnswer() == 4) countChoice4++;
            } else {
                currentPlayer.setPlaying(false);
            }
        }
        return this;
    }
}
