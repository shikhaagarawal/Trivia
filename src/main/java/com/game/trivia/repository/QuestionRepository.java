package com.game.trivia.repository;

import com.game.trivia.repository.model.QuestionBank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<QuestionBank, String> {

    public List<QuestionBank> findByLevelAndActive(int level, boolean active);

    //@Aggregation(pipeline = {"$sample: { size: 1 }"})
    //public AggregationResults findByLevelAndActive(int level, boolean active);
}
