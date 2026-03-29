package com.yazlab.contentservice.service;

import com.yazlab.contentservice.client.ReviewClient;
import com.yazlab.contentservice.dto.ReviewDTO;
import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.repository.FilmRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContentService {

    private final FilmRepository repository;
    private final ReviewClient reviewClient;

    public ContentService(FilmRepository repository, ReviewClient reviewClient) {
        this.repository = repository;
        this.reviewClient = reviewClient;
    }

    public List<Film> getAllFilms() {
        return repository.findAll();
    }

    public Film saveFilm(Film film) {
        if (film.getFilmAdi() == null || film.getFilmAdi().trim().isEmpty()) {
            throw new RuntimeException("Film adı boş bırakılamaz!");
        }
        return repository.save(film);
    }

    public Film getFilmById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film bulunamadı: " + id));
    }

    public void deleteFilm(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Silinecek film bulunamadı: " + id);
        }
        repository.deleteById(id);
    }

    public List<ReviewDTO> getFilmReviews(String filmId) {
        return reviewClient.getReviewsByFilmId(filmId);
    }
}