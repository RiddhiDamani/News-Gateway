package com.riddhidamani.news_gateway;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

// Raw Reading and Processing of the Country and Language JSON files
public class LoadAbbrData {

    private static final String TAG = "LoadAbbrData";
    private static final HashMap<String, String> countryCodeToName = new HashMap<>();
    private static final HashMap<String, String> langCodeToName = new HashMap<>();

    // loading abbreviations
    public static void loadAbbreviations(MainActivity mainActivityContext) {
        try {
            loadAndProcessCountryJSON(mainActivityContext);
            loadAndProcessLanguageJSON(mainActivityContext);
        }
        catch (Exception exception) {
            Log.d(TAG, "loadAbbreviations errors: " + exception.getMessage());
        }
    }

    // load and process country json data
    public static void loadAndProcessCountryJSON(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.country_codes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        try {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();

            JSONObject jObject = new JSONObject(sb.toString());
            JSONArray countryArray = jObject.getJSONArray("countries");
            for(int i = 0; i < countryArray.length(); i++) {
                JSONObject country = (JSONObject) countryArray.get(i);
                String code = country.getString("code");
                String name = country.getString("name");
                if(!countryCodeToName.containsKey(code)) {
                    countryCodeToName.put(code, name);
                }
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            Log.d(TAG, "loadAndProcessCountryJSON: " + exception.getMessage());
        }
    }

    // load and process language json data
    public static void loadAndProcessLanguageJSON(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.language_codes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();

            JSONObject jObject = new JSONObject(sb.toString());
            JSONArray languageArray = jObject.getJSONArray("languages");
            for(int i = 0; i < languageArray.length(); i++) {
                JSONObject language = (JSONObject) languageArray.get(i);
                String code = language.getString("code");
                String name = language.getString("name");
                if(!langCodeToName.containsKey(code)) {
                    langCodeToName.put(code, name);
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Log.d(TAG, "loadAndProcessLanguageJSON: " + exception.getMessage());
        }
    }

    // getter Methods
    public static HashMap<String, String> getCountryCodeToName() { return countryCodeToName; }
    public static HashMap<String, String> getLangCodeToName() { return langCodeToName; }

}
