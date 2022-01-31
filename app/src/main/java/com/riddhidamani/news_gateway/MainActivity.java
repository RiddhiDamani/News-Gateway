package com.riddhidamani.news_gateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.riddhidamani.news_gateway.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String newsSourcesURL = "https://newsapi.org/v2/sources";
    private static final String newsArticleURL = "https://newsapi.org/v2/top-headlines";
    private static final String apiKey = "b8988f2d0bbd4c0186dea5c522fefcd0";

    // Drawer variables
    private final HashMap<String, Integer> colorDrawerMap = new HashMap<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // Menu variables
    private Menu opt_menu;
    private SubMenu opt_menu_topics, opt_menu_countries, opt_menu_languages;
    private final CategorySelection selector = new CategorySelection();
    private static final String submenu_topics = "Topics";
    private static final String submenu_country = "Countries";
    private static final String submenu_language = "Languages";
    public static String menu_all = "All";

    // Json data mapping for country code and language code
    private HashMap<String, String> countryCodeToName;
    private HashMap<String, String> langCodeToName;

    // APIs
    private final ArrayList<Sources> srcs = new ArrayList<>();
    private final List<String> listOfNames = new ArrayList<>();
    private final List<String> listOfTopics = new ArrayList<>();
    private final List<String> listOfCountries = new ArrayList<>();
    private final List<String> listOfLanguages = new ArrayList<>();

    // API FOR UI
    private List<Sources> sourceList = new ArrayList<>();
    private List<String> displayMediaNames = new ArrayList<>();
    private final List<String> allMediaNames = new ArrayList<>();
    private List<String> topicList = new ArrayList<>();
    private final ArrayList<String> countryNameList = new ArrayList<>();
    private final ArrayList<String> languageNameList = new ArrayList<>();

    // ViewPager2
    private ViewPager2 viewPager;
    private ArrayAdapter<String> arrayAdapter;
    private NewsArticleAdapter newsArticleAdapter;
    ArrayList<NewsArticle> articleList = new ArrayList<>();
    private final ArrayList<NewsArticle> currentNewsArticleList = new ArrayList<>();

    // Color Menu
    private final HashMap<String, Integer> colorMenu = new HashMap<>();
    private final ArrayList<Integer> colors = new ArrayList<>();

    // State to save
    private String currentMediaName;
    private String currentMediaID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Make sample items for menu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItemInDrawer(position);
                    mDrawerLayout.closeDrawer(findViewById(R.id.c_layout));
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // loading json data - countryCode, langCode from raw folder.
        LoadAbbrData.loadAbbreviations(this);
        countryCodeToName = new HashMap<>(LoadAbbrData.getCountryCodeToName());
        langCodeToName = new HashMap<>(LoadAbbrData.getLangCodeToName());


        // Download Sources Data from news api using Volley
        // Gives us an object of type Request Queue
        if(sourceList.isEmpty()) {
            queue = Volley.newRequestQueue(this);
            performDownload();
        }

        newsArticleAdapter = new NewsArticleAdapter(this, currentNewsArticleList);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(newsArticleAdapter);

        // setup colorMenu
        addAllColors(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("TOPIC", selector.getTopic());
        outState.putString("COUNTRY", selector.getCountry());
        outState.putString("LANGUAGE", selector.getCountry());
        outState.putStringArrayList("MediaList", (ArrayList<String>) displayMediaNames);
        outState.putString("MEDIA_ID", currentMediaID);
        outState.putString("MEDIA_NAME", currentMediaName);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        displayMediaNames = savedInstanceState.getStringArrayList("MediaList");
        selector.setTopic(savedInstanceState.getString("TOPIC"));
        selector.setCountry(savedInstanceState.getString("COUNTRY"));
        selector.setLanguage(savedInstanceState.getString("LANGUAGE"));
        setupDrawerItemColor();

        currentMediaID = savedInstanceState.getString("MEDIA_ID");
        queue = Volley.newRequestQueue(this);
        performArticlesDownload(currentMediaID);

        currentMediaName = savedInstanceState.getString("MEDIA_NAME");
        setTitle(currentMediaName);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        if(opt_menu_topics == null) {
            setupSubMenu();
        }
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle" +  item);
            return true;
        }

        String selectedMenuItem = item.toString();
        processMenu(selectedMenuItem);
        return super.onOptionsItemSelected(item);
    }

    private void processMenu(String s) {
        switch (s) {
            case submenu_topics:
                selector.setCategoryTopicFlag();
                return;
            case submenu_country:
                selector.setCategoryCountryFlag();
                return;
            case submenu_language:
                selector.setCategoryLanguageFlag();
                return;
            default:
                if (selector.isCategoryTopicFlag()) selector.setTopic(s);
                if (selector.isCategoryCountryFlag()) selector.setCountry(s);
                if (selector.isCategoryLanguageFlag()) selector.setLanguage(s);
                break;
        }

        // check Topics
        String selectorTopic = selector.getTopic();
        String selectorCountry = selector.getCountry();
        String selectorLanguage = selector.getLanguage();

        boolean isTopicAll = selector.getTopic().equals("All");
        boolean isCountryAll = selector.getCountry().equals("All");
        boolean isLanguageAll = selector.getLanguage().equals("All");

        displayMediaNames.clear();

        for(int i = 0; i < sourceList.size(); i++) {
            Sources source = sourceList.get(i);
            String sourceTopic = source.getCategory();
            String sourceCountryCode = source.getCountry();
            String sourceCountry = countryCodeToName.get(sourceCountryCode.toUpperCase());
            String sourceLanguageCode = source.getLanguage();
            String sourceLanguage = langCodeToName.get(sourceLanguageCode.toUpperCase());
            String sourceName = source.getName();

            if(isTopicAll && isCountryAll && isLanguageAll) {
                displayMediaNames.addAll(allMediaNames);
                break;
            }else if(isTopicAll && isCountryAll){
                assert sourceLanguage != null;
                if(sourceLanguage.equals(selectorLanguage)) {
                    displayMediaNames.add(sourceName);
                }
            }else if(isTopicAll && isLanguageAll){
                assert sourceCountry != null;
                if(sourceCountry.equals(selectorCountry)) {
                    displayMediaNames.add(sourceName);
                }
            }else if (isCountryAll && isLanguageAll) {
                if(sourceTopic.equals(selectorTopic)) {
                    displayMediaNames.add(sourceName);
                }
            }else if (isTopicAll) {
                assert sourceCountry != null;
                if(sourceCountry.equals(selectorCountry)) {
                    assert sourceLanguage != null;
                    if (sourceLanguage.equals(selectorLanguage)) {
                        displayMediaNames.add(sourceName);
                    }
                }
            }else if (isCountryAll) {
                if(sourceTopic.equals(selectorTopic)) {
                    assert sourceLanguage != null;
                    if (sourceLanguage.equals(selectorLanguage)) {
                        displayMediaNames.add(sourceName);
                    }
                }
            }else if (isLanguageAll) {
                if(sourceTopic.equals(selectorTopic)) {
                    assert sourceCountry != null;
                    if (sourceCountry.equals(selectorCountry)) displayMediaNames.add(sourceName);
                }
            }else { // all selected
                if(sourceTopic.equals(selectorTopic)) {
                    assert sourceCountry != null;
                    if (sourceCountry.equals(selectorCountry)) {
                        assert sourceLanguage != null;
                        if (sourceLanguage.equals(selectorLanguage)) {
                            displayMediaNames.add(sourceName);
                        }
                    }
                }
            }
        }
        selector.completeFlag();

        // TODO no result alert
        if(displayMediaNames.size() < 1) {
            noResultAlert(selectorTopic, selectorCountry, selectorLanguage);
        }

        arrayAdapter.notifyDataSetChanged();
        setTitle("News Gateway (" + displayMediaNames.size() + ")");
    }

    private void noResultAlert(String topic, String country, String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", (dialog, id) -> { });
        builder.setTitle("No Sources");
        String message = "No News Sources match your criteria: \n" +
                "\nTopic: " + topic +
                "\nCountry: " + country +
                "\nLanguage: " + language;
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void performDownload() {
        Uri.Builder buildURL = Uri.parse(newsSourcesURL).buildUpon();
        buildURL.appendQueryParameter("apikey", apiKey);
        String urlToUse = buildURL.build().toString();

        // Creating Response Listener - listener will receive the response from my request to get internet content

        Response.Listener<JSONObject> listener = response -> {
            try {
                JSONArray sources = response.getJSONArray("sources");
                for(int i = 0; i < sources.length(); i++) {
                    JSONObject jSource = (JSONObject) sources.get(i);
                    String id = jSource.getString("id");
                    String name = jSource.getString("name");
                    String category = jSource.getString("category");
                    String language = jSource.getString("language");
                    String country = jSource.getString("country");

                    Sources srcData = new Sources(id, name, category,language,country);
                    srcs.add(srcData);
                    // get non-repeated topics
                    if(!listOfTopics.contains(category)) {
                        listOfTopics.add(category);
                    }
                    // get non-repeated countries
                    if(!listOfCountries.contains(country)) {
                        listOfCountries.add(country);
                    }
                    // get non-repeated languages
                    if(!listOfLanguages.contains(language)) {
                        listOfLanguages.add(language);
                    }
                    // get non-repeated media name
                    if(!listOfNames.contains(name)) {
                        listOfNames.add(name);
                    }
                }
                runOnUiThread(() -> {
                    setupNameList(listOfNames);
                    setupTopicList(listOfTopics);
                    getSources(srcs);
                    setupCountryList(listOfCountries);
                    setupLanguageList(listOfLanguages);
                    setupDrawerItemColor();

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener error = error1 -> {
            try {
                JSONObject jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "jsonObject: " + jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "");
                        return headers;
                    }
                };
        // Add the request to the RequestQueue.
        // Add the request Object to the queue and make the request from the internet
        // This will make asynchronous request for me and call either onResponse or onErrorResponse
        queue.add(jsonObjectRequest);
    }

    public void performArticlesDownload(String mediaID) {
        Uri.Builder buildURL = Uri.parse(newsArticleURL).buildUpon();
        buildURL.appendQueryParameter("sources", mediaID);
        buildURL.appendQueryParameter("apikey", apiKey);
        String urlToUse = buildURL.build().toString();

        articleList.clear();
        currentNewsArticleList.clear();

        Response.Listener<JSONObject> listener = response -> {
            try {
                JSONArray storiesArray = response.getJSONArray("articles");
                for(int i = 0; i < storiesArray.length(); i++) {
                    JSONObject jArticle = (JSONObject) storiesArray.get(i);
                    String author = jArticle.getString("author");
                    String title = jArticle.getString("title");
                    String content = jArticle.getString("description");
                    String url = jArticle.getString("url");
                    String urlToImage = jArticle.getString("urlToImage");
                    String date = jArticle.getString("publishedAt");

                    NewsArticle article = new NewsArticle();
                    article.setAuthor(author);
                    article.setTitle(title);
                    article.setDescription(content);
                    article.setUrl(url);
                    article.setUrlToImage(urlToImage);
                    article.setPublishedAtDate(date);

                    articleList.add(article);
                }
                runOnUiThread(() -> setArticles(articleList));
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "processJSON: " + e.getMessage());
            }
        };
        Response.ErrorListener error = error1 -> {
            try {
                JSONObject jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "Error jsonObject: " + jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "");
                        return headers;
                    }
                };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setArticles(ArrayList<NewsArticle> articleList) {
        currentNewsArticleList.clear();
        setTitle(currentMediaName);
        currentNewsArticleList.addAll(articleList);
        newsArticleAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);

    }

    public void setupNameList(List<String> nameList) {
        if(nameList == null) return;
        if(displayMediaNames.isEmpty()) {
            displayMediaNames = nameList;
        }
        allMediaNames.addAll(nameList);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, displayMediaNames));

        // show drawer icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setTitle("News Gateway ("+ displayMediaNames.size() + ")");

    }

    public void setupTopicList(List<String> _categoryList) {
        if(_categoryList == null) return;
        topicList = _categoryList;
    }

    public void getSources(List<Sources> sources) {
        if(sources == null) {
            Log.d(TAG, "getSources: sources is null" );
            return;
        }
        sourceList = sources;
    }

    public void setupCountryList(List<String> countryLists) {
        if (countryLists == null) return;

        for (int i = 0; i < countryLists.size(); i++) {
            String countryCode = countryLists.get(i).toUpperCase();
            if(countryCodeToName.containsKey(countryCode)) {
                String countryName = countryCodeToName.get(countryCode);
                countryNameList.add(countryName);
            }
        }
        Collections.sort(countryNameList);
    }

    public void setupLanguageList(List<String> languageLists) {
        if (languageLists == null)  return;
        for(int i = 0; i < languageLists.size(); i++) {
            String languageCode = languageLists.get(i).toUpperCase();
            if(langCodeToName.containsKey(languageCode)) {
                String languageName = langCodeToName.get(languageCode);
                languageNameList.add(languageName);
            }
        }
        Collections.sort(languageNameList);
        if(opt_menu != null) {
            setupSubMenu();
        }

    }

    public void setupDrawerItemColor() {
        for(int i = 0; i < sourceList.size(); i++) {
            Sources source = sourceList.get(i);
            String sourceTopic = source.getCategory();
            for (String key: colorMenu.keySet()) {
                String menuTopic = key;
                int menuColor = colorMenu.get(key);
                if(sourceTopic.equals(menuTopic)) {
                    String sourceName = source.getName();
                    colorDrawerMap.put(sourceName, menuColor);
                    break;
                }
            }
        }

        arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_item, displayMediaNames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView currentTextView = view.findViewById(R.id.text_view);
                String currentMediaName = currentTextView.getText().toString();
                int colorInt;
                if(colorDrawerMap.containsKey(currentMediaName)){
                    colorInt = colorDrawerMap.get(currentMediaName);
                    currentTextView.setTextColor(colorInt);
                }
                return view;
            }
        };

        mDrawerList.setAdapter(arrayAdapter);
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectItemInDrawer(int position) {
        viewPager.setBackground(null);
        String currentMediaName = displayMediaNames.get(position);
        currentNewsArticleList.clear();
        this.currentMediaName = currentMediaName;
        String mediaID = "";
        for(Sources s: sourceList) {
            if(s.getName().equals(currentMediaName)){
                mediaID = s.getId();
                break;
            }
        }
        if(mediaID != null) {
            Log.d(TAG, "selectItemInDrawer: find mediaID");
            currentMediaID = mediaID;
            // downloading news article based on current media id selected.
            queue = Volley.newRequestQueue(this);
            performArticlesDownload(currentMediaID);
        }
        else{
            Log.d(TAG, "selectItemInDrawer: mediaID is null");
        }

        mDrawerLayout.closeDrawer(findViewById(R.id.c_layout));
    }

    private void setupSubMenu() {
        if(opt_menu_topics == null) {
            opt_menu_topics = opt_menu.addSubMenu(submenu_topics);
            opt_menu_countries = opt_menu.addSubMenu(submenu_country);
            opt_menu_languages = opt_menu.addSubMenu(submenu_language);
            opt_menu_topics.add(menu_all);
            opt_menu_countries.add(menu_all);
            opt_menu_languages.add(menu_all);
        }

        // setup topics in menu
        int colorCounter = 0;

        for(int i = 0; i < topicList.size(); i++) {
            String category = topicList.get(i);
            MenuItem currentTopic = opt_menu_topics.add(category);
            if(colors.get(colorCounter) == null) {
                colorCounter = 0;
            }
            int colorID = colors.get(colorCounter);
            colorMenu.put(category, colorID);
            // set color
            SpannableString s = new SpannableString(category);
            s.setSpan(new ForegroundColorSpan(colorID), 0, s.length(), 0);
            //s.setSpan(new ForegroundColorSpan();
            currentTopic.setTitle(s);
            colorCounter++;
        }
        // setup country in menu
        for(String country : countryNameList) {
            opt_menu_countries.add(country);
        }
        // setup languages in menu
        for(String languageName : languageNameList){
            opt_menu_languages.add(languageName);
        }
    }

    private void addAllColors(Context context) {

        int color_0 = ContextCompat.getColor(context, R.color.category_0);
        int color_1 = ContextCompat.getColor(context, R.color.category_1);
        int color_2 = ContextCompat.getColor(context, R.color.category_2);
        int color_3 = ContextCompat.getColor(context, R.color.category_3);
        int color_4 = ContextCompat.getColor(context, R.color.category_4);
        int color_5 = ContextCompat.getColor(context, R.color.category_5);
        int color_6 = ContextCompat.getColor(context, R.color.category_6);
        int color_7 = ContextCompat.getColor(context, R.color.category_7);
        int color_8 = ContextCompat.getColor(context, R.color.category_8);
        int color_9 = ContextCompat.getColor(context, R.color.category_9);
        int color_10 = ContextCompat.getColor(context, R.color.category_10);

        colors.add(color_0);
        colors.add(color_1);
        colors.add(color_2);
        colors.add(color_3);
        colors.add(color_4);
        colors.add(color_5);
        colors.add(color_6);
        colors.add(color_7);
        colors.add(color_8);
        colors.add(color_9);
        colors.add(color_10);

    }
}