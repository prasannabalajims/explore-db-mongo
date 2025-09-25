package com.learning.explore.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("movies")
@Data @AllArgsConstructor @NoArgsConstructor
public class Movie {
    @Id
    private String id;
    private String title;
    private int durationInMins;
    private String genre;
}
