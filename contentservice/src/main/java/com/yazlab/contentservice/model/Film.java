package com.yazlab.contentservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "films") 
public class Film {
    @Id
    private String id;
    private String filmAdi;
    private String tur;
    private int yil;
    private String yonetmen;
    private double puan;
}