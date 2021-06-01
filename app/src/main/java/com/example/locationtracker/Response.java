package com.example.locationtracker;

import com.google.gson.annotations.SerializedName;

public  class Response{
    @SerializedName("response")
    private boolean response;

    public boolean getResponse() {
        return response;
    }

}
