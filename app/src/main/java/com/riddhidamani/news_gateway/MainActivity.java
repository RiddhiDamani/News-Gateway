package com.riddhidamani.news_gateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Drawer variables
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // Menu variables
    private Menu opt_menu;
    private SubMenu menu_opt_topics;
    private SubMenu menu_opt_countries;
    private SubMenu menu_opt_languages;

    // ViewPager variables
    private ViewPager2 viewPager;

    private final HashMap<String, HashSet<String>> regionToSubRegion = new HashMap<>();
    private final HashMap<String, ArrayList<NewsArticle>> subRegionToCountries = new HashMap<>();
    private final ArrayList<NewsArticle> currentCountryList = new ArrayList<>();
    private final ArrayList<String> subRegionDisplayed = new ArrayList<>();

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        if(menu_opt_topics == null) {
            // Setup sub menu
        }
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle" +  item);
            return true;
        }

        String selectedMenuItem = item.toString();
        //processMenu(selectedMenuItem);
        return super.onOptionsItemSelected(item);
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