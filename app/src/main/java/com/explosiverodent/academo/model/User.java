package com.explosiverodent.academo.model;

import android.net.Uri;

public class User {
    private int _id = 0;
    private String user_name = "";
    private int points = 0;
    private float xp = 0.f;
    private int level = 1;
    private String profile_picture_uri;
    public User(int _id, String user_name, int points, float xp, int level) {
        this._id = _id;
        this.user_name = user_name;
        this.points = points;
        this.xp = xp;
        this.level = level;
    }

    public int getId() { return _id; }
    public void setId(int id) { this._id = id; }

    public String getUserName() { return user_name; }
    public void setUserName(String userName) { this.user_name = userName; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public float getXp() { return xp; }
    public void setXp(float xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public void setProfilePicture(String picture_uri){
        this.profile_picture_uri = picture_uri;
    }

    public String getProfilePicture(){ return this.profile_picture_uri; }

    public Uri getProfilePictureUri(){
        if(this.profile_picture_uri == null || this.profile_picture_uri.isEmpty()) {
            return null;
        }
        return Uri.parse(this.profile_picture_uri);
    }

}