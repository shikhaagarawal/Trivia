package com.game.trivia.service;

import com.game.trivia.repository.GameInstanceRepository;
import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameInstanceService {

    Logger logger = LoggerFactory.getLogger(GameInstanceService.class);

    @Autowired
    GameInstanceRepository gameInstanceRepository;

    //@Value("{players.game.begin.time.seconds}")
    //private String gameWaitTime;


    Timer timer = new Timer();

    public GameInstance addPlayerInGame(String userName) {

        //find waiting game
        List<GameInstance> waitingPool = gameInstanceRepository.findByStatus(Status.WAITING);
        if (waitingPool.size() == 0) {
            logger.info("Creating new game");
            return createNewGame(userName);
        }
        if (waitingPool.size() > 1) {
            //TODO code error -Error handling
        }
        logger.info("Adding player in new game");
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("gameId").is(waitingPool.get(0).getGameId()));
        Optional<GameInstance> gameInstance = gameInstanceRepository.findById(waitingPool.get(0).getId());

        gameInstance.get().getPlayers().add(userName);
        gameInstanceRepository.save(gameInstance.get());

        return gameInstance.get();
    }

    public GameInstance createNewGame(String userName) {
        GameInstance gameInstance = new GameInstance();
        gameInstance.setGameId(12L); //TODO generate new gameID
        gameInstance.setPlayers(Arrays.asList(userName));
        gameInstance.setStatus(Status.WAITING);
        gameInstanceRepository.save(gameInstance);
        return gameInstance;
    }

    public void fetchQuestion(long gameId, int level) {
        logger.info("Fetching question for gameId:"+gameId);
        timer.schedule(new RemindTask(), 30*1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            //TODO fetch question and broadcast
            logger.info("Closing Fetching question for gameId:");
            //this.template.convertAndSend("/topic/play/game", p);
            timer.cancel(); //Terminate the timer thread
        }
    }
}
