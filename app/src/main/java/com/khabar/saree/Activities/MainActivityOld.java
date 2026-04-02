package com.khabar.saree.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khabar.saree.Adapter.HorizantalAdapter;
import com.khabar.saree.Adapter.MainAdapter;
import com.khabar.saree.Interface.horizantalClickListener;
import com.khabar.saree.Interface.mainClickListener;
import com.khabar.saree.Model.ContentModel;
import com.khabar.saree.Model.RestManager;
import com.khabar.saree.R;
import com.khabar.saree.Utils.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivityOld extends AppCompatActivity {
    RecyclerView mRecyclerView,horizontalrecyclerView;
    EditText main_search;
    private LinearLayoutManager mLayoutManager,hLayoutManager;
    private MainAdapter mAdapter;
    private HorizantalAdapter hAdapter;
    ArrayList<ContentModel> NewsList;
    ArrayList<ContentModel> SelectedNewsList;
    TextView share_selected,main_title_number,weather_source,weather_main,weather_day2,weather_day3,weather_day4,weather_date;
    SwipeRefreshLayout pullToRefresh;
    private ProgressBar progressBar;
    ImageView main_logo,copy_selected;
    private long backPressedTime;
    private Toast backToast;
    LinearLayout action_buttons;
    private FloatingActionButton fab;
    MaterialCardView main_weather_card;

    private static final long DELAY_MILLIS = 50; // 0.05 second delay

    private Runnable showTextViewRunnable;
    private static final int SHARE_REQUEST_CODE = 1;
    String weather_string = "احوال الطقس";
    private Handler handler = new Handler();
    ActivityResultLauncher<Intent> shareLauncher;
    int news_number;
    ScrollView main_weather_card_scroll;
    private Set<String> uniqueItemsSet = new HashSet<>();
    private List<String> uniqueItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.main_activity);
        main_weather_card_scroll = findViewById(R.id.main_weather_card_scroll);
        progressBar = findViewById(R.id.progressBar);
        main_search=findViewById(R.id.main_search);
        action_buttons=findViewById(R.id.action_buttons);
        share_selected=findViewById(R.id.share_selected);
        copy_selected=findViewById(R.id.copy_selected);
        mRecyclerView = findViewById(R.id.recyclerView);
        horizontalrecyclerView = findViewById(R.id.horizontalrecyclerView);
        main_title_number = findViewById(R.id.main_title_number);
        pullToRefresh=findViewById(R.id.pullToRefresh);
        main_logo=findViewById(R.id.main_logo);
        weather_source=findViewById(R.id.weather_source);
        weather_date=findViewById(R.id.weather_date);
        weather_main=findViewById(R.id.weather_main);
        weather_day2=findViewById(R.id.weather_day2);
        weather_day3=findViewById(R.id.weather_day3);
        weather_day4=findViewById(R.id.weather_day4);
        main_weather_card=findViewById(R.id.main_weather_card);
        fab = findViewById(R.id.fab);
        fab.hide();
        mRecyclerView.setHasFixedSize(true);
        horizontalrecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        hLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        if(action_buttons.getVisibility() == View.GONE){
            utils.saveSelected(getApplicationContext(),0,"sharestate");
           // copy_selected.setVisibility(View.GONE);
        }
        news_number = utils.getNumber(this,600);
        showTextViewRunnable = new Runnable() {
            @Override
            public void run() {
                main_search.setVisibility(View.VISIBLE);
               // fab.show();
            }
        };

        getWeather();

        main_logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showNumberInputDialog(savedInstanceState);
                return true;
            }
        });

        SelectedNewsList  = new ArrayList<>();
        NewsList  = new ArrayList<>();
        news_number = utils.getNumber(this,600);
        //getData(""+news_number,"","","","",savedInstanceState);
        if (savedInstanceState == null) {
            getData("" + news_number, "", "", "", "",savedInstanceState);
        }
        fab.setOnClickListener(view -> mRecyclerView.smoothScrollToPosition(0));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mLayoutManager.findFirstVisibleItemPosition() > 0) {
                    fab.show();
                } else {
                    fab.hide();
                }

