package com.riddhidamani.news_gateway;

import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleViewHolder> {

    private static final String TAG = "NewsArticleAdapter";
    private final MainActivity mainActivity;
    private final ArrayList<NewsArticle> newsArticlesList;

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
        NewsArticle newsArticle = newsArticlesList.get(position);

        // Title
        holder.news_title.setText(newsArticle.getTitle());

        // Date
        String dateStr = newsArticle.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        Date date = null;
        String formattedDate = null;
        try {
            date = format.parse(dateStr);
            Log.d(TAG, "Date: " + date.toString()); // Sat Jan 02 00:00:00 GMT 2010

            SimpleDateFormat desiredFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
            formattedDate = desiredFormat.format(date);
            Log.d(TAG, "Date after formatted: " + formattedDate);

        } catch (ParseException e) {
            Log.d(TAG, "Date: wrong format" + e.getMessage());
        }
        holder.news_date.setText(formattedDate);

        // Author
        String newsAuthor = newsArticle.getAuthor();
        if(newsAuthor.equals("null") || newsAuthor.isEmpty()) {
            holder.news_author.setVisibility(View.GONE);
        }else{
            holder.news_author.setText(newsArticle.getAuthor());
        }

        // Image
        ImageView imageView = holder.news_picture;
        String imageUrl = newsArticle.getUrlToImage();
        if(imageUrl.equals("null")) {
            imageView.setImageResource(R.drawable.noimage);
        }
        else{
            loadImagePicasso(imageView, imageUrl);
        }


        // Description
        holder.news_description.setText(newsArticle.getDescription());

        // Page Number
        holder.page_num.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), newsArticlesList.size()));

    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }

    private void loadImagePicasso(ImageView imageView, String imageURL) {

        Picasso.get().load(imageURL).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView, new Callback() {
          @Override
          public void onSuccess() {
              Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) imageView.getDrawable()).getBitmap().getByteCount());
          }

          @Override
            public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
        });
    }

//    public void openWebsite(String websiteURL) {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
//        startActivity(browserIntent);
//
//    }
}
