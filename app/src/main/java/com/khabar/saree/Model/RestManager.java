package com.khabar.saree.Model;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.khabar.saree.Interface.NewsFetch;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestManager {
    private NewsFetch mNewsService;
    public static String url;

    public NewsFetch getNewsService(Context context, String Url) {
        if (mNewsService == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES) // Set a large connection timeout
                    .readTimeout(5, TimeUnit.MINUTES)    // Set a large read timeout
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .build();
            url=Url;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mNewsService = retrofit.create(NewsFetch.class);
        }
        return mNewsService;
    }


    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }


}