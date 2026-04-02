package com.khabar.saree.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.khabar.saree.Interface.mainClickListener;
import com.khabar.saree.Model.ContentModel;
import com.khabar.saree.R;
import com.khabar.saree.Utils.utils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.AdkarViewHolder> {
    private ArrayList<ContentModel> mMainArrayList;
    private ArrayList<ContentModel> RefreshMainList;
    Context c;
    final mainClickListener listener;


    public static class AdkarViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView category;
        public TextView displayDate;
        public TextView source;
        public MaterialCardView main_item_card;


        public AdkarViewHolder( View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            category = itemView.findViewById(R.id.category);
            displayDate = itemView.findViewById(R.id.displayDate);
            source = itemView.findViewById(R.id.source);
            main_item_card = itemView.findViewById(R.id.main_item_card);
        }
    }
    public MainAdapter(Context c, ArrayList<ContentModel> MainList, boolean sorting, mainClickListener listener) {
        this.listener = listener;
        mMainArrayList = MainList;
        RefreshMainList = utils.loadData(c,"SelectedNewsList");
        this.c=c;
        if(sorting) {
            Collections.sort(mMainArrayList, new Comparator<ContentModel>() {
                @Override
                public int compare(ContentModel entry1, ContentModel entry2) {
                    String time1 = entry1.getTitle();
                    String time2 = entry2.getTitle();
                    return time1.compareToIgnoreCase(time2);
                }
            });
        }
    }
    @NonNull
    @Override
    public MainAdapter.AdkarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_main_item_2, viewGroup, false);
        MainAdapter.AdkarViewHolder evh = new MainAdapter.AdkarViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainAdapter.AdkarViewHolder adkarViewHolder, int i) {
        final ContentModel currentItem = mMainArrayList.get(i);

        adkarViewHolder.title.setText(currentItem.getTitle());
        if (!currentItem.getCategory().equals("")) {
            adkarViewHolder.category.setText(currentItem.getCategory());
        }else{
            //adkarViewHolder.category.setVisibility(View.GONE);
            adkarViewHolder.category.setText("اخر الأخبار");
        }

        if (!currentItem.getDisplayDate().equals("")) {
            adkarViewHolder.displayDate.setText(currentItem.getDisplayDate());
        }else{
            adkarViewHolder.displayDate.setText("منذ قليل");
        }
        if (!currentItem.getSource().equals("")) {
            adkarViewHolder.source.setText(currentItem.getSource());
        }else{
            adkarViewHolder.source.setVisibility(View.GONE);
        }

        adkarViewHolder.source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickSource(currentItem, v);
            }
        });


       // Log.e("RefreshMainList", "onBindViewHolder: "+RefreshMainList.size() );
//        for (int k=0;k<RefreshMainList.size();k++){
//            Log.e("RefreshMainList", RefreshMainList.get(k).getTitle()+" : "+currentItem.getTitle() );
//            if(RefreshMainList.get(k).getTitle().equals(currentItem.getTitle())){
//                adkarViewHolder.main_item_card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.purple_700));
//                adkarViewHolder.title.setTextColor(ContextCompat.getColor(c, R.color.white));
//            }
//            else{
//                adkarViewHolder.main_item_card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.white));
//                adkarViewHolder.title.setTextColor(ContextCompat.getColor(c, R.color.dark_orange));
//            }
//        }

        if(utils.getSelected(c,""+currentItem.getMain_id()) == 1 && utils.getSelected(c,"sharestate") == 1){
            adkarViewHolder.main_item_card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.selected_background));
            adkarViewHolder.title.setTextColor(ContextCompat.getColor(c, R.color.white));
        }else{
            Log.e("positionclicked", "onBindViewHolder: else" );
            adkarViewHolder.main_item_card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.white));
            adkarViewHolder.title.setTextColor(ContextCompat.getColor(c, R.color.text_secondary));
        }

//        if(currentItem.getSelected()!=null && currentItem.getSelected().equals("1")){
//            adkarViewHolder.main_item_card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.purple_700));
//            adkarViewHolder.title.setTextColor(ContextCompat.getColor(c, R.color.white));
//        }else{
//            adkarViewHolder.main_item_card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.white));
//            adkarViewHolder.title.setTextColor(ContextCompat.getColor(c, R.color.dark_orange));
//        }

        adkarViewHolder.main_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(currentItem, v);
            }
        });

        adkarViewHolder.main_item_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClickItem(currentItem, v);
                return false;
            }
        });

    }

    public void filterList(ArrayList<ContentModel> filteredList) {
        mMainArrayList = filteredList;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mMainArrayList.size();
    }
}
