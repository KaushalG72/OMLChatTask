package com.sunilkumar.omlchattask.models;

public class Users {

    private String id;
    private String username;
    private boolean typing;
    private String imageURL;
    private String status;

    public Users() {
    }

    public Users(String id, String username, String imageURL, String status, boolean typing) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.typing = typing;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
