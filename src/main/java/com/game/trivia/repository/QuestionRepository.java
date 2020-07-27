package com.game.trivia.repository;

import com.game.trivia.repository.model.QuestionBank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<QuestionBank ,String> {
}
