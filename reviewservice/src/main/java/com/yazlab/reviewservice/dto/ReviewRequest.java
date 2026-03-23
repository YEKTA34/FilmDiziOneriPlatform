package com.yazlab.reviewservice.dto;

public class ReviewRequest {
    private String filmId;
    private String kullaniciAdi;
    private String yorumMetni;
    private double puan;
    public String getFilmId() { return filmId; }
    public void setFilmId(String filmId) { this.filmId = filmId; }
    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }
    public String getYorumMetni() { return yorumMetni; }
    public void setYorumMetni(String yorumMetni) { this.yorumMetni = yorumMetni; }
    public double getPuan() { return puan; }
    public void setPuan(double puan) { this.puan = puan; }
}