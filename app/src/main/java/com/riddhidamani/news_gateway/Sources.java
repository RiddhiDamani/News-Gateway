package com.riddhidamani.news_gateway;

// Source class
public class Sources {

    private final String id;
    private final String name;
    private final String category;
    private final String language;
    private final String country;

    // Constructor
    public Sources(String id, String name, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    // Getter methods
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public String getLanguage() {
        return language;
    }
    public String getCountry() {
        return country;
    }
}

