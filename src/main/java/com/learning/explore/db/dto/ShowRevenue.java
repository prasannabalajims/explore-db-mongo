package com.learning.explore.db.dto;

import lombok.Data;

@Data
public class ShowRevenue {
    private String showId;
    private double totalRevenue;
    private int ticketsSold;
    private double avgTicketPrice;
}