package com.learning.explore.db.repository;

import com.learning.explore.db.model.Show;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowRepository extends MongoRepository<Show, String> {
}
