package com.game.trivia.dao;

import com.game.trivia.dao.model.QuestionBank;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepository extends MongoRepository<QuestionBank ,String> {
}
