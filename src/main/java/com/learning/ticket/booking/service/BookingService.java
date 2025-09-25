package com.learning.ticket.booking.service;

import com.learning.ticket.booking.dto.SeatAvailability;
import com.learning.ticket.booking.model.Booking;
import com.learning.ticket.booking.repository.BookingRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class BookingService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    BookingRepository bookingRepository;

    public List<SeatAvailability> getSeatAvailability(String showId, String movieId) {
        List<AggregationOperation> operations = new ArrayList<>();

        if(showId != null) {
            operations.add(match(Criteria.where("_id").is(showId)));
        }

        if(movieId != null) {
            operations.add(match(Criteria.where("movieId").is(movieId)));
        }

        operations.add(unwind("seats"));

        operations.add(match(Criteria.where("seats.status").is("AVAILABLE")));

        operations.add(LookupOperation.newLookup()
                .from("movies")
                .localField("movieId")
                .foreignField("_id")
                .as("movieDetails"));

        GroupOperation group = group("_id")
                .first("movieDetails.title").as("movieName")
                .count().as("availableSeats");

        operations.add(group);

        Aggregation aggregation = newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, "shows", SeatAvailability.class)
                .getMappedResults();

    }


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
