package com.example.myapplication;

public class Note {
    private String id;
    private String userID;
    private String title;
    private String body;
    private String imageUrl;

    // Default constructor
    public Note() {
    }

    // Constructor without image
/*    public Note(String id, String title, String body, String userID) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userID = userID;
    }*/

    // Constructor with image
    public Note(String id, String title, String body, String userID, String imageUrl) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userID = userID;
        this.imageUrl = imageUrl;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Note{" +
                "userID='" + userID + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}