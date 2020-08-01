package com.game.trivia.service;

import com.game.trivia.repository.GameInstanceRepository;
import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.Player;
import com.game.trivia.repository.model.Question;
import com.game.trivia.repository.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GameInstanceService {

    Logger logger = LoggerFactory.getLogger(GameInstanceService.class);

    @Autowired
    GameInstanceRepository gameInstanceRepository;

    /**
     * Add player into gameInstance where game status is waiting
     *
     * @param player
     * @return
     */
    public GameInstance addPlayerInGame(Player player) {
        //find waiting game
        List<GameInstance> waitingPool = gameInstanceRepository.findByStatus(Status.WAITING);
        if (waitingPool.size() == 0) {
            logger.info("Creating new game");
            return createNewGame(player);
        }
        //Add player in new game
        waitingPool.get(0).getPlayers().add(player);
        saveGame(waitingPool.get(0));

        return waitingPool.get(0);
    }

    public void saveGame(GameInstance gameInstance) {
        gameInstanceRepository.save(gameInstance);
    }

    /**
     * Create a game when no waiting gameInstance found in DB
     *
     * @param player
     * @return
     */
    public GameInstance createNewGame(Player player) {
        GameInstance gameInstance = new GameInstance();
        gameInstance.setGameId(nextGameId());
        gameInstance.setPlayers(Arrays.asList(player));
        gameInstance.setStatus(Status.WAITING);
        gameInstance.setLevel(0);
        saveGame(gameInstance);
        return gameInstance;
    }

    /**
     * Add question into current game
     *
     * @return
     */
    public void addQuesInGame(String quesId, GameInstance game, Status status) {
        Question ques = new Question();
        ques.setQuestionId(quesId);
        if (game.getQuestions().size() == 0) game.setQuestions(Arrays.asList(ques));
        else game.getQuestions().add(ques);
        game.setStatus(status);
        game.setLevel(game.getLevel() + 1);
        saveGame(game);
    }

    /**
     * Find maximum gameId into gameInstance collection.
     *
     * @return
     */
    private long nextGameId() {
        List<GameInstance> list = gameInstanceRepository.findAll(Sort.by(Sort.Direction.DESC, "gameId"));
        if (list.size() == 0) return 1L;
        return list.get(0).getGameId() + 1;
    }

    public GameInstance findGame(long gameId) {
        return gameInstanceRepository.findByGameId(gameId);
    }
}
