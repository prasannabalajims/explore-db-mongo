package com.learning.ticket.booking.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeatAvailabilityPublisher {

    @Autowired
    private SimpMessagingTemplate template;

    public void broadcastAvailability(Object payload) {
        template.convertAndSend("/topic/seat-availability", payload);
    }
}