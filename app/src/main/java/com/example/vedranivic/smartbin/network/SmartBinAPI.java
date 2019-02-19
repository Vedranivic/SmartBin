package com.example.vedranivic.smartbin.network;

import com.example.vedranivic.smartbin.model.APIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*----------------------------------------
API Interface that determines the possible
calls to the API (smartBin server’s REST API)
----------------------------------------*/
public interface SmartBinAPI {

    @GET("ssid")
    Call<APIResponse> sendHomeNetworkSSID(@Query("ssid") String ssid);

    @GET("pass")
    Call<APIResponse> sendHomeNetworkPassword(@Query("pass") String password);

    @GET("userID")
    Call<APIResponse> getUserID();

}
