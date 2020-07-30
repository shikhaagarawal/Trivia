package com.game.trivia.controller;

import com.game.trivia.repository.model.*;
import com.game.trivia.service.GameInstanceService;
import com.game.trivia.service.QuestionInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
public class PlayerController {

    Logger logger = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    GameInstanceService gameInstanceService;

    @Autowired
    QuestionInstanceService questionInstanceService;

    @Autowired
    SimpMessagingTemplate template;

    @Value("${players.min}")
    private String minPlayers;

    @Value("${game.begin.time.seconds}")
    private String gameWaitTime;

    @Value("${question.answer.wait.time.seconds}")
    private String answerWaitTime;

    @Value("${game.levels.to.play}")
    private String gameLevelsToPlay;

    private final String PLAYER_QUEUE_NAME = "/queue/play/game";

    private Map<Long, Map<String, Player>> playerAnswers = new HashMap<>();
    private Map<Long, String> fastestPlayer = new HashMap<>();
    private Map<Long, Integer> gameCorrectAnswer = new HashMap<>();

    @MessageMapping("/add/player")
    public void addPlayer(@Payload Player player, Principal principal) {

        logger.info("PlayerName: " + player.getPlayerName());

        player.setSessionId(principal.getName());
        player.setPlaying(true);
        GameInstance game = gameInstanceService.addPlayerInGame(player);
        player.setGameId(game.getGameId());
        if (game.getPlayers().size() == Integer.parseInt(minPlayers)) {
            //Send notification to all players
            for (Player p : game.getPlayers()) {
                if (p.isPlaying()) {
                    p.setGameId(game.getGameId());
                    broadcastGameAboutToBegin(p, p.getSessionId());
                }
            }
        } else if (game.getPlayers().size() > Integer.parseInt(minPlayers)) {
            //Otherwise send notification only to the current player
            broadcastGameAboutToBegin(player, principal.getName());
        }
        if (player.isStartGame()) {
            showQuiz(game);
        }
    }

