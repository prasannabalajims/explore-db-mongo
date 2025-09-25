package com.learning.explore.db.service;

import com.learning.explore.db.model.Booking;
import com.learning.explore.db.repository.BookingRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    BookingRepository bookingRepository;

    public String performSeatReservation(String userId, String showId, List<String> seatNumbers) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("shows");

        // STEP 1 : Attempt update if the requested Seats are available
        Document updatedSeats = updateSeat(userId, seatNumbers, showId, collection);
        if (updatedSeats == null) {
            // seats were not all available
            return null;
        }

        // STEP 2: Post-validate Seats Update
        boolean allBooked =  validateIfAllSeatsUpdated(userId, seatNumbers, updatedSeats);

        // STEP 3: Rollback (free any seats that were incorrectly booked by this user)
        if (!allBooked) {
            rollbackOnPartialSeatsUpdate(collection, userId, seatNumbers, showId);
            return null; // booking failed
        }

        // compute amount from updated seats
        List<Document> seats = (List<Document>) updatedSeats.get("seats");
        double amount = seatNumbers.stream()
                .mapToDouble(sn -> seats.stream()
                        .filter(s -> sn.equals(s.getString("seatNumber")))
                        .findFirst()
                        .map(d -> d.getDouble("price"))
                        .orElse(0.0))
                .sum();

        Booking booking = new Booking(null, showId, userId, seatNumbers, amount, "CONFIRMED", new Date());
        booking = bookingRepository.save(booking);

        return booking.getId();
    }

    private Document updateSeat(String userId, List<String> seatNumbers, String showId, MongoCollection<Document> collection) {
        Bson filter = Filters.eq("_id", new ObjectId(showId));

        Bson update = Updates.combine(
                Updates.set("seats.$[s].status", "BOOKED"),
                Updates.set("seats.$[s].bookedBy", userId),
                Updates.set("seats.$[s].bookedAt", new Date())
        );

        List<Bson> arrayFilters = Collections.singletonList(
                Filters.and(
                        Filters.in("s.seatNumber", seatNumbers),
                        Filters.eq("s.status", "AVAILABLE")
                )
        );


        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
                .arrayFilters(arrayFilters)
                .returnDocument(ReturnDocument.AFTER);

        return collection.findOneAndUpdate(filter, update, options);
    }

    private boolean validateIfAllSeatsUpdated(String userId, List<String> seatNumbers, Document updatedSeats) {
        List<Document> seats = (List<Document>) updatedSeats.get("seats");

        return seatNumbers.stream().allMatch(sn ->
                seats.stream().anyMatch(s ->
                        sn.equals(s.getString("seatNumber")) &&
                                "BOOKED".equals(s.getString("status")) &&
                                userId.equals(s.getString("bookedBy"))
                )
        );
    }

    private void rollbackOnPartialSeatsUpdate(MongoCollection<Document> collection, String userId, List<String> seatNumbers, String showId) {
        Bson rollbackUpdate = Updates.combine(
                Updates.set("seats.$[s].status", "AVAILABLE"),
                Updates.unset("seats.$[s].bookedBy"),
                Updates.unset("seats.$[s].bookedAt")
        );

        List<Bson> rollbackFilters = Collections.singletonList(
                Filters.and(
                        Filters.in("s.seatNumber", seatNumbers),
                        Filters.eq("s.bookedBy", userId) // only rollback seats we just marked
                )
        );

        collection.updateOne(Filters.eq("_id", new ObjectId(showId)),
                rollbackUpdate,
                new UpdateOptions().arrayFilters(rollbackFilters));
    }
}
