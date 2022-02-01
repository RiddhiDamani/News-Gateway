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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private final CategorySelection pickedCategory = new CategorySelection();
    private static final String newsSourcesURL = "https://newsapi.org/v2/sources";
    private static final String newsArticleURL = "https://newsapi.org/v2/top-headlines";
    private static final String apiKey = "b8988f2d0bbd4c0186dea5c522fefcd0";

    // Drawer variables
    private final HashMap<String, Integer> colorDrawerMap = new HashMap<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // ViewPager2
    private ViewPager2 viewPager;
    private ArrayAdapter<String> arrayAdapter;
    private NewsArticleAdapter newsArticleAdapter;
    ArrayList<NewsArticle> articleList = new ArrayList<>();
    private final ArrayList<NewsArticle> currentNewsArticleList = new ArrayList<>();

    // Menu variables
    private Menu opt_menu;
    private SubMenu optMenuTopics, optMenuCountries, optMenuLanguages;
    public static String opt_subMenu_All = "All";
    private static final String opt_subMenu_topics = "Topics";
    private static final String opt_subMenu_countries = "Countries";
    private static final String getOpt_subMenu_languages = "Languages";

    // Json data mapping for country code and language code
    private HashMap<String, String> countryCodeToName;
    private HashMap<String, String> langCodeToName;

    // APIs
    private final ArrayList<Sources> srcs = new ArrayList<>();
    private final ArrayList<String> listOfNewsSrcNames = new ArrayList<>();
    private final ArrayList<String> listOfTopics = new ArrayList<>();
    private final ArrayList<String> listOfCountries = new ArrayList<>();
    private final ArrayList<String> listOfLanguages = new ArrayList<>();

    // API FOR UI
    private ArrayList<Sources> sourceList = new ArrayList<>();
    private ArrayList<String> sourceNewsName = new ArrayList<>();
    private final ArrayList<String> sourceNewsNameAll = new ArrayList<>();
    private ArrayList<String> topicsArr = new ArrayList<>();
    private final ArrayList<String> countriesArr = new ArrayList<>();
    private final ArrayList<String> languagesArr = new ArrayList<>();

    // Extra Credit Fields: Color Menu and State Variables
    private final HashMap<String, Integer> colorMap = new HashMap<>();
    private final ArrayList<Integer> arrOfColors = new ArrayList<>();
    private String currSrcNewsID = "";
    private String currSrcNewsName;

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // binding = ActivityMainBinding.inflate(getLayoutInflater());
        // setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);

        // Setting up Drawer Layout
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer_list);
        //mDrawerList = binding.leftDrawerList;
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItemInDrawer(position);
                    mDrawerLayout.closeDrawer(findViewById(R.id.c_layout));
                }
        );
        // Drawer Toggle
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

        // Setting up ViewPager2 Adapter
        newsArticleAdapter = new NewsArticleAdapter(this, currentNewsArticleList);
        //viewPager = binding.viewpager;
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(newsArticleAdapter);

        // Extra Credit 2: Display News Categories in the menus with different color.
        // Then, color the news sources according to their category color
        colorMenuCategories(this);

    }

    // Extra Credit: Saving App State
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("news_topic", pickedCategory.getTopic());
        bundle.putString("news_country", pickedCategory.getCountry());
        bundle.putString("news_language", pickedCategory.getLanguage());
        bundle.putStringArrayList("news_source_name", sourceNewsName);
        bundle.putString("current_news_src_id", currSrcNewsID);
        bundle.putString("current_news_src_name", currSrcNewsName);
        super.onSaveInstanceState(bundle);
    }

    // Extra Credit: Restoring Saved App State
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setBackground(null);
        pickedCategory.setTopic(savedInstanceState.getString("news_topic"));
        pickedCategory.setCountry(savedInstanceState.getString("news_country"));
        pickedCategory.setLanguage(savedInstanceState.getString("news_language"));
        sourceNewsName = savedInstanceState.getStringArrayList("news_source_name");
        drawerItemColorBuild();
        currSrcNewsID = savedInstanceState.getString("current_news_src_id");
        currSrcNewsName = savedInstanceState.getString("current_news_src_name");
        setTitle(currSrcNewsName);
        queue = Volley.newRequestQueue(this);
        performArticlesDownload(currSrcNewsID);
    }

    // color-coded opt_sub_menu
    private void colorMenuCategories(Context context) {
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat1));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat2));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat3));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat4));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat5));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat6));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat7));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat8));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat9));
        arrOfColors.add(ContextCompat.getColor(context, R.color.subMenuCat10));
    }

    // onPostCreate
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    // onConfigurationChanged
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        if(optMenuTopics == null) {
            optSubMenuBuild();
        }
        return true;
    }

    // onOptionsItemSelected
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle" +  item);
            return true;
        }
        // assigning the selected item to a string variable
        String optMenuSelected = item.toString();
        // passing the selected item to executeMenuSelection for further processing
        executeMenuSelection(optMenuSelected);
        return super.onOptionsItemSelected(item);
    }

    // selected option menu gets checked against the conditions (Topic, Country, Language) based on
    // individual flag values the news gets displayed on the viewpager2
    private void executeMenuSelection(String optMenuSelected) {
        String pickedTopic,  pickedCountry, pickedLanguage;
        boolean checkAllTopics, checkAllCountries, checkAllLanguages;
        switch (optMenuSelected) {
            case opt_subMenu_topics:
                pickedCategory.setCategoryTopicFlag();
                return;
            case opt_subMenu_countries:
                pickedCategory.setCategoryCountryFlag();
                return;
            case getOpt_subMenu_languages:
                pickedCategory.setCategoryLanguageFlag();
                return;
            default:
                if (pickedCategory.isCategoryTopicFlag()) pickedCategory.setTopic(optMenuSelected);
                if (pickedCategory.isCategoryCountryFlag()) pickedCategory.setCountry(optMenuSelected);
                if (pickedCategory.isCategoryLanguageFlag()) pickedCategory.setLanguage(optMenuSelected);
                break;
        }
        pickedTopic = pickedCategory.getTopic();
        pickedCountry = pickedCategory.getCountry();
        pickedLanguage = pickedCategory.getLanguage();
        checkAllTopics = pickedCategory.getTopic().equals("All");
        checkAllCountries = pickedCategory.getCountry().equals("All");
        checkAllLanguages = pickedCategory.getLanguage().equals("All");

        sourceNewsName.clear();
        performCheck(pickedTopic, checkAllTopics, pickedCountry, checkAllCountries, pickedLanguage, checkAllLanguages);
        if(sourceNewsName.size() < 1) {
            noNewsSourcesAlertDialog(pickedTopic, pickedCountry, pickedLanguage);
        }
        arrayAdapter.notifyDataSetChanged();
        setTitle("News Gateway" + " (" + sourceNewsName.size() + ") ");
    }

    // perform validations
    private void performCheck(String pickedTopic, boolean checkAllTopics, String pickedCountry, boolean checkAllCountries, String  pickedLanguage, boolean checkAllLanguages) {
        for(int i = 0; i < sourceList.size(); i++) {
            Sources source = sourceList.get(i);
            String sourceTopic = source.getCategory();
            String sourceCountryCode = source.getCountry();
            String sourceCountry = countryCodeToName.get(sourceCountryCode.toUpperCase());
            String sourceLanguageCode = source.getLanguage();
            String sourceLanguage = langCodeToName.get(sourceLanguageCode.toUpperCase());
            String sourceName = source.getName();

            if(checkAllTopics && checkAllCountries && checkAllLanguages) {
                sourceNewsName.addAll(sourceNewsNameAll);
                break;
            }else if(checkAllTopics && checkAllCountries) {
                assert sourceLanguage != null;
                if(sourceLanguage.equals(pickedLanguage)) {
                    sourceNewsName.add(sourceName);
                }
            }else if(checkAllTopics && checkAllLanguages){
                assert sourceCountry != null;
                if(sourceCountry.equals(pickedCountry)) {
                    sourceNewsName.add(sourceName);
                }
            }else if (checkAllCountries && checkAllLanguages) {
                if(sourceTopic.equals(pickedTopic)) {
                    sourceNewsName.add(sourceName);
                }
            }else if (checkAllTopics) {
                assert sourceCountry != null;
                if(sourceCountry.equals(pickedCountry)) {
                    assert sourceLanguage != null;
                    if (sourceLanguage.equals(pickedLanguage)) {
                        sourceNewsName.add(sourceName);
                    }
                }
            }else if (checkAllCountries) {
                if(sourceTopic.equals(pickedTopic)) {
                    assert sourceLanguage != null;
                    if (sourceLanguage.equals(pickedLanguage)) {
                        sourceNewsName.add(sourceName);
                    }
                }
            }else if (checkAllLanguages) {
                if(sourceTopic.equals(pickedTopic)) {
                    assert sourceCountry != null;
                    if (sourceCountry.equals(pickedCountry)) sourceNewsName.add(sourceName);
                }
            }else {
                if(sourceTopic.equals(pickedTopic)) {
                    assert sourceCountry != null;
                    if (sourceCountry.equals(pickedCountry)) {
                        assert sourceLanguage != null;
                        if (sourceLanguage.equals(pickedLanguage)) {
                            sourceNewsName.add(sourceName);
                        }
                    }
                }
            }
        }
        pickedCategory.completeFlag();
    }

    // If the specified criteria result in no News Sources at all, an AlertDialog should be displayed
    private void noNewsSourcesAlertDialog(String pickedTopic, String pickedCountry, String pickedLanguage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No News Sources");
        String dialogMsg = "No News Sources match your criteria: " +
                "\nTopic: " + pickedTopic +
                "\nCountry: " + pickedCountry +
                "\nLanguage: " + pickedLanguage;
        builder.setMessage(dialogMsg);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // source volley download data from source news api
    public void performDownload() {
        Uri.Builder buildURL = Uri.parse(newsSourcesURL).buildUpon();
        buildURL.appendQueryParameter("apikey", apiKey);
        String urlToUse = buildURL.build().toString();

        // Creating Response Listener - listener will receive the response from my request to get internet content
        Response.Listener<JSONObject> listener = response -> {
            try {
                JSONArray sources = response.getJSONArray("sources");
                for(int i = 0; i < sources.length(); i++) {
                    JSONObject jsonSrc = (JSONObject) sources.get(i);
                    String id = jsonSrc.getString("id");
                    String name = jsonSrc.getString("name");
                    String category = jsonSrc.getString("category");
                    String language = jsonSrc.getString("language");
                    String country = jsonSrc.getString("country");
                    Sources srcData = new Sources(id, name, category,language,country);
                    // eliminating repeated data
                    if(!listOfTopics.contains(category)) {
                        listOfTopics.add(category);
                    }
                    if(!listOfCountries.contains(country)) {
                        listOfCountries.add(country);
                    }
                    if(!listOfLanguages.contains(language)) {
                        listOfLanguages.add(language);
                    }
                    if(!listOfNewsSrcNames.contains(name)) {
                        listOfNewsSrcNames.add(name);
                    }
                    srcs.add(srcData);
                }
                runOnUiThread(() -> {
                    newsSrcNamesBuild(listOfNewsSrcNames);
                    topicListBuild(listOfTopics);
                    fetchSrcs(srcs);
                    countryListBuild(listOfCountries);
                    languageListBuild(listOfLanguages);
                    drawerItemColorBuild();
                });
            } catch (Exception exception) {
                exception.printStackTrace();
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

    // Story (News Article Download) Download using Volley
    public void performArticlesDownload(String newsSrcID) {
        Uri.Builder buildURL = Uri.parse(newsArticleURL).buildUpon();
        buildURL.appendQueryParameter("sources", newsSrcID);
        buildURL.appendQueryParameter("apikey", apiKey);
        String urlToUse = buildURL.build().toString();
        articleList.clear();
        currentNewsArticleList.clear();
        Response.Listener<JSONObject> listener = response -> {
            try {
                JSONArray storiesArray = response.getJSONArray("articles");
                for(int i = 0; i < storiesArray.length(); i++) {
                    JSONObject jsonStories = (JSONObject) storiesArray.get(i);
                    String author = jsonStories.getString("author");
                    String title = jsonStories.getString("title");
                    String description = jsonStories.getString("description");
                    String url = jsonStories.getString("url");
                    String urlToImage = jsonStories.getString("urlToImage");
                    String publishedAt = jsonStories.getString("publishedAt");
                    NewsArticle story = new NewsArticle();
                    story.setAuthor(author);
                    story.setTitle(title);
                    story.setDescription(description);
                    story.setUrl(url);
                    story.setUrlToImage(urlToImage);
                    story.setPublishedAtDate(publishedAt);
                    articleList.add(story);
                }
                runOnUiThread(() -> newsArticlesBuild(articleList));
            } catch (Exception exception) {
                exception.printStackTrace();
                Log.d(TAG, "processJSON: " + exception.getMessage());
            }
        };
        Response.ErrorListener error = error1 -> {
            try {
                JSONObject jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "Error jsonObject: " + jsonObject);
            } catch (JSONException exception) {
                exception.printStackTrace();
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

    // Setting Articles on the viewPager2
    @SuppressLint("NotifyDataSetChanged")
    public void newsArticlesBuild(ArrayList<NewsArticle> articleList) {
        currentNewsArticleList.clear();
        setTitle(currSrcNewsName);
        currentNewsArticleList.addAll(articleList);
        newsArticleAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }

    // News Source Names Build
    public void newsSrcNamesBuild(ArrayList<String> nameList) {
        if(nameList == null) return;
        if(sourceNewsName.isEmpty()) {
            sourceNewsName = nameList;
        }
        sourceNewsNameAll.addAll(nameList);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, sourceNewsName));

        // show drawer icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setTitle("News Gateway ("+  sourceNewsName.size() + ")");

    }

    // Building Topic List
    public void topicListBuild(ArrayList<String> categoryList) {
        if(categoryList == null) return;
        topicsArr = categoryList;
    }

    // Fetching Sources
    public void fetchSrcs(ArrayList<Sources> sources) {
        if(sources == null) {
            Log.d(TAG, "getSources: sources is null" );
            return;
        }
        sourceList = sources;
    }

    // Countries Build
    public void countryListBuild(ArrayList<String> countryLists) {
        if (countryLists == null) return;

        for (int i = 0; i < countryLists.size(); i++) {
            String countryCode = countryLists.get(i).toUpperCase();
            if(countryCodeToName.containsKey(countryCode)) {
                String countryName = countryCodeToName.get(countryCode);
                countriesArr.add(countryName);
            }
        }
        Collections.sort(countriesArr);
    }

    // Language s Build
    public void languageListBuild(ArrayList<String> languageLists) {
        if (languageLists == null)  return;
        for(int i = 0; i < languageLists.size(); i++) {
            String languageCode = languageLists.get(i).toUpperCase();
            if(langCodeToName.containsKey(languageCode)) {
                String languageName = langCodeToName.get(languageCode);
                languagesArr.add(languageName);
            }
        }
        Collections.sort(languagesArr);
        if(opt_menu != null) {
            optSubMenuBuild();
        }

    }

    // Building Color Coded Drawer Src Names
    public void drawerItemColorBuild() {
        for(int i = 0; i < sourceList.size(); i++) {
            int subMenuOptionColor;
            String subMenuOption;
            Sources dataSrc = sourceList.get(i);
            String dataSrcCategory = dataSrc.getCategory();
            for (String k: colorMap.keySet()) {
                subMenuOption = k; subMenuOptionColor = colorMap.get(k);
                if(dataSrcCategory.equals(subMenuOption)) {
                    String newsSrcName = dataSrc.getName();
                    colorDrawerMap.put(newsSrcName, subMenuOptionColor);
                    break;
                }
            }
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_item, sourceNewsName) {
            // Overriding to set the color coded Src Name in the Drawer
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String currentSrcName;
                TextView cTV;
                View v;
                v = super.getView(position, convertView, parent);
                cTV = v.findViewById(R.id.text_view);
                currentSrcName = cTV.getText().toString();
                if(colorDrawerMap.containsKey(currentSrcName)) {
                    int colorCode = colorDrawerMap.get(currentSrcName);
                    cTV.setTextColor(colorCode);
                }
                return v;
            }
        };
        // setting adapter
        mDrawerList.setAdapter(arrayAdapter);
        // notifying adapter about the changes
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
    }

    // selecting item from the drawer
    @SuppressLint("NotifyDataSetChanged")
    private void selectItemInDrawer(int position) {
        String srcNewsID = "";
        viewPager.setBackground(null);
        String currentSourceNews = sourceNewsName.get(position);
        currentNewsArticleList.clear();
        this.currSrcNewsName = currentSourceNews;
        for(Sources s: sourceList) {
            if(s.getName().equals(currentSourceNews)){
                srcNewsID = s.getId();
                break;
            }
        }
        if(srcNewsID != null) {
            currSrcNewsID = srcNewsID;
            // downloading news article based on current news source id selected.
            queue = Volley.newRequestQueue(this);
            performArticlesDownload(currSrcNewsID);
        }
        else {
            Log.d(TAG, "selectItemInDrawer: current news source id is null");
        }
        mDrawerLayout.closeDrawer(findViewById(R.id.c_layout));
    }

    // options menu build
    private void optSubMenuBuild() {
        int cc = 0;
        if(optMenuTopics == null) {
            optMenuTopics = opt_menu.addSubMenu(opt_subMenu_topics);
            optMenuTopics.add(opt_subMenu_All);
            optMenuCountries = opt_menu.addSubMenu(opt_subMenu_countries);
            optMenuCountries.add(opt_subMenu_All);
            optMenuLanguages = opt_menu.addSubMenu(getOpt_subMenu_languages);
            optMenuLanguages.add(opt_subMenu_All);
        }
        for(String c : countriesArr) {
            optMenuCountries.add(c);
        }
        for(String l : languagesArr){
            optMenuLanguages.add(l);
        }
        for(int i = 0; i < topicsArr.size(); i++) {
            String c = topicsArr.get(i);
            MenuItem ct = optMenuTopics.add(c);
            if(topicsArr.get(i) == null) {
                cc =0;
            }
            int cID = arrOfColors.get(cc);
            colorMap.put(c, cID);
            SpannableString s = new SpannableString(c);
            s.setSpan(new ForegroundColorSpan(cID), 0, s.length(), 0);
            ct.setTitle(s);
            cc++;
        }
    }
}