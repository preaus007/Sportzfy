package com.example.sportzfy.helperClasses;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiServices {
    @GET("preaus007/State_District_api/main/chittagong.json")
    Call<DistrictResponse> getChittagongDistrictData();
}
