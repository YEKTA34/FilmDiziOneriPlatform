package com.yazlab.contentservice.controller;

import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/films")
public class FilmController {

    @Autowired
    private FilmRepository repository;

    @GetMapping
    public List<Film> getFilms() {
        return repository.findAll(); // MongoDB'deki tüm filmleri getirir
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return repository.save(film); // Yeni film ekler
    }
}