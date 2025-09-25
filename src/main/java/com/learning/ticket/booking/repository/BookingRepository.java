package com.learning.ticket.booking.repository;

import com.learning.ticket.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository  extends MongoRepository<Booking, String> {
}
