package com.khabar.saree.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.khabar.saree.R;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.khabar.saree.Model.BayanModel;
import com.khabar.saree.Utils.utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BayanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_IMAGE = 2;
    Context c;
    String copyMode;
    private List<BayanModel> list;

    public BayanAdapter(Context c,String copyMode, List<BayanModel> list) {
        this.list = list;
        this.c = c;
        this.copyMode = copyMode;
    }

    public void setCopyMode(String copyMode) {
        this.copyMode = copyMode;
    }

    @Override
    public int getItemViewType(int position) {
        BayanModel item = list.get(position);

        if (item.getTitle().isEmpty()) {
            return TYPE_IMAGE;
        } else {
            return TYPE_TEXT;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= TEXT VIEW =================
    public static class TextHolder extends RecyclerView.ViewHolder {

        TextView title,displayDate,category,source;
        MaterialCardView main_item_card;
        ImageView image;
        public TextHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.bayan_image);
            title = itemView.findViewById(R.id.title);
            displayDate = itemView.findViewById(R.id.displayDate);
            category = itemView.findViewById(R.id.category);
            source = itemView.findViewById(R.id.source);
            main_item_card = itemView.findViewById(R.id.main_item_card);
        }
    }

    // ================= IMAGE VIEW =================
    public static class ImageHolder extends RecyclerView.ViewHolder {
        MaterialCardView main_item_card;
        ImageView image;
        TextView displayDate,category,source;
        public ImageHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.bayan_image);
            displayDate = itemView.findViewById(R.id.displayDate);
            category = itemView.findViewById(R.id.category);
            source = itemView.findViewById(R.id.source);
            main_item_card = itemView.findViewById(R.id.main_item_card);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_bayan, parent, false);
            return new ImageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bayan, parent, false);
            return new TextHolder(view);
        }
    }

    public void updateData(List<BayanModel> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        BayanModel item = list.get(position);
        ArrayList<BayanModel> SelectedNewsList = new ArrayList<>();;
        if (holder.getItemViewType() == TYPE_IMAGE) {

            ImageHolder h = (ImageHolder) holder;

            h.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    utils.shareImageFromUrl(v.getContext(), item.getThumbnail(),"");
                    return true;
                }
            });
            h.displayDate.setText(item.getDisplayDate());
            h.category.setVisibility(View.GONE);
            h.source.setVisibility(View.GONE);
            if(!item.getThumbnail().isEmpty()) {
                Picasso.get()
                        .load(item.getThumbnail())
                        .into(h.image);
            }

        } else {

            TextHolder h = (TextHolder) holder;

            if (item.getThumbnail() != null && !item.getThumbnail().isEmpty()) {
                h.image.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(item.getThumbnail())
                        .into(h.image);

            } else {
                h.image.setVisibility(View.GONE);
            }
            String text = item.getTitle().replace("**", "");
            text = text.replaceAll("(?m)^#.*$", "");
            text = text.replaceAll("(?s)﴿.*?﴾", "");
            String[] blocks = text.split("\\n\\s*\\n");
            String biggestBlock = "";
            int maxLength = 0;

            for (String block : blocks) {
                block = block.trim();

                if (block.isEmpty()) continue;

                int len = block.length();

                if (len > maxLength) {
                    maxLength = len;
                    biggestBlock = block;
                }
            }
            Log.d("logtitle", "Block " + biggestBlock);
            String formated_msg = item.getTitle_formatted()+"\n" +"\n" +biggestBlock;
            h.title.setText(formated_msg);
            h.category.setText(item.getBayan_number());
            h.source.setVisibility(View.GONE);
            h.displayDate.setText(item.getDisplayDate());

            h.main_item_card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (item.getThumbnail() != null && !item.getThumbnail().isEmpty()) {
                        utils.shareImageFromUrl(v.getContext(), item.getThumbnail(),formated_msg);
                    }else {
                        utils.copyToClipboard(c, formated_msg);
                    }
                    return true;
                }
            });
            h.main_item_card.setOnClickListener(v -> {

                String textToCopy;

                switch (copyMode) {
                    case "formatted":
                        textToCopy = formated_msg;
                        break;

                    default:
                        textToCopy = item.getTitle();
                        break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(c);

                TextView textView = new TextView(c);
                textView.setText(textToCopy);
                textView.setTextIsSelectable(true);
                // 🔥 FONT SIZE
                textView.setTextSize(20); // in SP

                // 🔥 FONT (from assets or system font)
                Typeface typeface = ResourcesCompat.getFont(c, R.font.tajawal_medium);
                textView.setTypeface(typeface);
                textView.setPadding(60, 60, 60, 60);

                builder.setView(textView);

                builder.setPositiveButton("Copy", (dialog, which) -> {
                    utils.copyToClipboard(c, formated_msg);
                });

                builder.setNegativeButton("Close", null);

                builder.show();
            });



//            h.main_item_card.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    utils.shareMessage(c,formated_msg);
//                    return true;
//                }
//            });
        }
    }
}

