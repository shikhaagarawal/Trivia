package com.game.trivia.dao;

import com.game.trivia.dao.model.QuestionBank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<QuestionBank, String >{

    //@Query(value = "{'level': ?0}", fields = "{'employees' : 0}")
    //List<QuestionBank> findQuestionRandomly(String level);

}
