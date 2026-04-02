package com.khabar.saree.Model;

import com.khabar.saree.Model.BayanModel;

import java.util.List;

public class BayanResponse {

    private String status;
    private String count;
    private List<BayanModel> data;

    public String getStatus() {
        return status;
    }

    public String getCount() {
        return count;
    }

    public List<BayanModel> getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setData(List<BayanModel> data) {
        this.data = data;
    }
}
