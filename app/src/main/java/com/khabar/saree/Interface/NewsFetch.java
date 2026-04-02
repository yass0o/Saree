package com.khabar.saree.Interface;

import com.khabar.saree.Model.BayanModel;
import com.khabar.saree.Model.BayanResponse;
import com.khabar.saree.Model.ContentModel;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;

public interface NewsFetch {
    @GET("files/get_news.php")
    Call<List<ContentModel>> get_news(
            @Query("limit") String limit,
            @Query("title") String title,
            @Query("category") String category,
            @Query("source") String source,
            @Query("created_date") String date
    );
    @GET("files/get_news_bayan.php")
    Call<BayanResponse> get_news_bayan();
    @GET("files/get_weather.php")
    Call<ContentModel> get_weather();
}
