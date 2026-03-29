package com.yazlab.contentservice.controller;

import com.yazlab.contentservice.service.ContentService;
import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.dto.ReviewDTO; 
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity; 
import java.util.List;
import java.util.Map; 
import java.util.HashMap; 

@RestController
@RequestMapping("/api/films")
public class FilmController {

    private final ContentService contentService; 

    public FilmController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return contentService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable String id) {
        return contentService.getFilmById(id);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getFilmWithReviews(@PathVariable String id) {
        Film film = contentService.getFilmById(id);
        
        List<ReviewDTO> reviews = contentService.getFilmReviews(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("film", film);
        response.put("reviews", reviews);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return contentService.saveFilm(film);
    }
    
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable String id) {
        contentService.deleteFilm(id);
    }
}