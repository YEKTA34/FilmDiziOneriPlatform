package com.yazlab.contentservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "films")
public class Film {

    @Id
    private String id;
    
    private String filmAdi;
    private String tur;
    private int yil;
    private String yonetmen;
    private double puan;

    public Film() {
    }

    public Film(String filmAdi, String tur, int yil, String yonetmen, double puan) {
        this.filmAdi = filmAdi;
        this.tur = tur;
        this.yil = yil;
        this.yonetmen = yonetmen;
        this.puan = puan;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFilmAdi() { return filmAdi; }
    public void setFilmAdi(String filmAdi) { this.filmAdi = filmAdi; }

    public String getTur() { return tur; }
    public void setTur(String tur) { this.tur = tur; }

    public int getYil() { return yil; }
    public void setYil(int yil) { this.yil = yil; }

    public String getYonetmen() { return yonetmen; }
    public void setYonetmen(String yonetmen) { this.yonetmen = yonetmen; }

    public double getPuan() { return puan; }
    public void setPuan(double puan) { this.puan = puan; }
}