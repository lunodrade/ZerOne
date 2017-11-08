package com.lunodrade.zerone.ranking;


public class RankingEntry {
    String uid;
    String name;
    String photo;
    int xp;
    int position;

    public RankingEntry(String key, Integer value) {
        this.uid = key;
        this.xp = value;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public int getXp() {
        return xp;
    }

    public String getPhoto() { return photo; }

    public void setPhoto(String photo) { this.photo = photo; }

    public void setName(String name) { this.name = name; }

    public void setPosition(int position) { this.position = position; }

    public int getPosition() { return position; }
}