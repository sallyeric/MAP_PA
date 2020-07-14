package com.example.map_pa;

public class PostItem {
    private String username;
    private String postcontent;
    private String posttag;

    public PostItem () { }

    public PostItem(String username, String postcontent, String posttag) {
        this.username = username;
        this.postcontent = postcontent;
        this.posttag = posttag;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return postcontent;
    }

    public String getTag() {
        return posttag;
    }
}
