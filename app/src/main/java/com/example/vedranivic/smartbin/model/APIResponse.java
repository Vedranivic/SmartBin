package com.example.vedranivic.smartbin.model;

import com.google.gson.annotations.SerializedName;

public class APIResponse
{
    private String connected;

    private String userID;

    @SerializedName("return_value")
    private int returnValue;

    private String name;

    private String id;

    private String hardware;

    public String getConnected ()
    {
        return connected;
    }

    public String getUserID() {
        return userID;
    }

    public String getName ()
    {
        return name;
    }


    public String getId ()
    {
        return id;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public String getHardware ()
    {
        return hardware;
    }

}