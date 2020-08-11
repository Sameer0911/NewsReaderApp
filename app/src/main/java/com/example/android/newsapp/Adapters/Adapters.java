package com.example.android.newsapp.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.newsapp.Models.News;
import com.example.android.newsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapters {
    public static class NewsAdapter extends ArrayAdapter<News> {


        public NewsAdapter(Activity context, ArrayList<News> designClassAdapters) {
            // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
            // the second argument is used when the ArrayAdapter is populating a single TextView.
            // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
            // going to use this second argument, so it can be any value. Here, we used 0.
            super(context, 0, designClassAdapters);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if the existing view is being reused, otherwise inflate the view
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.moj_layout, parent, false);
            }
            News currentItem = getItem(position);

            String type = currentItem.getDate();
            String title = currentItem.getWebTitle();
            String selectionName = currentItem.getCategory();


            TextView contactView1 = (TextView) listItemView.findViewById(R.id.date);
            contactView1.setText(type);

            TextView contactView2 = (TextView) listItemView.findViewById(R.id.category);
            contactView2.setText("Category: " + selectionName);

            TextView contactView3 = (TextView) listItemView.findViewById(R.id.web_title);
            contactView3.setText(title);

            ImageView imageView = (ImageView) listItemView.findViewById(R.id.thumbnail_image);
            Picasso.with(getContext()).load(currentItem.getmThumbUrl()).into(imageView);

            return listItemView;
        }
    }
}
