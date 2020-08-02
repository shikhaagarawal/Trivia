package com.game.trivia.controller;

import com.game.trivia.repository.model.*;
import com.game.trivia.service.GameInstanceService;
import com.game.trivia.service.QuestionInstanceService;
import com.game.trivia.util.GenerateStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GameController {

    Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    public GameInstanceService gameInstanceService;

    @Autowired
    QuestionInstanceService questionInstanceService;

    @Autowired
    public SimpMessagingTemplate template;

    @Value("${players.min}")
    private String minPlayers;

    @Value("${game.begin.time.seconds}")
    private String gameWaitTime;

    @Value("${question.answer.wait.time.seconds}")
    private String answerWaitTime;

    @Value("${game.levels.to.play}")
    public String gameLevelsToPlay;

    private final String PLAYER_QUEUE_NAME = "/queue/play/game";

    private Map<Long, Map<String, Player>> playerAnswers = new HashMap<>();
    private Map<Long, String> fastestPlayer = new HashMap<>();
    private Map<Long, Integer> gameCorrectAnswer = new HashMap<>();

    @MessageMapping("/add/player")
    public void addPlayer(@Payload Player player, Principal principal) {

        logger.info("PlayerName: " + player.getPlayerName());
        //TODO input validations
        player.setSessionId(principal.getName());
        player.setPlaying(true);
        GameInstance game = gameInstanceService.addPlayerInGame(player);
        player.setGameId(game.getGameId());
        if (game.getPlayers().size() == Integer.parseInt(minPlayers)) {
            //Send notification to all players
            game.getPlayers().parallelStream().filter(p -> p.isPlaying() == true).forEach(p -> {
                p.setGameId(game.getGameId());
                p.setStartGame(true);
                broadcastPlayerInfo(p, p.getSessionId());
            });
            showQuiz(game.getGameId(), game.getLevel());
        } else if (game.getPlayers().size() > Integer.parseInt(minPlayers)) {
            //Otherwise send notification only to the current player
            player.setStartGame(true);
            broadcastPlayerInfo(player, principal.getName());
        }
    }

    /**
     * Invoked when player selects an answer on UI.
     *
     * @param player
     * @param principal
     */
    @MessageMapping("/quiz/selection")
    public void quizAnswerSelectedByPlayer(@Payload Player player, Principal principal) {
        logger.info("Player : " + player.getPlayerName() + " selected choice: " + player.getSelectedAnswer());
        player.setAnswerReceivedAt(LocalTime.now());
        playerAnswers.putIfAbsent(player.getGameId(), new HashMap<>());
        playerAnswers.get(player.getGameId()).putIfAbsent(principal.getName(), player);
        //Cache the first correct answer player session id
        if (gameCorrectAnswer.containsKey(player.getGameId()) && player.getSelectedAnswer() == gameCorrectAnswer.get(player.getGameId())) {
            fastestPlayer.putIfAbsent(player.getGameId(), player.getSessionId());
        }
    }

    private void showQuiz(long gameId, int gameLevel) {
        if (gameLevel > Integer.parseInt(gameLevelsToPlay)) {
            return;
        }
        try {
            Thread.sleep(Integer.parseInt(gameWaitTime) * 1000);
            GameInstance currentGame = gameInstanceService.findGame(gameId);
            QuestionBank ques = questionInstanceService.fetchQuestion(currentGame.getLevel() + 1);
            if (ques.getId().length() == 0) return;
            questionInstanceService.changeQuesStatus(ques, false);
            gameInstanceService.addQuesInGame(ques.getId(), currentGame, Status.PLAYING);

            //Send question to all players added in current game.
            gameCorrectAnswer.put(currentGame.getGameId(), ques.getCorrectChoice());
            broadcastQuiz(gameId, ques);

            //Wait for ques to be answered
            Thread.sleep(Integer.parseInt(answerWaitTime) * 1000);
            processStatistic(gameId, ques);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will be invoked after question is displayed to players.
     * This will generate statistics, broadcast them and update stats and player status(active/inactive) in database
     *
     * @param gameId
     * @param ques
     */
    private int processStatistic(long gameId, QuestionBank ques) {
        //Send overall stats and result of a particular player
        logger.info("Time's up! Generate Stats for questionId : " + ques.getId());

        GameInstance currentGame = gameInstanceService.findGame(gameId);
        int nextLevelPlayerCount = 0;
        Question q = currentGame.getQuestions().parallelStream()
                .filter(x -> x.getQuestionId().equals(ques.getId()))
                .findFirst().get();

        GenerateStats generateStats = new GenerateStats(currentGame, gameCorrectAnswer.get(currentGame.getGameId())).invoke(playerAnswers);
        nextLevelPlayerCount = generateStats.getNextLevelPlayerCount();

        q.setStats(Arrays.asList(new Statistic(ques.getChoices().get(0).getText(), generateStats.getCountChoice1()),
                new Statistic(ques.getChoices().get(1).getText(), generateStats.getCountChoice2()),
                new Statistic(ques.getChoices().get(2).getText(), generateStats.getCountChoice3()),
                new Statistic(ques.getChoices().get(3).getText(), generateStats.getCountChoice4())));

        broadcastStats(currentGame, q);

        decideWinnerOrShowNextGame(currentGame, generateStats);

        //Clear cached map to get ready for next level
        fastestPlayer.remove(currentGame.getGameId());
        playerAnswers.remove(currentGame.getGameId());
        return nextLevelPlayerCount;
    }

    /**
     * If game has reached to the final level - winner is fastest finger player and finish the game
     * If only one player has answered correctly - he/she is the winner and finish the game
     * If none player has answered correctly - finish the game
     * If none of the above condition matches - show next quiz
     *
     * @param currentGame
     * @param generateStats
     */
    private void decideWinnerOrShowNextGame(GameInstance currentGame, GenerateStats generateStats) {
        if (currentGame.getLevel() == Integer.parseInt(gameLevelsToPlay)) {
            logger.info("Reached to last level, winner is :" + fastestPlayer.getOrDefault(currentGame.getGameId(), null));
            for (Player currentPlayer : currentGame.getPlayers()) {
                if (currentPlayer.isPlaying() && currentPlayer.getSessionId().equals(fastestPlayer.getOrDefault(currentGame.getGameId(), null))) {
                    broadcastWinner(currentGame, currentPlayer);
                } else if (currentPlayer.isPlaying()) {
                    currentPlayer.setPlaying(false);
                    broadcastPlayerInfo(currentPlayer, currentPlayer.getSessionId());
                }
            }
            currentGame.setStatus(Status.FINISHED);
            gameInstanceService.saveGame(currentGame);
        } else if (generateStats.getNextLevelPlayerCount() <= 1) {
            logger.info("None or one player has answered correctly");
            for (Player currentPlayer : currentGame.getPlayers()) {
                if (currentPlayer.isPlaying()) {
                    broadcastWinner(currentGame, currentPlayer);
                }
            }
            currentGame.setStatus(Status.FINISHED);
            gameInstanceService.saveGame(currentGame);
        } else {
            //Advance to next level
            for (Player currentPlayer : currentGame.getPlayers()) {
                playerAnswers.remove(currentGame.getGameId());
                showQuiz(currentGame.getGameId(), currentGame.getLevel());
            }
        }
    }

    /**
     * Attaching stats to the player pojo. Then broadcast player info.
     *
     * @param game
     * @param q
     */
    private void broadcastStats(GameInstance game, Question q) {
        game.getPlayers().parallelStream().forEach(player -> {
            player.setStats(q.getStats());
            broadcastPlayerInfo(player, player.getSessionId());
            player.setStats(null);
            player.setAnswerReceivedAt(null);
        });
    }

    private void broadcastPlayerInfo(Player player, String sessionId) {
        template.convertAndSendToUser(sessionId, PLAYER_QUEUE_NAME, player);
    }

    private GameInstance broadcastQuiz(long gameId, QuestionBank ques) {
        GameInstance game = gameInstanceService.findGame(gameId); //Fetch updated list of players
        game.getPlayers().parallelStream()
                .filter(Player::isPlaying)
                .forEach(p -> {
                    template.convertAndSendToUser(p.getSessionId(), PLAYER_QUEUE_NAME, ques);
                });
        return game;
    }

    /**
     * Only one player can be winner, therefore only that player will get the winner page on web.
     *
     * @param game
     * @param player
     */
    private void broadcastWinner(GameInstance game, Player player) {
        game.setWinner(player.getPlayerName());
        Winner winner = new Winner(game.getPlayers().size(), game.getLevel(), true);
        template.convertAndSendToUser(player.getSessionId(), PLAYER_QUEUE_NAME, winner);
    }

}
