package com.example.android.newsapp.Models;

public class News {

    private String date;
    private String category;
    private String webTitle;
    private String webURL;
    private String mThumbUrl;

    public News(String date, String webTitle, String category, String webURL, String mThumbUrl) {
        this.date = date;
        this.webTitle = webTitle;
        this.category = category;
        this.webURL = webURL;
        this.mThumbUrl = mThumbUrl;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getWebURL() {
        return webURL;
    }

    public String getmThumbUrl() {
        return mThumbUrl;
    }
}
