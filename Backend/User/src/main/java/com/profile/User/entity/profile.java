package com.profile.User.entity;

public class profile {
    private String USERNAME;
    private String PROFILE_PICTURE;
    private String CLUB;
    private String CLUB_PICTURE;
    private int WINS;
    private int LOSSES;
    private int GAMES_PLAYED;
    private int RATING;


    public profile(String name) {
        this.USERNAME = name;
        this.PROFILE_PICTURE = "default";
        this.CLUB = "NONE";
        this.CLUB_PICTURE = "default";
        this.WINS = 0;
        this.LOSSES = 0;
        this.GAMES_PLAYED = 0;
        this.RATING = 800;
    }
    public profile(String name, String PROFILE_PICTURE, String club, String club_picture) {
        this.USERNAME = name;
        this.PROFILE_PICTURE = PROFILE_PICTURE;
        this.CLUB = club;
        this.CLUB_PICTURE = club_picture;
        this.WINS = 0;
        this.LOSSES = 0;
        this.GAMES_PLAYED = 0;
        this.RATING = 800;
    }

    public profile() {
    }
    public String getUSERNAME(){
        return USERNAME;
    }

    public void setUSERNAME(String name){
        this.USERNAME = name;
    }

    public String getPROFILE_PICTURE(){
        return PROFILE_PICTURE;
    }

    public void setPROFILE_PICTURE(String Pic){
        this.PROFILE_PICTURE = Pic;
    }

    public String getCLUB(){
        return CLUB;
    }

    public void setCLUB(String Club){
        this.CLUB = Club;
    }

    public String getCLUB_PICTURE(){return CLUB_PICTURE;}

    public void setCLUB_PICTURE(String Pic){
        this.CLUB_PICTURE = Pic;
    }

    public int getWINS(){return WINS;}

    public void setWINS(int wins){
        this.WINS = wins;
    }

    public int getLOSSES(){return LOSSES;}

    public void setLOSSES(int losses){
        this.LOSSES = losses;
    }

    public int getGAMES_PLAYED(){return GAMES_PLAYED;}

    public void setGAMES_PLAYED(int games){
        this.GAMES_PLAYED = games;
    }

    public int getRATING(){return RATING;}

    public void setRATING(int rating){
        this.RATING = rating;
    }



}
