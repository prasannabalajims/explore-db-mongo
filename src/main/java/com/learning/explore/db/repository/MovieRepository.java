package com.learning.explore.db.repository;

import com.learning.explore.db.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie,String> {}
