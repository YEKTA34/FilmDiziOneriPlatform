package com.yazlab.reviewservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String filmId;
    private String kullaniciAdi;
    private String yorumMetni;
    private double puan;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFilmId() { return filmId; }
    public void setFilmId(String filmId) { this.filmId = filmId; }
    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }
    public String getYorumMetni() { return yorumMetni; }
    public void setYorumMetni(String yorumMetni) { this.yorumMetni = yorumMetni; }
    public double getPuan() { return puan; }
    public void setPuan(double puan) { this.puan = puan; }
}