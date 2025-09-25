package com.learning.explore.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("shows")
@CompoundIndexes({
        @CompoundIndex(name="movie_start_idx", def = "{'movieId':1, 'startTime':1}"),
        @CompoundIndex(name="theater_start_idx", def = "{'theaterId':1, 'startTime':1}")
})
@Data @AllArgsConstructor @NoArgsConstructor
public class Show {
    @Id
    private String id;
    private String movieId;
    private String theaterId;
    private Date startTime;
    private List<Seat> seats; // embedded — availability per show
}
