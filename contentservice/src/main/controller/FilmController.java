package com.yazlab.contentservice.controller;

import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/filmler") 
public class FilmController {
    @Autowired
    private FilmRepository repository;
    @PostMapping
    public ResponseEntity<Film> filmEkle(@RequestBody Film film) {
        Film kaydedilen = repository.save(film);
        return new ResponseEntity<>(kaydedilen, HttpStatus.CREATED); 
    }
    @GetMapping
    public List<Film> listele() {
        return repository.findAll();
    }
    @GetMapping("/{isim}")
    public ResponseEntity<?> bul(@PathVariable String isim) {
        Film sonuc = repository.findByFilmAdi(isim);
        if (sonuc != null) {
            return ResponseEntity.ok(sonuc);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Maalesef '" + isim + "' isimli film arsivde yok.");
    }
}