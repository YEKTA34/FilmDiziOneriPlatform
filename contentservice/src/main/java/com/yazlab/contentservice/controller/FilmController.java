package com.yazlab.contentservice.controller;
import com.yazlab.contentservice.service.ContentService;
import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.repository.FilmRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/films")
public class FilmController {

    private final ContentService contentService; // İsim güncellendi

    public FilmController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return contentService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return contentService.saveFilm(film);
    }
}