package com.learning.ticket.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("bookings")
@CompoundIndexes({
        @CompoundIndex(name="user_idx", def = "{'userId':1}"),
        @CompoundIndex(name="show_idx", def = "{'showId':1}")
})
@Data @AllArgsConstructor @NoArgsConstructor
public class Booking {
    @Id
    private String id;
    private String showId;
    private String userId;
    private List<String> seatNumbers;
    private double amount;
    private String status; // CONFIRMED / CANCELLED
    private Date createdAt;
}
