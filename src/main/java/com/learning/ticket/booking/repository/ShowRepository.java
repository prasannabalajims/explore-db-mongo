package com.learning.ticket.booking.repository;

import com.learning.ticket.booking.model.Show;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowRepository extends MongoRepository<Show, String> {
}
