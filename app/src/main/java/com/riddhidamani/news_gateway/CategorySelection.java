package com.riddhidamani.news_gateway;

// Selection Class for News Sources
// Based on the flags of each categories; source list is selected for the drawer
public class CategorySelection
{
    // Variables declarations
    private String topic;
    private String country;
    private String language;
    private boolean categoryTopicFlag = false;
    private boolean categoryCountryFlag = false;
    private boolean categoryLanguageFlag = false;

    // Constructor - Setting all as default value
    public CategorySelection() {
        topic = MainActivity.menu_all;
        country = MainActivity.menu_all;
        language = MainActivity.menu_all;
    }

    // setter methods
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // getter methods
    public String getTopic() {
        return topic;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }


    // flag getters
    public boolean isCategoryTopicFlag() {
        return categoryTopicFlag;
    }

    public boolean isCategoryCountryFlag() {
        return categoryCountryFlag;
    }

    public boolean isCategoryLanguageFlag() {
        return categoryLanguageFlag;
    }

    // flag setters
    public void setCategoryTopicFlag() {
        this.categoryTopicFlag = true;
        this.categoryCountryFlag = false;
        this.categoryLanguageFlag = false;
    }

    public void setCategoryCountryFlag() {
        this.categoryTopicFlag = false;
        this.categoryCountryFlag = true;
        this.categoryLanguageFlag = false;
    }

    public void setCategoryLanguageFlag() {
        this.categoryTopicFlag = false;
        this.categoryCountryFlag = false;
        this.categoryLanguageFlag = true;
    }

    public void completeFlag() {
        this.categoryTopicFlag = false;
        this.categoryCountryFlag = false;
        this.categoryLanguageFlag = false;
    }
}
