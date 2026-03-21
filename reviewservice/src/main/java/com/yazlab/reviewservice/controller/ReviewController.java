package com.yazlab.reviewservice.controller;

import com.yazlab.reviewservice.model.Review;
import com.yazlab.reviewservice.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository repository;

    public ReviewController(ReviewRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return repository.findAll();
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return repository.save(review);
    }

    @GetMapping("/film/{filmId}")
    public List<Review> getReviewsByFilm(@PathVariable String filmId) {
        return repository.findByFilmId(filmId);
    }
}