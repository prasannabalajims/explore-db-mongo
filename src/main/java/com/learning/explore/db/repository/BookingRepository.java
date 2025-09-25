package com.learning.explore.db.repository;

import com.learning.explore.db.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository  extends MongoRepository<Booking, String> {
}
