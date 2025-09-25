package com.learning.ticket.booking.repository;

import com.learning.ticket.booking.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie,String> {}
