package com.riddhidamani.news_gateway;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NewsArticleDownloader {
    private static final String newsArticleURL = "https://newsapi.org/v2/top-headlines?sources=";
    private static final String apiKey = "b8988f2d0bbd4c0186dea5c522fefcd0";
    private RequestQueue queue;

    public NewsArticleDownloader() {

    }
}
