package com.example.locationtracker;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Push_data {

    @FormUrlEncoded
    @POST("insert.php")
    Call<Response> insert_new_location(
            @Field("long") Double longitude,
            @Field("lat") Double latitude,
            @Field("id") String app_id
    );


}
