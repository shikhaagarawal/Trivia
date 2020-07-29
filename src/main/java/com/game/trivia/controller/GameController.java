package com.game.trivia.controller;

import com.game.trivia.repository.QuestionRepository;
import com.game.trivia.repository.model.QuestionBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @RequestMapping(value = "/app/quiz/choice",method = RequestMethod.GET, produces = "application/json")
    public List<QuestionBank> savePlayersQuizChoice(){
        List<QuestionBank> qbR = questionRepository.findAll();
        return qbR;


    }

}