//                if (dy != 0) {
//                    // Scrolling detected
//                    main_search.setVisibility(View.INVISIBLE);
//                   // fab.hide();
 //                   handler.removeCallbacks(showTextViewRunnable); // Remove any pending show request
//                } else {
//                    // No scrolling
//                    main_search.setVisibility(View.VISIBLE);
//                   // fab.show();
//                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // When scrolling stops, schedule the textView to be shown after a short delay
                    handler.post(showTextViewRunnable);
                    //handler.postDelayed(showTextViewRunnable, 1000); // Delay in milliseconds
                }
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //NewsList  = new ArrayList<>();
                if(NewsList!=null && NewsList.size()>0){
                    NewsList.clear();
                }
                if(uniqueItemsList!=null && uniqueItemsList.size()>0){
                    uniqueItemsList.clear();
                    uniqueItemsSet.clear();
                }
                if(mAdapter!=null) {
                    mAdapter.notifyDataSetChanged();
                }
                if(hAdapter!=null) {
                    hAdapter.notifyDataSetChanged();
                }
                fab.hide();
                news_number = utils.getNumber(getApplicationContext(),600);
                getData(""+news_number,"","","","",savedInstanceState);
                pullToRefresh.setRefreshing(false);
                if (main_search != null && main_search.getText() != null && main_search.getText().length() > 0) {
                    main_search.setText("");
                }
                getWeather();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back press event here
                // For example, you could call finish() to close the activity
                hAdapter.setSelectedPosition(RecyclerView.NO_POSITION);

                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel();
                    finishAffinity();
                    return;
                } else {
                    if (main_search != null && main_search.getText() !=null && !main_search.getText().toString().equals("")) {
                        main_search.setText("");
                    }else if(main_weather_card.getVisibility() == View.VISIBLE){
                        main_weather_card.setVisibility(View.GONE);
                        hAdapter.setWeather(false);
                    } {
                        if (SelectedNewsList != null && SelectedNewsList.size() > 0) {
                            action_buttons.setVisibility(View.GONE);
                            //copy_selected.setVisibility(View.GONE);
                            for (int i = 0; i < SelectedNewsList.size(); i++) {
                                utils.saveSelected(getApplicationContext(), 0, "" + SelectedNewsList.get(i).getMain_id());
                            }
                            utils.saveSelected(getApplicationContext(), 0, "sharestate");
                            if(mAdapter!=null) {
                                mAdapter.notifyDataSetChanged();
                            }
                            SelectedNewsList.clear();
                        }
                    }

                    backToast = Toast.makeText(getBaseContext(), getString(R.string.clickbackagaintoexit), Toast.LENGTH_SHORT);
                    backToast.show();
                }

                backPressedTime = System.currentTimeMillis();
            }
        });

        shareLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (NewsList != null && NewsList.size() > 0) {
                            NewsList.clear();
                        }
                        if (uniqueItemsList != null && uniqueItemsList.size() > 0) {
                            uniqueItemsList.clear();
                            uniqueItemsSet.clear();
                        }
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        if (hAdapter != null) {
                            hAdapter.notifyDataSetChanged();
                        }
                        fab.hide();
                        news_number = utils.getNumber(this,600);
                        getData("" + news_number, "", "", "", "",savedInstanceState);
                        if (main_search != null && main_search.getText() != null && main_search.getText().length() > 0) {
                            main_search.setText("");
                        }
                    }
                }
        );
    }

    private void getWeather() {
        RestManager mManager = new RestManager();
        Call<ContentModel> listCall = mManager.getNewsService(this, "https://thomaihtadayt.com/").get_weather();
        listCall.enqueue(new Callback<ContentModel>() {
            @Override
            public void onResponse(Call<ContentModel> call, Response<ContentModel> response) {
                Log.e("newsurl", "onResponse: "+call.request().url() );
                if(response.isSuccessful() && response.body()!=null){
                    weather_source.setText("الحالة العامة: "+response.body().getGeneral());
                    //weather_source.setText(response.body().getDay());
                    weather_day2.setText(response.body().getDayname()+": "+response.body().getDay());
                    weather_day3.setText(response.body().getDay2());
                    weather_day4.setText(response.body().getDay3());
                   // String weather_title = response.body().getDayname()+"\n\n"+ response.body().getNashra_time();
                    //weather_main.setText(response.body().getDayname());
                    weather_main.setText("الحالة العامة");
                    if(!response.body().getTime().isEmpty()) {
                        String[] parts = response.body().getTime().split(" ");
                        weather_date.setText(response.body().getNashra_time() + " " + parts[0]);
                    }
                    weather_string = getWeatherMessage(response);

                    main_weather_card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            String finalMessage = getWeatherMessage(response);
                            shareMessage(finalMessage,view);
                            return true;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ContentModel> call, Throwable t) {

            }
        });
    }

    private String getWeatherMessage(Response<ContentModel> response) {
        StringBuilder sb = new StringBuilder();
        String day2 = response.body().getDay2();
        String day3 = response.body().getDay3();
        int colonIndex2 = day2.indexOf(":");
        int colonIndex3 = day3.indexOf(":");
        String day2formatted = day2,day3formatted = day3;
        if (colonIndex2 != -1) {
            String dayName = day2.substring(0, colonIndex2 + 1).trim(); // "الأربعاء:"
            String forecast = day2.substring(colonIndex2 + 1).trim();   // rest of text
            day2formatted = "*" + dayName + "* " + forecast;
        }
        if (colonIndex3 != -1) {
            String dayName = day3.substring(0, colonIndex3 + 1).trim(); // "الأربعاء:"
            String forecast = day3.substring(colonIndex3 + 1).trim();   // rest of text
            day3formatted = "*" + dayName + "* " + forecast;
        }

        sb.append("🌤 *الاحوال الجوية:*\n\n")
                .append("- 🌍 *الحالة العامة:* ").append(response.body().getGeneral()).append("\n\n")
                .append("- 📅 *").append(response.body().getDayname()).append(":* ").append(response.body().getDay()).append("\n\n")
                .append("- 📆 ").append(day2formatted).append("\n\n")
                .append("- 📆 ").append(day3formatted);


        return sb.toString();
    }

    private void showNumberInputDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_number_input);

        EditText editTextNumber = dialog.findViewById(R.id.editTextNumber);
        Button buttonSet = dialog.findViewById(R.id.buttonSet);

        buttonSet.setOnClickListener(view -> {
            String input = editTextNumber.getText().toString();
            if (!input.isEmpty()) {
                int number = Integer.parseInt(input);
                utils.saveNumber(this,number);

                refreshdata(input,savedInstanceState);
                //Toast.makeText(MainActivity.this, "Number set: " + number, Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Dismiss the dialog
            } else {
                Toast.makeText(MainActivityOld.this, "Please enter a number", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void refreshdata(String number, Bundle savedInstanceState) {
        if(NewsList!=null && NewsList.size()>0){
            NewsList.clear();
        }
        if(uniqueItemsList!=null && uniqueItemsList.size()>0){
            uniqueItemsList.clear();
            uniqueItemsSet.clear();
        }
        if(mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
        if(hAdapter!=null) {
            hAdapter.notifyDataSetChanged();
        }
        fab.hide();
        getData(number,"","","","",savedInstanceState);
        pullToRefresh.setRefreshing(false);
        if (main_search != null && main_search.getText() != null && main_search.getText().length() > 0) {
            main_search.setText("");
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search_text", main_search.getText().toString());
        if (mLayoutManager != null) {
            int scrollPosition = mLayoutManager.findFirstVisibleItemPosition();
            outState.putInt("recycler_position", scrollPosition);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore search text
            main_search.setText(savedInstanceState.getString("search_text", ""));

            // Restore RecyclerView scroll position
            int scrollPosition = savedInstanceState.getInt("recycler_position", 0);
            if (mRecyclerView != null && mLayoutManager != null) {
                mRecyclerView.scrollToPosition(scrollPosition);
            }
        }
    }

    private void filter(String text) {
        if (mAdapter == null) return;

        if(mRecyclerView!=null) {
            mRecyclerView.scrollToPosition(0);
        }
        ArrayList<ContentModel> filteredList = new ArrayList<>(NewsList);
        filteredList.removeIf(item1 -> !item1.getTitle().contains(text) && !item1.getCategory().contains(text) && !item1.getDisplayDate().contains(text) && !item1.getSource().contains(text));
        main_title_number.setText(filteredList.size()+"");
        mAdapter.filterList(filteredList);
    }

    private void getData(String limit,String title,String category,String source,String created_date, Bundle savedInstanceState) {
        progressBar.setVisibility(View.VISIBLE);
        RestManager mManager = new RestManager();
        Call<List<ContentModel>> listCall = mManager.getNewsService(this, "https://thomaihtadayt.com/").get_news(limit,title,category,source,created_date);
        listCall.enqueue(new Callback<List<ContentModel>>() {
            @Override
            public void onResponse(Call<List<ContentModel>> call, Response<List<ContentModel>> response) {
                Log.e("newsurl", "onResponse: "+call.request().url() );
                if(response.isSuccessful() && response.body()!=null){
                    for (ContentModel item : response.body()) {
                        String title = item.getSource();
                        // Add to set and list only if not already present
                        if (uniqueItemsSet.add(title)) {
                            uniqueItemsList.add(title);
                        }
                    }
                    uniqueItemsList.add(0, "أحوال الطقس");
                    hAdapter = new HorizantalAdapter(uniqueItemsList, new horizantalClickListener() {
                        @Override
                        public void onClickItem(String item, View v) {
                            if (item.equals("أحوال الطقس")){
                                main_weather_card_scroll.smoothScrollTo(0, 0);
                                if (main_search != null && main_search.getText() !=null && !main_search.getText().toString().equals("")) {
                                    main_search.setText("");
                                }
                                if(main_weather_card.getVisibility() == View.GONE){
                                    main_weather_card.setVisibility(View.VISIBLE);
                                    hAdapter.setWeather(true);
                                }else {
                                    main_weather_card.setVisibility(View.GONE);
                                    hAdapter.setWeather(false);
                                }
                            }else {
                                if(main_weather_card.getVisibility() == View.VISIBLE){
                                    main_weather_card.setVisibility(View.GONE);
                                    hAdapter.setWeather(false);
                                }
                                main_search.setText(item);
                            }
                        }

                        @Override
                        public void onLongClickItem(String item, View v) {
                            shareMessage(weather_string,v);
                        }
                    },getApplicationContext());
                    horizontalrecyclerView.setLayoutManager(hLayoutManager);
                    horizontalrecyclerView.setAdapter(hAdapter);
                    hAdapter.notifyDataSetChanged();

                    //NewsList.addAll(response.body());
                    String[] wordsToReplace = response.body().get(0).getWordsToReplace().toArray(new String[0]);
                    String[] replacementWords = response.body().get(0).getReplacementWords().toArray(new String[0]);
                    NewsList.addAll(utils.cleanarray(response.body(),wordsToReplace,replacementWords));
                    main_title_number.setText(NewsList.size()+"");
                    mAdapter = new MainAdapter(getApplicationContext(), NewsList, false, new mainClickListener() {
                        @Override
                        public void onClickItem(ContentModel item, View v) {
//                            TextView category_view = v.findViewById(R.id.category);
                            TextView title_view = v.findViewById(R.id.title);
                            if(title_view.getHint()==null || title_view.getHint()=="0") {
                                if(item.getSource().trim().equals("الاعلام الحربي") && utils.getLine(2,item.getTitle()).trim().startsWith("بيان صادر عن المقاومة الإسلامية"))
                                {
                                    Log.e("newsclick", "onResponse: " + utils.getLine(2, item.getTitle()) + "/" + item.getSource());
                                }else{
                                    Log.e("newsclick", "onResponseelse: " + utils.getLine(2, item.getTitle()).trim());
                                    Log.e("newsclick", "onResponseelse: " + ":بيان صادر عن المقاومة الإسلامية");
                                }
                                action_buttons.setVisibility(View.VISIBLE);
                                //copy_selected.setVisibility(View.VISIBLE);
                                MaterialCardView main_item_card_view = v.findViewById(R.id.main_item_card);
                                main_item_card_view.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_background));
                                title_view.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                SelectedNewsList.add(item);
                                share_selected.setText(SelectedNewsList.size()+"");
                                //utils.saveData(getApplicationContext(),SelectedNewsList,"SelectedNewsList");
                                utils.saveSelected(getApplicationContext(),1,""+item.getMain_id());
                                utils.saveSelected(getApplicationContext(),1,"sharestate");
                                item.setSelected("1");
                                title_view.setHint("1");
                            }else{
                                if (SelectedNewsList.size()==1){
                                    action_buttons.setVisibility(View.GONE);
                                   // copy_selected.setVisibility(View.GONE);
                                    utils.saveSelected(getApplicationContext(),0,"sharestate");
                                }
                                MaterialCardView main_item_card_view = v.findViewById(R.id.main_item_card);
                                main_item_card_view.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                title_view.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_secondary));
                                SelectedNewsList.remove(item);
                                share_selected.setText(SelectedNewsList.size()+"");
                                //utils.saveData(getApplicationContext(),SelectedNewsList,"SelectedNewsList");
                                utils.saveSelected(getApplicationContext(),0,""+item.getMain_id());
                                utils.saveSelected(getApplicationContext(),1,"sharestate");
                                item.setSelected("0");
                                title_view.setHint("0");
                            }
                        }

                        @Override
                        public void onClickSource(ContentModel item, View v) {
                            main_search.setText(item.getSource());
                            hAdapter.setSelectedPosition(utils.getPositionByWord(NewsList,item.getSource()));
                        }

                        @Override
                        public void onLongClickItem(ContentModel item, View v) {
                            if(item.getSource().trim().equals("الاعلام الحربي") && utils.isInteger(utils.getLine(0,item.getTitle()).trim()) && utils.searchForWord(item.getTitle(),"بيان صادر عن المقاومة").size()>0)
                            {
                                shareMessage(bayanhizbmodify(item.getTitle()).trim(), v);
                            }else{
                                String main_message = "*" + ((!item.getSource().isEmpty()) ? item.getSource() : item.getCategory()) + ":*" + System.lineSeparator() + System.lineSeparator()
                                        + item.getTitle();
                                shareMessage(main_message.trim(), v);
                            }
                        }
                    });


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Hide the ProgressBar
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            progressBar.setVisibility(View.GONE);
                            mRecyclerView.setAdapter(mAdapter);
                            if(mAdapter!=null) {
                                mAdapter.notifyDataSetChanged();
                            }
                            main_search.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    //  search(charSequence.toString());
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    filter(editable.toString());
                                }
                            });
                            if (savedInstanceState != null) {
                                String restoredText = savedInstanceState.getString("search_text", "");
                                main_search.setText(restoredText); // triggers filtering automatically
                            }
                        }
                    }, DELAY_MILLIS);


                }
            }

            @Override
            public void onFailure(Call<List<ContentModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

        share_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SelectedNewsList!=null && SelectedNewsList.size()>0) {
                    SelectedNewsList.sort(new Comparator<ContentModel>() {
                        @Override
                        public int compare(ContentModel o1, ContentModel o2) {
                            return o1.getSource().compareTo(o2.getSource());
                        }
                    });

                    if (SelectedNewsList.size() == 1) {
                        if(SelectedNewsList.get(0).getSource().trim().equals("الاعلام الحربي") && utils.isInteger(utils.getLine(0,SelectedNewsList.get(0).getTitle()).trim()) && utils.searchForWord(SelectedNewsList.get(0).getTitle(),"بيان صادر عن المقاومة").size()>0)
                        {
                            shareMessage(bayanhizbmodify(SelectedNewsList.get(0).getTitle()).trim(), v);
                        }else{
                            String main_message = "*" + ((!SelectedNewsList.get(0).getSource().isEmpty()) ? SelectedNewsList.get(0).getSource() : SelectedNewsList.get(0).getCategory()) + ":*" + System.lineSeparator() + System.lineSeparator()
                                    + SelectedNewsList.get(0).getTitle();
                            shareMessage(main_message.trim(), v);
                        }

                    } else {
                        StringBuilder message = new StringBuilder();
                        String last_category = (!SelectedNewsList.get(0).getSource().isEmpty()) ? SelectedNewsList.get(0).getSource() : SelectedNewsList.get(0).getCategory();
                        for (int i = 0; i < SelectedNewsList.size(); i++) {
                            //Log.e("SelectedShareList", "onSelectedShare: "+SelectedNewsList.get(i).getTitle() +"//"+SelectedNewsList.size());
                            if (!last_category.equals((!SelectedNewsList.get(i).getSource().isEmpty()) ? SelectedNewsList.get(i).getSource() : SelectedNewsList.get(i).getCategory())) {
                                message.append(System.lineSeparator()).append("*").append((!SelectedNewsList.get(i).getSource().isEmpty()) ? SelectedNewsList.get(i).getSource() : SelectedNewsList.get(i).getCategory()).append(":*").append(System.lineSeparator()).append(System.lineSeparator()).append("- ").append(SelectedNewsList.get(i).getTitle()).append(System.lineSeparator());
                            } else {
                                message.append(System.lineSeparator()).append("- ").append(SelectedNewsList.get(i).getTitle()).append(System.lineSeparator());
                            }
                            last_category = (!SelectedNewsList.get(i).getSource().isEmpty()) ? SelectedNewsList.get(i).getSource() : SelectedNewsList.get(i).getCategory();
                        }
                        String main_message = "*" + ((!SelectedNewsList.get(0).getSource().isEmpty()) ? SelectedNewsList.get(0).getSource() : SelectedNewsList.get(0).getCategory()) + ":*" + System.lineSeparator()
                                + message;
                        shareMessage(main_message.trim(), v);
                    }
                }
            }
        });

        copy_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SelectedNewsList!=null && SelectedNewsList.size()>0) {
                    SelectedNewsList.sort(new Comparator<ContentModel>() {
                        @Override
                        public int compare(ContentModel o1, ContentModel o2) {
                            return o1.getSource().compareTo(o2.getSource());
                        }
                    });

                    if (SelectedNewsList.size() == 1) {
                        if(SelectedNewsList.get(0).getSource().trim().equals("الاعلام الحربي") && utils.isInteger(utils.getLine(0,SelectedNewsList.get(0).getTitle()).trim()) && utils.searchForWord(SelectedNewsList.get(0).getTitle(),"بيان صادر عن المقاومة").size()>0)
                        {
                            copyToClipboard(getApplicationContext(),bayanhizbmodify(SelectedNewsList.get(0).getTitle()).trim());
                        }else{
                            String main_message = "*" + ((!SelectedNewsList.get(0).getSource().isEmpty()) ? SelectedNewsList.get(0).getSource() : SelectedNewsList.get(0).getCategory()) + ":*" + System.lineSeparator() + System.lineSeparator()
                                    + SelectedNewsList.get(0).getTitle();
                            copyToClipboard(getApplicationContext(),main_message.trim());
                        }

                    } else {
                        StringBuilder message = new StringBuilder();
                        String last_category = (!SelectedNewsList.get(0).getSource().isEmpty()) ? SelectedNewsList.get(0).getSource() : SelectedNewsList.get(0).getCategory();
                        for (int i = 0; i < SelectedNewsList.size(); i++) {
                            //Log.e("SelectedShareList", "onSelectedShare: "+SelectedNewsList.get(i).getTitle() +"//"+SelectedNewsList.size());
                            if (!last_category.equals((!SelectedNewsList.get(i).getSource().isEmpty()) ? SelectedNewsList.get(i).getSource() : SelectedNewsList.get(i).getCategory())) {
                                message.append(System.lineSeparator()).append("*").append((!SelectedNewsList.get(i).getSource().isEmpty()) ? SelectedNewsList.get(i).getSource() : SelectedNewsList.get(i).getCategory()).append(":*").append(System.lineSeparator()).append(System.lineSeparator()).append("- ").append(SelectedNewsList.get(i).getTitle()).append(System.lineSeparator());
                            } else {
                                message.append(System.lineSeparator()).append("- ").append(SelectedNewsList.get(i).getTitle()).append(System.lineSeparator());
                            }
                            last_category = (!SelectedNewsList.get(i).getSource().isEmpty()) ? SelectedNewsList.get(i).getSource() : SelectedNewsList.get(i).getCategory();
                        }
                        String main_message = "*" + ((!SelectedNewsList.get(0).getSource().isEmpty()) ? SelectedNewsList.get(0).getSource() : SelectedNewsList.get(0).getCategory()) + ":*" + System.lineSeparator()
                                + message;
                        copyToClipboard(getApplicationContext(),main_message.trim());
                    }
                }
            }
        });
    }

    private String bayanhizbmodify(String bayan_txt) {//social_txt.getText() == bayan_txt
            bayan_txt = bayan_txt.replace("*", "");
            String _stline ="";
            String[] lines = bayan_txt.split("\\r?\\n");
            if(utils.searchForIraqWord(bayan_txt,"بيان صادر عن المقاومة").size()>0) {
                _stline = utils.searchForIraqWord(bayan_txt, "بيان صادر عن المقاومة").get(0);
            }
           // Log.d("bayanhizbmodify", _stline);
            String social_number_txt = utils.extractNumbers(bayan_txt).get(0).toString();
            String social_string = "";
            if(!_stline.isEmpty()){
                social_string += "*بيان صادر عن المقاومة الإسلامية في العراق رقم ";
            }else {
                social_string += "*بيان صادر عن المقاومة الإسلامية رقم ";
            }
            social_string += social_number_txt.trim()+ ":*"+ System.getProperty("line.separator")+ System.getProperty("line.separator");
            social_string += utils.findLargestString(lines).trim();
            //Log.e("bayanhizbmodify", "onClick: "+social_string );
            String[] wordsToReplace = {"والشريفة، "};
            String[] replacementWords = {"والشريفة، *"};
            utils.Result modifiedText = utils.replaceWords(social_string, wordsToReplace, replacementWords);
            if(modifiedText.wasReplaced){
                social_string = modifiedText.modifiedText.replaceAll("[\\s\\u00A0\\u200B\\u200F]+$", "").trim()+"*";
                Log.e("sharedmessage", social_string);
            }else{
                social_string = modifiedText.mainText.trim();
            }
        return social_string;
    }

    public void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        action_buttons.setVisibility(View.GONE);
        //copy_selected.setVisibility(View.GONE);
        for(int i=0;i<SelectedNewsList.size();i++) {
            utils.saveSelected(getApplicationContext(), 0, "" + SelectedNewsList.get(i).getMain_id());
        }
        utils.saveSelected(getApplicationContext(),0,"sharestate");
        if(mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
        SelectedNewsList.clear();
        Toast.makeText(context, "تم النسخ", Toast.LENGTH_SHORT).show();
    }

    public void shareMessage(String shareMessage, View v){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/html");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, v.getContext().getString(R.string.app_name));
            //shareMessage = shareMessage + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            //v.getContext().startActivity(Intent.createChooser(shareIntent, "choose one"));
            shareLauncher.launch(Intent.createChooser(shareIntent, "Share via"));
            action_buttons.setVisibility(View.GONE);
            //copy_selected.setVisibility(View.GONE);
            for(int i=0;i<SelectedNewsList.size();i++) {
                utils.saveSelected(getApplicationContext(), 0, "" + SelectedNewsList.get(i).getMain_id());
            }
            utils.saveSelected(getApplicationContext(),0,"sharestate");
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
            SelectedNewsList.clear();
        } catch(Exception e) {
            Toast.makeText(v.getContext(), "خلل في المشاركة، الرجاء الإعادة", Toast.LENGTH_SHORT).show();
            //e.toString();
        }
    }


}
