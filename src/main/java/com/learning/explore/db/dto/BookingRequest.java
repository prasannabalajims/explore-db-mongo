package com.learning.explore.db.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    private String userId;
    private List<String> seatNumbers;
}
