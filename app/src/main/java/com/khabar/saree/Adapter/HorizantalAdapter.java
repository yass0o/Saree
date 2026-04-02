package com.khabar.saree.Adapter;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.khabar.saree.Interface.horizantalClickListener;
import com.khabar.saree.R;

import java.util.List;

public class HorizantalAdapter extends RecyclerView.Adapter<HorizantalAdapter.ViewHolder> {
    private List<String> itemList;
    final horizantalClickListener listener;
    Context c;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private boolean isWeatherVisible = false;


    public HorizantalAdapter(List<String> items, horizantalClickListener listener, Context c) {
        this.itemList = items;
        this.listener = listener;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_horizantal_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemList.get(position);
//        if(item.equals("أحوال الطقس")) {
//            holder.main_item_card.setBackgroundColor(getColor(c ,R.color.tabs_weather_background));
//        }else{
//            holder.main_item_card.setBackgroundColor(getColor(c ,R.color.tabs_background));
//        }
        if (selectedPosition == position) {
            holder.title.setSelected(true);
        } else {
            holder.title.setSelected(false);
        }

        if (item.equals("أحوال الطقس")) {
            holder.title.setSelected(isWeatherVisible);
        }

        holder.title.setText(item);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(item, v);
                setSelectedPosition(position);
            }
        });
//        holder.main_item_card.setOnClickListener(v -> {
//            int previousSelectedPosition = selectedPosition;
//            selectedPosition = position;
//
//            // Notify the adapter that an item has been clicked and should be re-bound
//            notifyItemChanged(previousSelectedPosition);
//            notifyItemChanged(selectedPosition);
//        });
        holder.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(item.equals("أحوال الطقس")) {
                    listener.onLongClickItem(item, view);
                }
                return true;
            }
        });
    }

    public void setSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selectedPosition);
    }

    public void setWeather(boolean isWeatherVisible){
        this.isWeatherVisible = isWeatherVisible;
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public MaterialCardView main_item_card;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.source);
            main_item_card = itemView.findViewById(R.id.main_item_card);
        }
    }
}