package com.learning.ticket.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private String seatNumber; // e.g. "A1"
    private String row;
    private String type; // REGULAR / PREMIUM
    private double price;
    private String status; // AVAILABLE / BOOKED
    private String bookedBy; // optional userId
    private Date bookedAt;
}
