package com.example.snap_develop.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConnectApiService {
    //Retrofitインターフェース(APIリクエストを管理)
    String API_URL = "https://snap-6cc78.an.r.appspot.com";

    @GET("/search")
    Call<Map<String, Object>> searchPost(@Query("word") String word);
}
