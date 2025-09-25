package com.learning.ticket.booking.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    private String userId;
    private List<String> seatNumbers;
}
