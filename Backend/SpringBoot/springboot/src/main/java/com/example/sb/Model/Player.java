package com.example.sb.Model;

public class Player {
    // Getters and Setters

    private int playerId;
    private String username;
    private Boolean isReady;
    private String rank;
    private String clubname;

    public Player(int playerId) {
        this.playerId = playerId;
        this.isReady = false;  // Players are not ready when they join
    }
    public int getPlayerId() {
        return playerId;
    }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Boolean isReady() {
        return isReady;
    }
    public void setReady(boolean ready) {
        isReady = ready;
    }
    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getClubname() {
        return clubname;
    }
    public void setClubname(String clubname) {
        this.clubname = clubname;
    }
}
