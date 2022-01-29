package com.riddhidamani.news_gateway;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class LoadAbbrDataStore {

    private static final String TAG = "LoadAbbrDataStore";
    private static final HashMap<String, String> countryCodeToName = new HashMap<>();
    private static final HashMap<String, String> langCodeToName = new HashMap<>();


    public static HashMap<String, String> getCountryCodeToName() { return countryCodeToName; }
    public static HashMap<String, String> getLangCodeToName() { return langCodeToName; }

    public static void loadAbbrData(MainActivity context) {
        try {
            String countryJSONStr = loadCountryJSONData(context);
            processCountryJSON(countryJSONStr);

            String languageJSONStr = loadLanguageJSONData(context);
            processLanguageJSON(languageJSONStr);

        }catch (Exception e) {
            Log.d(TAG, "laodData: " + e.getMessage());
        }
    }

    static String loadCountryJSONData(Context context) throws IOException, JSONException {
        InputStream is = context.getResources().openRawResource(R.raw.country_codes);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return sb.toString();

    }

    static String loadLanguageJSONData(Context context) throws IOException, JSONException {
        InputStream is = context.getResources().openRawResource(R.raw.language_codes);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return sb.toString();

    }

    private static void processCountryJSON(String s) {
        try
        {
            JSONObject jObject = new JSONObject(s);
            JSONArray countryArray = jObject.getJSONArray("countries");
            for(int i = 0; i < countryArray.length(); i++) {
                JSONObject country = (JSONObject) countryArray.get(i);
                String code = country.getString("code");
                String name = country.getString("name");
                if(!countryCodeToName.containsKey(code)) {
                    countryCodeToName.put(code, name);
                }
            }
        }catch (Exception e) {
            Log.d(TAG, "processCountryJSON: " + e.getMessage());
        }
    }

    private static void processLanguageJSON(String s) {
        try
        {
            JSONObject jObject = new JSONObject(s);
            JSONArray languageArray = jObject.getJSONArray("languages");
            for(int i = 0; i < languageArray.length(); i++) {
                JSONObject language = (JSONObject) languageArray.get(i);
                String code = language.getString("code");
                String name = language.getString("name");
                if(!langCodeToName.containsKey(code)) {
                    langCodeToName.put(code, name);
                }
            }
        }catch (Exception e) {
            Log.d(TAG, "processCountryJSON: " + e.getMessage());
        }
    }

}
