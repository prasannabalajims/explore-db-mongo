package com.learning.ticket.booking.service;

import com.learning.ticket.booking.dto.ShowRevenue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class AnalyticsService {

    @Autowired
    private MongoTemplate mongoTemplate;


    public List<ShowRevenue> getRevenuePerShow() {
        // 1. Match confirmed bookings
        MatchOperation match = match(Criteria.where("status").is("CONFIRMED"));

        // 2. Group by showId
        GroupOperation group = group("showId")
                .sum("amount").as("totalRevenue")
                .sum(ConditionalOperators.ifNull("seatNumbers").then(0)) // fallback
                .as("ticketsSold")
                .avg("amount").as("avgTicketPrice");

        // 3. Sort by revenue descending
        SortOperation sort = Aggregation.sort(org.springframework.data.domain.Sort.by("totalRevenue").descending());

        Aggregation aggregation = newAggregation(match, group, sort);

        AggregationResults<ShowRevenue> results = mongoTemplate.aggregate(
                aggregation,
                "bookings",
                ShowRevenue.class
        );

        return results.getMappedResults();
    }
}