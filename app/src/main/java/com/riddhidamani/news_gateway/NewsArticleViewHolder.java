package com.riddhidamani.news_gateway;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// News Articles (Stories) View Holder that extends RecyclerView ViewHolder
public class NewsArticleViewHolder extends RecyclerView.ViewHolder {

    // Layout fields
    TextView news_title;
    TextView news_date;
    TextView news_author;
    ImageView news_picture;
    TextView news_description;
    TextView page_num;

    public NewsArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        news_title = itemView.findViewById(R.id.news_title);
        news_date = itemView.findViewById(R.id.news_date);
        news_author = itemView.findViewById(R.id.news_author);
        news_picture = itemView.findViewById(R.id.news_picture);
        news_description = itemView.findViewById(R.id.news_description);
        page_num = itemView.findViewById(R.id.page_num);
    }
}
