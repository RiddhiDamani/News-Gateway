package com.riddhidamani.news_gateway;


import java.io.Serializable;

public class NewsArticle implements Serializable {

    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String date;

    public void setAuthor(String author) {this.author = author;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setUrl(String url) {this.url = url;}
    public void setUrlToImage(String urlToImage) {this.urlToImage = urlToImage;}
    public void setDate(String date) {this.date = date;}


    public String getAuthor() {return this.author; }
    public String getTitle() {return this.title; }
    public String getDescription() {return this.description; }
    public String getUrl() {return this.url; }
    public String getUrlToImage() {return this.urlToImage; }
    public String getDate() {return this.date; }

}

