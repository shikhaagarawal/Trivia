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

    private final String PLAYER_QUEUE_NAME = "/queue/play/game";
    private final String QUIZ_ANSWER_QUEUE_NAME = "/queue/quiz/answer";

    private Map<Long, Map<String, Player>> gamePlayerQuiz = new HashMap<>();

    @MessageMapping("/add/player")
    public void addPlayer(@Payload Player player, Principal principal) {

        logger.info("PlayerName: " + player.getPlayerName());

        player.setSessionId(principal.getName());
        player.setPlaying(true);
        GameInstance currentGame = gameInstanceService.addPlayerInGame(player);
        player.setGameId(currentGame.getGameId());
        if (currentGame.getPlayers().size() == Integer.parseInt(minPlayers)
                && currentGame.getStatus() == Status.WAITING) {
            //Send notification to all users
            for (Player p : currentGame.getPlayers()) {
                p.setStartGame(true);
                p.setGameId(currentGame.getGameId());
                template.convertAndSendToUser(p.getSessionId(), PLAYER_QUEUE_NAME, p);
            }
        } else if (currentGame.getPlayers().size() > Integer.parseInt(minPlayers)
                && currentGame.getStatus() == Status.WAITING) {
            //Otherwise only to current user
            player.setStartGame(true);
            template.convertAndSendToUser(principal.getName(), PLAYER_QUEUE_NAME, player);
        }
        if (player.isStartGame()) {
            try {
                Thread.sleep(Integer.parseInt(gameWaitTime) * 1000); //TODO bug is here
                QuestionBank ques = questionInstanceService.fetchQuestion(currentGame.getLevel());
                questionInstanceService.changeQuesStatus(ques, false);
                gameInstanceService.addQuesInGame(ques.getId(), currentGame, Status.PLAYING);

                //Send question to all players added in current game.
                currentGame = gameInstanceService.findGame(currentGame.getGameId()); //Fetch updated list
                final int correctChoice = ques.getCorrectChoice();
                ques.setCorrectChoice(0); //TODO igore in sending response rather than setting to 0.
                for (Player p : currentGame.getPlayers()) {
                    template.convertAndSendToUser(p.getSessionId(), PLAYER_QUEUE_NAME, ques);
                }

                //Wait for ques to be answered
                Thread.sleep(Integer.parseInt(answerWaitTime) * 1000);
                //Send overall stats and result of a particular player
                logger.info("Time's up! Generate Stats for questionId : " + ques.getId());

                for (Question q : currentGame.getQuestions()) {
                    if (q.getQuestionId().equals(ques.getId())) {
                        int countChoice1 = 0, countChoice2 = 0, countChoice3 = 0, countChoice4 = 0;

                        //Look for all players
                        for (Player currentPlayer : currentGame.getPlayers()) {
                            Player cachedPlayerInfo = gamePlayerQuiz.getOrDefault(currentGame.getGameId(), new HashMap<>()).getOrDefault(currentPlayer.getSessionId(), null);
                            if(null != cachedPlayerInfo) {
                                currentPlayer.setSelectedAnswer(cachedPlayerInfo.getSelectedAnswer());
                                currentPlayer.setAnswerReceivedAt(cachedPlayerInfo.getAnswerReceivedAt());
                                if (currentPlayer.getSelectedAnswer() == correctChoice) currentPlayer.setPlaying(true);
                                else currentPlayer.setPlaying(false);
                                if (currentPlayer.getSelectedAnswer() == 1) countChoice1++;
                                if (currentPlayer.getSelectedAnswer() == 2) countChoice2++;
                                if (currentPlayer.getSelectedAnswer() == 3) countChoice3++;
                                if (currentPlayer.getSelectedAnswer() == 4) countChoice4++;
                            }else{
                                currentPlayer.setPlaying(false);
                            }
                        }
                        //TODO update player info in database

                        //TODO last level fastestFinger wins
                        q.setStats(Arrays.asList(new Statistic(ques.getChoices().get(correctChoice-1).getText(), ques.getChoices().get(0).getText(), countChoice1),
                                new Statistic(ques.getChoices().get(correctChoice-1).getText(), ques.getChoices().get(1).getText(), countChoice2),
                                new Statistic(ques.getChoices().get(correctChoice-1).getText(), ques.getChoices().get(2).getText(), countChoice3),
                                new Statistic(ques.getChoices().get(correctChoice-1).getText(), ques.getChoices().get(3).getText(), countChoice4)));
                        for (Player currentPlayer : currentGame.getPlayers()) {
                            currentPlayer.setStats(q.getStats());
                            template.convertAndSendToUser(currentPlayer.getSessionId(), PLAYER_QUEUE_NAME, q.getStats());
                            if(currentPlayer.isPlaying()){
                                //loop over question series
                            }
                            else{
                                //disconnect websocket connection
                            }
                        }
                        break;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @MessageMapping("/quiz/selection")
    public void quizAnswerSelectedByPlayer(@Payload Player player, Principal principal) {
            logger.info("Player : " + player.getPlayerName() + " selected choice: " + player.getSelectedAnswer());
        player.setAnswerReceivedAt(LocalTime.now());
        gamePlayerQuiz.putIfAbsent(player.getGameId(),new HashMap<>());
        gamePlayerQuiz.get(player.getGameId()).putIfAbsent(principal.getName(), player);
    }

}
