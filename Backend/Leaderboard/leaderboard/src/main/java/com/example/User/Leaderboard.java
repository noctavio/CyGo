package com.example.User;

import java.util.ArrayList;
import java.util.List;

//this wil function like a leaderboard when you sort the list from greatest to least(top to bottom) on user Rating
public class Leaderboard {
    private List<User> boardList;

    public Leaderboard() {
        boardList = new ArrayList<User>();
    }
    public List<User> getBoardList() {
        return boardList;
    }
    public void addUser(User user) {
        boardList.add(user);
    }
    public void removeUser(User user) {
        boardList.remove(user);
    }
}
