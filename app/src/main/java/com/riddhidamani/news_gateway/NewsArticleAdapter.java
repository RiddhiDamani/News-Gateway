package com.riddhidamani.news_gateway;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// News Articles (Stories) Adapter
public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleViewHolder> {

    private static final String TAG = "NewsArticleAdapter";
    private final ArrayList<NewsArticle> newsArticlesList;
    private final MainActivity mainActivity;

    // Constructor
    public NewsArticleAdapter(MainActivity mainActivity, ArrayList<NewsArticle> newsArticlesList) {
        this.mainActivity = mainActivity;
        this.newsArticlesList = newsArticlesList;
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.activity_news_article, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticleViewHolder holder, int position) {
        Date date;
        String publishedDateFmt = null;
        NewsArticle newsArticle = newsArticlesList.get(position);

        // Story Title
        holder.news_title.setText(newsArticle.getTitle());
        holder.news_title.setOnClickListener(v -> openURL(newsArticle.getUrl()));


        // Story Date
        String publishedDate = newsArticle.getPublishedAtDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        try {
            date = format.parse(publishedDate);
            SimpleDateFormat changedDateFmt = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
            if (date != null) {
                publishedDateFmt = changedDateFmt.format(date);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Log.d(TAG, "Incorrect Published Date Format:" + exception.getMessage());
        }
        holder.news_date.setText(publishedDateFmt);

        // Story Author
        String newsAuthor = newsArticle.getAuthor();
        if(newsAuthor.equals("null") || newsAuthor.isEmpty()) {
            holder.news_author.setVisibility(View.GONE);
        }
        else {
            holder.news_author.setText(newsArticle.getAuthor());
        }

        // Story Image
        ImageView imageView = holder.news_picture;
        String storyImageURL = newsArticle.getUrlToImage();
        if(storyImageURL.equals("null")) {
            imageView.setImageResource(R.drawable.noimage);
        }
        else {
            Picasso.get().load(storyImageURL).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Story Image Loaded Successfully!" + ((BitmapDrawable) imageView.getDrawable()).getBitmap().getByteCount());
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "Story Image Error: " + e.getMessage());
                }
            });
        }
        holder.news_picture.setOnClickListener(v -> openURL(newsArticle.getUrl()));


        // Story Description
        holder.news_description.setText(newsArticle.getDescription());
        holder.news_description.setMovementMethod(new ScrollingMovementMethod());
        holder.news_description.setOnClickListener(v -> openURL(newsArticle.getUrl()));


        // ViewPager2 Story Page Number
        holder.page_num.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), newsArticlesList.size()));

    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }

    // Click on article title, image, or text to go to extended article on the news source web site
    public void openURL(String websiteURL) {
        Uri mapUri = Uri.parse(websiteURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        mainActivity.startActivity(intent);
    }
}
