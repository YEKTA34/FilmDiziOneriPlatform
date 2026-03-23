package com.yazlab.reviewservice.controller;

import com.yazlab.reviewservice.dto.ReviewRequest;
import com.yazlab.reviewservice.dto.ReviewResponse;
import com.yazlab.reviewservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.saveReview(request));
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByFilm(@PathVariable String filmId) {
        return ResponseEntity.ok(reviewService.getReviewsByFilmId(filmId));
    }
}