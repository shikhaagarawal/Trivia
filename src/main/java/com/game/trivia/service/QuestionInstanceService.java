package com.game.trivia.service;

import com.game.trivia.repository.QuestionRepository;
import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.QuestionBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionInstanceService {

    @Autowired
    QuestionRepository questionRepository;

    /**
     * Retrieve random question from database for a given game level
     * @param level
     * @return
     */
    public QuestionBank fetchQuestion(int level) {
        List<QuestionBank> questions = questionRepository.findByLevelAndActive(level, true);
        //TODO random algo
        return questions.get(0);
    }

    /**
     * Set active flag to true or false
     * @param ques
     * @param active
     */
    public void changeQuesStatus(QuestionBank ques, boolean active){
        ques.setActive(active);
        questionRepository.save(ques);
    }
}
