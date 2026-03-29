package com.yazlab.contentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewDTO {
    private String id;
    private String filmId;
    
    @JsonProperty("yorumMetni") 
    private String yorumMetni;
    
    private int puan;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFilmId() { return filmId; }
    public void setFilmId(String filmId) { this.filmId = filmId; }
    
    public String getYorumMetni() { return yorumMetni; }
    public void setYorumMetni(String yorumMetni) { this.yorumMetni = yorumMetni; }
    
    public int getPuan() { return puan; }
    public void setPuan(int puan) { this.puan = puan; }
}