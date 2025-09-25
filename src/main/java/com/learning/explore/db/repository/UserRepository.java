package com.learning.explore.db.repository;

import com.learning.explore.db.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Find users by name (case-insensitive)
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<User> findByNameContainingIgnoreCase(String name);
}
