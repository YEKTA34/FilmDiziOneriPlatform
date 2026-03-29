package com.yazlab.contentservice.controller;

import com.yazlab.contentservice.model.Film;
import com.yazlab.contentservice.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/film-merkezi")
public class FilmController {

    @Autowired
    private FilmRepository filmDeposu;

    @PostMapping("/ekle")
    public ResponseEntity<Film> yeniKayit(@RequestBody Film gelenVeri) {
        Film kaydedilen = filmDeposu.save(gelenVeri);
        return new ResponseEntity<>(kaydedilen, HttpStatus.CREATED);
    }

    @GetMapping("/liste")
    public List<Film> hepsiniGetir() {
        return filmDeposu.findAll();
    }

    @GetMapping("/ara/{filmIsmi}")
    public ResponseEntity<?> tekilSorgu(@PathVariable String filmIsmi) {
        Film sonuc = filmDeposu.findByFilmAdi(filmIsmi);
        
        if (sonuc != null) {
            return ResponseEntity.ok(sonuc);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(filmIsmi + " isimli icerik veri tabaninda bulunamadi.");
    }
}