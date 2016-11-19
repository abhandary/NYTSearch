package com.example.akshayb.nytsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.R.attr.resource;

/**
 * Created by akshayb on 11/15/16.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    private static class ViewHolder {
        ImageView ivImage;
        TextView  tvTitle;
    }

    private static class TextOnlyViewHolder {
        TextView  tvTitle;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the data item for the position
        Article article = getItem(position);

        if (article.getThumbNail().length() == 0) {
            TextOnlyViewHolder viewHolder;

            // check to see if the view is being recycled
            if (convertView == null || convertView.getTag(R.string.text_only_view_holder) == null) {
                //  if it's not being recycled then inflate the view
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.item_article_text_only, parent, false);
                viewHolder = new TextOnlyViewHolder();
                convertView.setTag(R.string.text_only_view_holder, viewHolder);

                // find the title view
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            } else {
                // get the view holder associated with the recycled convertView
                viewHolder = (TextOnlyViewHolder) convertView.getTag(R.string.text_only_view_holder);
            }

            // set the title on the view holder
            viewHolder.tvTitle.setText(article.getHeadline());
        }
        else {
            ViewHolder viewHolder;

            // check to see if the view is being recycled
            if (convertView == null || convertView.getTag(R.string.text_and_image_view_holder) == null) {
                // if it's not being recycled then inflate the view
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.item_article_result, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(R.string.text_and_image_view_holder, viewHolder);

                // find the views
                viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            } else {
                // get the view holder associated with the recycled convertView
                viewHolder = (ViewHolder) convertView.getTag(R.string.text_and_image_view_holder);
            }

            // clear out the recycled image from convertview from last time.
            viewHolder.ivImage.setImageResource(0);

            // set the title of the view holder
            viewHolder.tvTitle.setText(article.getHeadline());

            // populate the thumbnail image
            // download the image in the background
            String thumbNail = article.getThumbNail();

            if (!TextUtils.isEmpty(thumbNail)) {
                Picasso.with(getContext()).load(thumbNail).into(viewHolder.ivImage);
            }
        }

        return convertView;
    }
}
