package com.yazlab.contentservice.repository;

import com.yazlab.contentservice.model.Film;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FilmRepository extends MongoRepository<Film, String> { 
    
    
    Film findByFilmAdi(String filmAdi);
}