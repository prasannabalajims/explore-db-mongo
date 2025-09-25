package com.learning.ticket.booking.controller;

import com.learning.ticket.booking.dto.ShowRevenue;
import com.learning.ticket.booking.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/revenue")
    public List<ShowRevenue> getRevenuePerShow() {
        return analyticsService.getRevenuePerShow();
    }
}
