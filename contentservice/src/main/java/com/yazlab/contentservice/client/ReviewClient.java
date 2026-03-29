package com.yazlab.contentservice.client;

import com.yazlab.contentservice.dto.ReviewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "review-service", url = "http://reviewservice:8082") 
public interface ReviewClient {
    
    @GetMapping("/api/reviews/film/{filmId}")
    List<ReviewDTO> getReviewsByFilmId(@PathVariable("filmId") Long filmId);
}