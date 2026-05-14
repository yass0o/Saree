package com.khabar.saree.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khabar.saree.Adapter.BayanAdapter;
import com.khabar.saree.Model.BayanModel;
import com.khabar.saree.Model.BayanModel;
import com.khabar.saree.Model.BayanResponse;
import com.khabar.saree.Model.RestManager;
import com.khabar.saree.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BayanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    AppCompatRadioButton rb_full, rb_formatted;

    String copyMode = "full";

    private BayanAdapter adapter;
    private List<BayanModel> list = new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable refreshRunnable;
    private static final int REFRESH_INTERVAL = 10000;

    private boolean isLoading = false;

    // =========================
    // ON CREATE
    // =========================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_bayan);

        rb_full = findViewById(R.id.rb_full);
        rb_formatted = findViewById(R.id.rb_formatted);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.pullToRefresh);

        // =========================
        // SETTINGS
        // =========================
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String mode = prefs.getString("copy_mode", "full");

        if ("formatted".equals(mode)) {
            rb_formatted.setChecked(true);
            copyMode = "formatted";
        } else {
            rb_full.setChecked(true);
            copyMode = "full";
        }

        SharedPreferences.Editor editor = prefs.edit();

        rb_full.setOnClickListener(v -> {
            copyMode = "full";
            editor.putString("copy_mode", "full").apply();
            adapter.setCopyMode(copyMode);
            adapter.notifyDataSetChanged();
        });

        rb_formatted.setOnClickListener(v -> {
            copyMode = "formatted";
            editor.putString("copy_mode", "formatted").apply();
            adapter.setCopyMode(copyMode);
            adapter.notifyDataSetChanged();
        });

        // =========================
        // RECYCLER SETUP
        // =========================
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        // =========================
        // LOAD CACHE FIRST
        // =========================
        list = loadCache();

        adapter = new BayanAdapter(this, copyMode, list);
        recyclerView.setAdapter(adapter);

        // =========================
        // INITIAL LOAD
        // =========================
        loadData();

        // =========================
        // PULL TO REFRESH
        // =========================
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    // =========================
    // AUTO REFRESH
    // =========================
    @Override
    protected void onResume() {
        super.onResume();
        startAutoRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoRefresh();
    }

    private void startAutoRefresh() {
        if (refreshRunnable == null) {
            refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    loadData();
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            };
        }
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void stopAutoRefresh() {
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    // =========================
    // LOAD DATA (API)
    // =========================
    private void loadData() {

        if (isLoading) return;
        isLoading = true;

        swipeRefreshLayout.setRefreshing(true);

        RestManager mManager = new RestManager();

        Call<BayanResponse> call =
                mManager.getNewsService(this, "https://thomaihtadayt.com/")
                        .get_news_bayan();

        call.enqueue(new Callback<BayanResponse>() {

            @Override
            public void onResponse(Call<BayanResponse> call, Response<BayanResponse> response) {

                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {

                    List<BayanModel> newList = response.body().getData();

                    if (newList == null || newList.isEmpty()) return;

                    // =========================
                    // CHECK OLD STATE BEFORE UPDATE
                    // =========================
                    boolean wasEmpty = list.isEmpty();
                    String oldTopId = wasEmpty ? null : list.get(0).getId();

                    int newCount = 0;
                    boolean hasNewItems = false;

                    if (!wasEmpty) {
                        for (BayanModel item : newList) {
                            if (item.getId().equals(oldTopId)) {
                                break;
                            }
                            newCount++;
                        }
                        hasNewItems = newCount > 0;
                    }

                    // =========================
                    // UPDATE DATA
                    // =========================
//                    list.clear();
//                    list.addAll(newList);

                    adapter.updateData(newList);
                    saveCache(newList);

                    // =========================
                    // SHOW NEW ITEM NOTICE
                    // =========================
                    if (hasNewItems) {
                        onNewItemDetected(newCount);
                    }
                }
            }
            @Override
            public void onFailure(Call<BayanResponse> call, Throwable t) {

                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);

                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    // =========================
    // SAVE CACHE (TODAY ONLY)
    // =========================
    private void saveCache(List<BayanModel> data) {

        SharedPreferences prefs = getSharedPreferences("bayan_cache", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(data);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        editor.putString("data", json);
        editor.putString("date", today);
        editor.apply();
    }

    // =========================
    // LOAD CACHE (ONLY TODAY)
    // =========================
    private List<BayanModel> loadCache() {

        SharedPreferences prefs = getSharedPreferences("bayan_cache", MODE_PRIVATE);

        String savedDate = prefs.getString("date", "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        if (!today.equals(savedDate)) {
            prefs.edit().clear().apply();
            return new ArrayList<>();
        }

        String json = prefs.getString("data", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BayanModel>>() {}.getType();
            return gson.fromJson(json, type);
        }

        return new ArrayList<>();
    }

    private void onNewItemDetected(int newCount) {

        String message = newCount == 1
                ? "New item available"
                : newCount + " new items available";

        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction("View", v ->
                        recyclerView.smoothScrollToPosition(0))
                .show();
    }
}