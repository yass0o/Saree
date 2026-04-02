package com.khabar.saree.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContentModel implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("main_id")
    private Integer main_id;
    @SerializedName("day2")
    private String day2;
    @SerializedName("day3")
    private String day3;
    @SerializedName("day4")
    private String day4;
    @SerializedName("nashra_time")
    private String nashra_time;
    @SerializedName("general")
    private String general;
    @SerializedName("day")
    private String day;
    @SerializedName("dayname")
    private String dayname;
    @SerializedName("time")
    private String time;
    @SerializedName("title")
    private String title;
    @SerializedName("category")
    private String category;
    @SerializedName("source")
    private String source;
    @SerializedName("url")
    private String url;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("summary")
    private Object summary;
    @SerializedName("show")
    private String show;
    @SerializedName("selected")
    private String selected;
    @SerializedName("count")
    private String count;
    @SerializedName("displayDate")
    private String displayDate;
    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("wordsToReplace")
    private List<String> wordsToReplace;

    @SerializedName("replacementWords")
    private List<String> replacementWords;

    // Getters and setters


    public String getDay2() {
        return day2;
    }

    public void setDay2(String day2) {
        this.day2 = day2;
    }

    public String getDay3() {
        return day3;
    }

    public void setDay3(String day3) {
        this.day3 = day3;
    }

    public String getDay4() {
        return day4;
    }

    public void setDay4(String day4) {
        this.day4 = day4;
    }

    public String getNashra_time() {
        return nashra_time;
    }

    public void setNashra_time(String nashra_time) {
        this.nashra_time = nashra_time;
    }

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDayname() {
        return dayname;
    }

    public void setDayname(String dayname) {
        this.dayname = dayname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getWordsToReplace() {
        return wordsToReplace;
    }

    public void setWordsToReplace(List<String> wordsToReplace) {
        this.wordsToReplace = wordsToReplace;
    }

    public List<String> getReplacementWords() {
        return replacementWords;
    }

    public void setReplacementWords(List<String> replacementWords) {
        this.replacementWords = replacementWords;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public Integer getMain_id() {
        return main_id;
    }

    public void setMain_id(Integer main_id) {
        this.main_id = main_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Object getSummary() {
        return summary;
    }

    public void setSummary(Object summary) {
        this.summary = summary;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
