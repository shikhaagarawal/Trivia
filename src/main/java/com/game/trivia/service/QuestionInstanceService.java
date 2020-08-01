package com.game.trivia.service;

import com.game.trivia.controller.GameController;
import com.game.trivia.repository.QuestionRepository;
import com.game.trivia.repository.model.QuestionBank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class QuestionInstanceService {

    @Autowired
    public QuestionRepository questionRepository;

    Random random = new Random();

    Logger logger = LoggerFactory.getLogger(QuestionInstanceService.class);

    /**
     * Retrieve random question from database for a given game level
     *
     * @param level
     * @return
     */
    public QuestionBank fetchQuestion(int level) {
        List<QuestionBank> questions = questionRepository.findByLevelAndActive(level, true);
        if(questions.size() == 0){
            logger.error("No active questions for level ${{level}} found in database",level);
            return new QuestionBank();
        }

        return questions.get(random.nextInt(questions.size()));
    }

    /**
     * Set active flag to true or false
     *
     * @param ques
     * @param active
     */
    public void changeQuesStatus(QuestionBank ques, boolean active) {
        ques.setActive(active);
        questionRepository.save(ques);
    }
}
