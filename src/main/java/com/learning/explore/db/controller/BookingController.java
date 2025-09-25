package com.learning.explore.db.controller;

import com.learning.explore.db.dto.BookingRequest;
import com.learning.explore.db.dto.SeatAvailability;
import com.learning.explore.db.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @GetMapping("/availability")
    public List<SeatAvailability> getSeatAvailability(
            @RequestParam(required = false) String showId,
            @RequestParam(required = false) String movieId
    ) {
        List<SeatAvailability> response = bookingService.getSeatAvailability(showId, movieId);
        return response;
    }

    @PostMapping("/shows/{showId}/book")
    public ResponseEntity<?> book(@PathVariable String showId, @RequestBody BookingRequest req) {
        String bookingId = bookingService.performSeatReservation(req.getUserId(), showId, req.getSeatNumbers());
        if (bookingId == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Seats unavailable"));
        }
        return ResponseEntity.ok(Map.of("bookingId", bookingId));
    }
}