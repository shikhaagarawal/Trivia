package com.game.trivia.controller;

import com.game.trivia.repository.QuestionRepository;
import com.game.trivia.repository.model.QuestionBank;
import com.game.trivia.util.QuestionGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * For admins only
 */
@RestController
public class QuestionBankController {

    @Autowired
    QuestionRepository questionRepository;

    QuestionGenerator questionGenerator = new QuestionGenerator();

    @RequestMapping(value="/add/questions/{level}/{from}/{to}",method= RequestMethod.POST, produces = "application/json")
    public void generateQuesionBank(@PathVariable int level, @PathVariable int from, @PathVariable int to){
        int count =20;
        while(count > 0) {
            QuestionBank qb = questionGenerator.generate(to, from, 4);
            qb.setLevel(level);
            qb.setActive(true);
            questionRepository.save(qb);
            count--;
        }
    }

    @RequestMapping(value="/reuse/questions", method = RequestMethod.PUT,produces = "application/json")
    public void reuseQuestions(){
        List<QuestionBank> list = questionRepository.findAll();
        for(QuestionBank q : list){
            q.setActive(true);
        }
        questionRepository.saveAll(list);
    }
}
