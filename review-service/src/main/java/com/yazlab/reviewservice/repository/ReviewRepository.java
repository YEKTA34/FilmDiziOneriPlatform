package com.yazlab.reviewservice.repository;

import com.yazlab.reviewservice.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByFilmId(String filmId);
}