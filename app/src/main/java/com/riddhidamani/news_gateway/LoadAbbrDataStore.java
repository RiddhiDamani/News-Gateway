package com.riddhidamani.news_gateway;

import java.util.HashMap;

public class LoadAbbrDataStore {

    private static final String TAG = "LoadAbbrDataStore";
    private static final HashMap<String, String> countryCodeToName = new HashMap<>();
    private static final HashMap<String, String> langCodeToName = new HashMap<>();


    public static HashMap<String, String> getCountryCodeToName() { return countryCodeToName; }
    public static HashMap<String, String> getLangCodeToName() { return langCodeToName; }

}