    private void showQuiz(GameInstance currentGame) {
        if (currentGame.getLevel() > Integer.parseInt(gameLevelsToPlay)) {
            return;
        }
        try {
            Thread.sleep(Integer.parseInt(gameWaitTime) * 1000); //TODO here is a bug for last moment players joining in
            QuestionBank ques = questionInstanceService.fetchQuestion(currentGame.getLevel()+1);
            questionInstanceService.changeQuesStatus(ques, false);
            gameInstanceService.addQuesInGame(ques.getId(), currentGame, Status.PLAYING);

            //Send question to all players added in current game.
            gameCorrectAnswer.put(currentGame.getGameId(),ques.getCorrectChoice());
            currentGame = broadcastQuiz(currentGame, ques);

            //Wait for ques to be answered
            Thread.sleep(Integer.parseInt(answerWaitTime) * 1000);
            processAndBroadcastStatistic(currentGame, ques);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will be invoked after question is displayed to players.
     * This will generate statistics, broadcast them and update stats and player status(active/inactive) in database
     *
     * @param currentGame
     * @param ques
     */
    private int processAndBroadcastStatistic(GameInstance currentGame, QuestionBank ques) {
        //Send overall stats and result of a particular player
        logger.info("Time's up! Generate Stats for questionId : " + ques.getId());
        int correctChoice = gameCorrectAnswer.get(currentGame.getGameId());
        int nextLevelPlayerCount = 0;
        for (Question q : currentGame.getQuestions()) {
            if (q.getQuestionId().equals(ques.getId())) {
                int countChoice1 = 0, countChoice2 = 0, countChoice3 = 0, countChoice4 = 0;

                //Look for all players
                for (Player currentPlayer : currentGame.getPlayers()) {
                    Player cachedPlayerInfo = playerAnswers.getOrDefault(currentGame.getGameId(), new HashMap<>()).getOrDefault(currentPlayer.getSessionId(), null);
                    if (null != cachedPlayerInfo) {
                        currentPlayer.setSelectedAnswer(cachedPlayerInfo.getSelectedAnswer());
                        currentPlayer.setAnswerReceivedAt(cachedPlayerInfo.getAnswerReceivedAt());
                        if (currentPlayer.getSelectedAnswer() == correctChoice) {
                            currentPlayer.setPlaying(true);
                            nextLevelPlayerCount++;
                        } else {
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

                q.setStats(Arrays.asList(new Statistic(ques.getChoices().get(correctChoice - 1).getText(), ques.getChoices().get(0).getText(), countChoice1),
                        new Statistic(ques.getChoices().get(correctChoice - 1).getText(), ques.getChoices().get(1).getText(), countChoice2),
                        new Statistic(ques.getChoices().get(correctChoice - 1).getText(), ques.getChoices().get(2).getText(), countChoice3),
                        new Statistic(ques.getChoices().get(correctChoice - 1).getText(), ques.getChoices().get(3).getText(), countChoice4)));

                for (Player currentPlayer : currentGame.getPlayers()) {
                    currentPlayer.setStats(q.getStats());
                    template.convertAndSendToUser(currentPlayer.getSessionId(), PLAYER_QUEUE_NAME, currentPlayer);
                    currentPlayer.setStats(null);
                    currentPlayer.setAnswerReceivedAt(null);
                }

                //Find winner or send to next game level
                if (currentGame.getLevel() == Integer.parseInt(gameLevelsToPlay)) {
                    logger.info("Reached to last level, winner is :" + fastestPlayer.getOrDefault(currentGame.getGameId(), null));
                    for (Player currentPlayer : currentGame.getPlayers()) {
                        if (currentPlayer.isPlaying() && currentPlayer.getSessionId().equals(fastestPlayer.getOrDefault(currentGame.getGameId(), null))) {
                            broadcastWinner(currentGame, currentPlayer);
                        } else if (currentPlayer.isPlaying()) {
                            currentPlayer.setPlaying(false);
                            template.convertAndSendToUser(currentPlayer.getSessionId(), PLAYER_QUEUE_NAME, currentPlayer);
                        }
                    }
                    currentGame.setStatus(Status.FINISHED);
                } else if (nextLevelPlayerCount <= 1) {
                    logger.info("None or one player has answered correctly");
                    for (Player currentPlayer : currentGame.getPlayers()) {
                        if (currentPlayer.isPlaying()) {
                            broadcastWinner(currentGame, currentPlayer);
                        }
                    }
                    currentGame.setStatus(Status.FINISHED);
                } else {
                    //Advance to next level
                    for (Player currentPlayer : currentGame.getPlayers()) {
                        try {
                            Thread.sleep(3000);
                        }catch (InterruptedException ie){
                            logger.error(ie.getLocalizedMessage());
                        }
                        playerAnswers.remove(currentGame.getGameId());
                        showQuiz(currentGame);
                    }
                }
                //update player info in database, also Update winner and game status in database (if any)
                gameInstanceService.saveGame(currentGame); //TODO check where this should be

                //Clear cached map to get ready for next level
                fastestPlayer.remove(currentGame.getGameId());
                playerAnswers.remove(currentGame.getGameId());
                break;
            }
        }
        return nextLevelPlayerCount;
    }


    private void broadcastGameAboutToBegin(Player p, String sessionId) {
        p.setStartGame(true);
        template.convertAndSendToUser(sessionId, PLAYER_QUEUE_NAME, p);
    }

    private GameInstance broadcastQuiz(GameInstance game, QuestionBank ques) {
        game = gameInstanceService.findGame(game.getGameId()); //Fetch updated list
        ques.setCorrectChoice(0); //TODO ignore in sending response rather than setting to 0.
        for (Player p : game.getPlayers()) {
            if (p.isPlaying()) {
                template.convertAndSendToUser(p.getSessionId(), PLAYER_QUEUE_NAME, ques);
            }
        }
        return game;
    }

    private void broadcastWinner(GameInstance game, Player player) {
        game.setWinner(player.getSessionId());

        Winner winner = new Winner(game.getPlayers().size(), game.getLevel(), true);
        template.convertAndSendToUser(player.getSessionId(), PLAYER_QUEUE_NAME, winner);
    }

    @MessageMapping("/quiz/selection")
    public void quizAnswerSelectedByPlayer(@Payload Player player, Principal principal) {
        logger.info("Player : " + player.getPlayerName() + " selected choice: " + player.getSelectedAnswer());
        player.setAnswerReceivedAt(LocalTime.now());
        playerAnswers.putIfAbsent(player.getGameId(), new HashMap<>());
        playerAnswers.get(player.getGameId()).putIfAbsent(principal.getName(), player);
        if(player.getSelectedAnswer() == gameCorrectAnswer.get(player.getGameId())){
            fastestPlayer.putIfAbsent(player.getGameId(), player.getSessionId());
        }
    }

}
