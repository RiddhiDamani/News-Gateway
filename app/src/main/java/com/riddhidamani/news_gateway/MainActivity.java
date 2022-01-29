package com.riddhidamani.news_gateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.riddhidamani.news_gateway.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Drawer variables
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // Menu variables
    private Menu menu;
    private static final int CATEGORIES_ID = 0;
    private static final int LANGUAGES_ID = 1;
    private static final int COUNTRIES_ID = 2;

    // Article Adapter
    private NewsArticle articleAdapter;

    // ViewPager variables
    private ViewPager2 viewPager;

    // Sources Volley
    private static final String newsSourcesURL = "https://newsapi.org/v2/sources?apiKey=b8988f2d0bbd4c0186dea5c522fefcd0";
    private RequestQueue queue;
    private ActivityMainBinding binding;
    private long start;


    private final HashMap<String, HashSet<String>> regionToSubRegion = new HashMap<>();
    private final HashMap<String, ArrayList<NewsArticle>> subRegionToCountries = new HashMap<>();
    private final ArrayList<NewsArticle> currentCountryList = new ArrayList<>();
    private final ArrayList<String> subRegionDisplayed = new ArrayList<>();

    // data from JSON
    private HashMap<String, String> countryCodeToName;
    private HashMap<String, String> langCodeToName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        queue = Volley.newRequestQueue(this);
        performDownload();

        // loading json data - countryCode, langCode from raw folder.
        LoadAbbrDataStore.loadAbbrData(this);
        countryCodeToName = new HashMap<>(LoadAbbrDataStore.getCountryCodeToName());
        langCodeToName = new HashMap<>(LoadAbbrDataStore.getLangCodeToName());

    }

    public void performDownload() {

        Uri.Builder buildURL = Uri.parse(newsSourcesURL).buildUpon();
        String urlToUse = buildURL.build().toString();

        start = System.currentTimeMillis();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   System.out.println(response.toString());
                } catch (Exception e) {

                }
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    System.out.println(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle" +  item);
            return true;
        }

        if(item.hasSubMenu()) {
            return true;
        }

        String selectedMenuItem = item.toString();
        //processMenu(selectedMenuItem);
        return super.onOptionsItemSelected(item);
    }

    public void makeMenu(View v) {
        menu.clear();

    }

    private void selectItemInDrawer(int position) {
        viewPager.setBackground(null);
//        String currentMediaName = displayMediaNames.get(position);
//        this.currentMediaName = currentMediaName;
//        // get media's id
//        String mediaID = "";
//        for(Source s: sourceList) {
//            if(s.getName().equals(currentMediaName)){
//                mediaID = s.getID();
//                break;
//            }
//        }
//        if(mediaID != null) {
//            Log.d(TAG, "selectItemInDrawer: find mediaID");
//            currentMediaID = mediaID;
//            new Thread(new ArticleLoader(this, mediaID)).start();
//        }else{
//            Log.d(TAG, "selectItemInDrawer: mediaID is null");
//        }

        mDrawerLayout.closeDrawer(findViewById(R.id.c_layout));
    }
}