package com.khabar.saree.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.khabar.saree.Adapter.BayanAdapter;
import com.khabar.saree.Model.BayanModel;
import com.khabar.saree.Model.BayanModel;
import com.khabar.saree.Model.BayanResponse;
import com.khabar.saree.Model.RestManager;
import com.khabar.saree.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BayanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    AppCompatRadioButton rb_full,rb_formatted;
    String copyMode = "full";
    private BayanAdapter adapter;
    private List<BayanModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_bayan);

        // =========================
        // INIT VIEWS
        // =========================
        rb_full = findViewById(R.id.rb_full);
        rb_formatted = findViewById(R.id.rb_formatted);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.pullToRefresh);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String mode = prefs.getString("copy_mode", "full"); // default = full

        switch (mode) {
            case "formatted":
                rb_formatted.setChecked(true);
                copyMode = mode;
                break;

            default:
                rb_full.setChecked(true);
                copyMode = mode;
                break;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        rb_full.setOnClickListener(v -> {
            copyMode = "full";
            editor.putString("copy_mode", "full");
            editor.apply();

            if (adapter != null) {
                adapter.setCopyMode(copyMode);
                adapter.notifyDataSetChanged();
            }
        });

        rb_formatted.setOnClickListener(v -> {
            copyMode = "formatted";
            editor.putString("copy_mode", "formatted");
            editor.apply();

            if (adapter != null) {
                adapter.setCopyMode(copyMode);
                adapter.notifyDataSetChanged();
            }
        });
        // =========================
        // INITIAL LOAD
        // =========================
        loadData();

        // =========================
        // PULL TO REFRESH
        // =========================
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData();
        });
    }
    // =========================
    // LOAD DATA METHOD
    // =========================
    private void loadData() {

        progressBar.setVisibility(View.VISIBLE);

        RestManager mManager = new RestManager();

        Call<BayanResponse> call =
                mManager.getNewsService(this, "https://thomaihtadayt.com/")
                        .get_news_bayan();

        call.enqueue(new Callback<BayanResponse>() {

            @Override
            public void onResponse(Call<BayanResponse> call, Response<BayanResponse> response) {

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {

                    list = response.body().getData();

                    if (adapter == null) {
                        adapter = new BayanAdapter(BayanActivity.this,copyMode, list);
                        recyclerView.setAdapter(adapter);
                        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
                        if (animator instanceof SimpleItemAnimator) {
                            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                        }
                    } else {
                        adapter.updateData(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<BayanResponse> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                Log.e("newsurl", "error: " + t.getMessage());
            }
        });
    }
}

