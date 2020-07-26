package com.game.trivia.controller;

import com.game.trivia.dao.QuestionRepository;
import com.game.trivia.dao.model.QuestionBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins={"http://localhost:4200","https://profile-analyzer.herokuapp.com"})
@CrossOrigin
@RestController
public class GameController {

    @Autowired
    QuestionRepository questionRepository;

    @RequestMapping(value = "/games",method = RequestMethod.GET, produces = "application/json")
    public List<QuestionBank> getQuestion(){
        List<QuestionBank> qbR = questionRepository.findAll();
        return qbR;


    }



    //@SendToUser


    /*@MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(PostNotFoundException ex) {
        logger.debug("Post not found", ex);
        return "The requested post was not found";
    }*/


}
