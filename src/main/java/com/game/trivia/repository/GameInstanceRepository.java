package com.game.trivia.repository;

import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameInstanceRepository extends MongoRepository<GameInstance, String> {

    List<GameInstance> findByStatus(Status status);

    GameInstance findByGameId(long gameId);
}
