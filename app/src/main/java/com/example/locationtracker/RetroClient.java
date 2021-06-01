package com.example.locationtracker;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
   // private static final  String base_url="http://192.168.1.104/xigtech/location_tracker_api/";
     private static final  String base_url="http://hasib-ahmed.xigtech.fr.fo/location_tracker_api/";

    private static Retrofit retrofit;
    private static  RetroClient mInstance;


    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();

    private RetroClient(){

        retrofit=new Retrofit.Builder()
                .baseUrl(base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static synchronized RetroClient getInstance(){

        if(mInstance==null) {
            mInstance = new RetroClient();
        }
        return mInstance;
    }
    public Push_data api_push_data(){

        return retrofit.create(Push_data.class);
    }

}
