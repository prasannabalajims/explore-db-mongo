package com.learning.explore.db.controller;

import com.learning.explore.db.dto.ShowRevenue;
import com.learning.explore.db.service.AnalyticsService;
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
