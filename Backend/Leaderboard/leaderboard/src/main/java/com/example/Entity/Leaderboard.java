package com.example.Entity;

import java.util.ArrayList;
import java.util.List;

//this wil function like a leaderboard when you sort the list from greatest to least(top to bottom) on user Rating
public class Leaderboard {
    private List<Player> boardList;

    public Leaderboard() {
        boardList = new ArrayList<Player>();
    }
    public List<Player> getBoardList() {
        return boardList;
    }
    public void addUser(Player player) {
        boardList.add(player);
    }
    public void removeUser(Player player) {
        boardList.remove(player);
    }
}
