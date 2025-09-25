package com.learning.explore.db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatAvailability {
    private String id;
    private String movieName;
    private int availableSeats;
}
