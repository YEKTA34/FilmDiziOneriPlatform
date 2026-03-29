package com.yazlab.contentservice.repository;

import com.yazlab.contentservice.model.Film;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> { 
    
    
    Film findByFilmAdi(String filmAdi);
}