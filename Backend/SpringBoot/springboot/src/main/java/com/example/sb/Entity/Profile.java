package com.example.sb.Entity;

import jakarta.persistence.*;

@Entity()
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String USERNAME;
    public String PROFILE_PICTURE;
    public String CLUB;
    public String CLUB_PICTURE;
    public int WINS;
    public int LOSSES;
    public int GAMES_PLAYED;
    public int RATING;


    public Profile(String name) {

        this.USERNAME = name;
        this.PROFILE_PICTURE = "default";
        this.CLUB = "NONE";
        this.CLUB_PICTURE = "default";
        this.WINS = 0;
        this.LOSSES = 0;
        this.GAMES_PLAYED = 0;
        this.RATING = 800;
    }
    public Profile(String USERNAME, String PROFILE_PICTURE, String club, String club_picture) {
        this.USERNAME = USERNAME;
        this.PROFILE_PICTURE = PROFILE_PICTURE;
        this.CLUB = club;
        this.CLUB_PICTURE = club_picture;
        this.WINS = 0;
        this.LOSSES = 0;
        this.GAMES_PLAYED = 0;
        this.RATING = 800;
    }

    public Profile() {
    }
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
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
