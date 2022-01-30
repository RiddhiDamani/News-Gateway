package com.riddhidamani.news_gateway;

public class Sources {
    private String id;
    private String name;
    private String category;
    private String language;
    private String country;
    private int color;

    public Sources(String id, String name, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public String getID() { return this.id; }
    public String getName() { return this.name; }
    public String getCategory() { return this.category; }
    public String getLanguage() { return this.language; }
    public String getCountry() { return this.country; }

    public void setColor(int color) { this.color = color; }
    public int getColor() {return this.color;}
}

