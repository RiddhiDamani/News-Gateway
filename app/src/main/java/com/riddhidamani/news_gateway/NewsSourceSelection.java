package com.riddhidamani.news_gateway;

public class NewsSourceSelection
{
    private String topic;
    private String country;
    private String language;

    private boolean selectingTopic = false;
    private boolean selectingCountry = false;
    private boolean selectingLanguage = false;


    public NewsSourceSelection() {
        //topic = MainActivity.menu_all;
        //country = MainActivity.menu_all;
        //language = MainActivity.menu_all;
    }

    public void setTopic(String topic){ this.topic = topic; }
    public void setCountry(String country) { this.country = country; }
    public void setLanguage(String language) { this.language = language; }

    public String getTopic() { return this.topic; }
    public String getCountry() { return this.country; }
    public String getLanguage() { return this.language; }

    public boolean getTopicStatus () { return this.selectingTopic; }
    public boolean getCountryStatus () { return this.selectingCountry; }
    public boolean getLanguageStatus () { return this.selectingLanguage; }


    public void setTopicStatus() {
        this.selectingTopic = true;
        this.selectingCountry = false;
        this.selectingLanguage = false;
    }

    public void setCountryStatus() {
        this.selectingTopic = false;
        this.selectingCountry = true;
        this.selectingLanguage = false;
    }

    public void setLanguageStatus() {
        this.selectingTopic = false;
        this.selectingCountry = false;
        this.selectingLanguage = true;
    }

    public void finishSelecting() {
        this.selectingTopic = false;
        this.selectingCountry = false;
        this.selectingLanguage = false;
    }
}
