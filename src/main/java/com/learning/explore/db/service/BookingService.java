package com.learning.explore.db.service;

import com.learning.explore.db.model.Booking;
import com.learning.explore.db.repository.BookingRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
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

    public String bookSeats(String userId, String showId, List<String> seatNumbers) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("shows");

        List<Bson> elemMatches = seatNumbers.stream()
                .map(sn -> Filters.elemMatch("seats", Filters.and(Filters.eq("seatNumber", sn), Filters.eq("status", "AVAILABLE"))))
                .toList();
        Bson filter = Filters.and(Filters.eq("_id", new ObjectId(showId)), Filters.and(elemMatches));

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

        Document updated = collection.findOneAndUpdate(filter, update, options);

        if (updated == null) {
            // seats were not all available
            return null;
        }

        // compute amount from updated seats
        List<Document> seats = (List<Document>) updated.get("seats");
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
}
