package com.yazlab.reviewservice.service;

import com.yazlab.reviewservice.dto.ReviewRequest;
import com.yazlab.reviewservice.dto.ReviewResponse;
import com.yazlab.reviewservice.model.Review;
import com.yazlab.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public ReviewResponse saveReview(ReviewRequest request) {
        if (request.getPuan() < 0 || request.getPuan() > 10) {
            throw new RuntimeException("Puan 0 ile 10 arasında olmalıdır!");
        }
        if (request.getYorumMetni() == null || request.getYorumMetni().trim().isEmpty()) {
            throw new RuntimeException("Yorum metni boş bırakılamaz!");
        }

        Review review = new Review();
        review.setFilmId(request.getFilmId());
        review.setKullaniciAdi(request.getKullaniciAdi());
        review.setYorumMetni(request.getYorumMetni());
        review.setPuan(request.getPuan());

        Review savedReview = repository.save(review);
        return mapToResponse(savedReview);
    }

    public List<ReviewResponse> getAllReviews() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByFilmId(String filmId) {
        return repository.findByFilmId(filmId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setFilmId(review.getFilmId());
        response.setKullaniciAdi(review.getKullaniciAdi());
        response.setYorumMetni(review.getYorumMetni());
        response.setPuan(review.getPuan());
        return response;
    }
}