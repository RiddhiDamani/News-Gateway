package com.riddhidamani.news_gateway;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.riddhidamani.news_gateway.databinding.ActivityMainBinding;

public class SourcesLoaderRunnable extends AppCompatActivity  {

    private static final String TAG = "SourceLoader";
    private static final String newsSourcesURL = "https://newsapi.org/v2/sources?apiKey=b8988f2d0bbd4c0186dea5c522fefcd0";

    private RequestQueue queue;
    private ActivityMainBinding binding;
    private long start;

}
