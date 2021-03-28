package com.example.spacexdemo.api;

import com.example.spacexdemo.pojos.CrewListModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("crew")
    Call<List<CrewListModel>> getAllCrew();
}
