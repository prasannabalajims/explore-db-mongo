package com.learning.ticket.booking.util;

import com.learning.ticket.booking.model.Movie;
import com.learning.ticket.booking.model.Seat;
import com.learning.ticket.booking.model.Show;
import com.learning.ticket.booking.model.User;
import com.learning.ticket.booking.repository.MovieRepository;
import com.learning.ticket.booking.repository.ShowRepository;
import com.learning.ticket.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    MovieRepository movieRepo;
    @Autowired
    ShowRepository showRepo;
    @Autowired
    UserRepository userRepo;

    @Override
    public void run(String... args) throws Exception {
        movieRepo.deleteAll(); showRepo.deleteAll(); userRepo.deleteAll();
        Movie m = movieRepo.save(new Movie(null,"Space Adventure",120,"SciFi"));
        User u = userRepo.save(new User(null,"Prasanna","p@example.com"));

        List<Seat> seats = new ArrayList<>();
        for (int r=1;r<=3;r++){
            for (int c=1;c<=6;c++){
                String sn = (char)('A'+r-1) + String.valueOf(c);
                seats.add(new Seat(sn, String.valueOf(r), "REGULAR", 150.0, "AVAILABLE", null, null));
            }
        }
        Show s = new Show(null, m.getId(), "theater-1", Date.from(Instant.now().plus(1, ChronoUnit.DAYS)), seats);
        showRepo.save(s);

        System.out.println("Seeded movieId=" + m.getId() + " showId=" + s.getId() + " userId=" + u.getId());
    }
}