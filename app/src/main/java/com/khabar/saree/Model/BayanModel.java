package com.khabar.saree.Model;

import com.google.gson.annotations.SerializedName;

public class BayanModel {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("displayDate")
    private String displayDate;
    @SerializedName("selected")
    private String selected;
    @SerializedName("url")
    private String url;
    @SerializedName("title_formatted")
    private String title_formatted;
    @SerializedName("body_formatted")
    private String body_formatted;
    @SerializedName("bayan_number")
    private String bayan_number;
    @SerializedName("thumbnail")
    private String thumbnail;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle_formatted() {
        return title_formatted;
    }

    public void setTitle_formatted(String title_formatted) {
        this.title_formatted = title_formatted;
    }

    public String getBody_formatted() {
        return body_formatted;
    }

    public void setBody_formatted(String body_formatted) {
        this.body_formatted = body_formatted;
    }

    public String getBayan_number() {
        return bayan_number;
    }

    public void setBayan_number(String bayan_number) {
        this.bayan_number = bayan_number;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
