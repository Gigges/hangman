package com.example.dell.myapplication;

/**
 * Created by Johannes on 20.08.2016.
 */
public class Score {

    private String Teamname = "";

    private int points = 0;

    public Score(String name, int points){
        this.Teamname = name;
        this.points = points;
    }

    public String getTeamname() {
        return Teamname;
    }

    public int getPoints() {
        return points;
    }

    public void setTeamname(String teamname) {
        this.Teamname = teamname;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
