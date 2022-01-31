package com.riddhidamani.news_gateway;


import java.io.Serializable;

// News Articles (Stories) Class
public class NewsArticle implements Serializable {

    // Fields
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAtDate;

    // Getters
    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public String getUrlToImage() {
        return urlToImage;
    }
    public String getPublishedAtDate() {
        return publishedAtDate;
    }


    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
    public void setPublishedAtDate(String publishedAtDate) {
        this.publishedAtDate = publishedAtDate;
    }
}

