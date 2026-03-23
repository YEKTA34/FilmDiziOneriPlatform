package com.yazlab.contentservice.dto;

public class FilmRequest {
    private String filmAdi;
    private String tur;
    private int yil;
    private String yonetmen;
    private double puan;
    public FilmRequest() {}

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