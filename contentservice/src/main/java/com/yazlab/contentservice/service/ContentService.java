package com.yazlab.contentservice.service;

import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.repository.FilmRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContentService {

    private final FilmRepository repository;

    public ContentService(FilmRepository repository) {
        this.repository = repository;
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
}