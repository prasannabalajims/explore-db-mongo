package com.learning.explore.db.dto;

import lombok.Data;

@Data
public class SeatAvailability {
    private String id;
    private String movieName;
    private int availableSeats;
}
