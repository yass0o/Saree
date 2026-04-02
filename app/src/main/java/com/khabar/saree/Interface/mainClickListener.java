package com.khabar.saree.Interface;


import android.view.View;

import com.khabar.saree.Model.ContentModel;

public interface mainClickListener {
    void onClickItem(ContentModel item, View v);
    void onLongClickItem(ContentModel item, View v);
    void onClickSource(ContentModel item, View v);
}
