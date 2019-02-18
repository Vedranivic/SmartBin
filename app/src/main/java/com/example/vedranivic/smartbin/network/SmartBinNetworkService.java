package com.example.vedranivic.smartbin.network;

import com.example.vedranivic.smartbin.base.Constants;
import com.example.vedranivic.smartbin.model.APIResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SmartBinNetworkService {

    private static Retrofit retrofit;

    public static void initialize(){
        if(retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.SMARTBINAPI_BASE_URL)
                    .client(new OkHttpClient.Builder()
                            .build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static Call<APIResponse> sendSSID(String ssid) {
        return retrofit.create(SmartBinAPI.class).sendHomeNetworkSSID(ssid);
    }

    public static Call<APIResponse> sendPassword(String pass) {
        return retrofit.create(SmartBinAPI.class).sendHomeNetworkPassword(pass);
    }

    public static Call<APIResponse> getUserID() {
        return retrofit.create(SmartBinAPI.class).getUserID();
    }

    public static void release(){
        retrofit = null;
    }
}
