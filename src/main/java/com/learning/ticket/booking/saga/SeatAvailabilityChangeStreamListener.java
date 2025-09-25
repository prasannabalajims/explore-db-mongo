package com.learning.ticket.booking.saga;

import com.learning.ticket.booking.dto.SeatAvailability;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeatAvailabilityChangeStreamListener {

    @Autowired
    private SeatAvailabilityPublisher publisher;

    @Autowired
    MongoTemplate mongoTemplate;

    @PostConstruct
    public void listen() {
//        MongoDatabase db = MongoClients.create("mongodb://localhost:27017").getDatabase("bookingdb");
        MongoCollection<Document> shows = mongoTemplate.getCollection("shows");

        new Thread(() -> {
            for (ChangeStreamDocument<Document> change : shows.watch()) {
                Document updatedShow = change.getFullDocument();
                if (updatedShow != null) {
                    // Compute aggregated availability
                    long availableSeats = updatedShow.getList("seats", Document.class)
                            .stream()
                            .filter(s -> "AVAILABLE".equals(s.getString("status")))
                            .count();

                    // Optional: fetch movie name if embedded or via lookup
                    String movieName = "";
                    if (updatedShow.containsKey("movieId")) {
                        // simple example: assume movie name is in embedded field or fetched separately
                        movieName = updatedShow.getString("movieName");
                    }

                    SeatAvailability dto = new SeatAvailability(
                            updatedShow.get("_id").toString(),
                            movieName,
                            (int) availableSeats
                    );

                    publisher.broadcastAvailability(dto);
                }
            }
        }).start();
    }
}
