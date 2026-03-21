package com.yazlab.reviewservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String filmId;     
    private String kullaniciAdi;
    private String yorumMetni;
    private double puan;
}