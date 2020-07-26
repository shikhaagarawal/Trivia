package com.game.trivia.controller;

import com.game.trivia.dao.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
public class PlayerController {

    Logger logger = LoggerFactory.getLogger(PlayerController.class);
    int playerCount=0;

    @MessageMapping("/add/player")
    @SendTo("/topic/play/game")
    public Player addPlayer(@Payload Player player){
        playerCount++;
        logger.info("PlayerName: "+player.getPlayerName() +"total number of Players:"+playerCount);
        player.setGameInstance("1234");
        if(playerCount>2){
            player.setStartGame(true);
        }
        return player;
        //this.simpMessagingTemplate.convertAndSend("/topic/news", message)
    }
}
