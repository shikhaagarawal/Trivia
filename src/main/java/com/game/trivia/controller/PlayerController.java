package com.game.trivia.controller;

import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.Player;
import com.game.trivia.repository.model.Status;
import com.game.trivia.service.GameInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
public class PlayerController {

    Logger logger = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    GameInstanceService gameInstanceService;

    //@Value("{players.min}")
    //private String minPlayers;

    @Autowired
    SimpMessagingTemplate template;

    @MessageMapping("/add/player")
    @SendTo("/topic/play/game")
    public Player addPlayer(@Payload Player player, @Header("simpSessionId") String sessionId) {

        logger.info("PlayerName: " + player.getPlayerName());

        //TODO handle sessionId, that should not be created twice in DB
        player.setUserName(player.getPlayerName() + "_" + sessionId);
        GameInstance gi = gameInstanceService.addPlayerInGame(player.getPlayerName() + "_" + sessionId);

        if (gi.getPlayers().size() >= 3
                && gi.getStatus() == Status.WAITING) {
            player.setStartGame(true);
            gameInstanceService.fetchQuestion(gi.getGameId(), gi.getLevel() + 1);
        }
        player.setGameId(gi.getGameId());
        return player;
        //this.template.convertAndSend(player);
    }

}
