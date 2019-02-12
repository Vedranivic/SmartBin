package com.example.vedranivic.smartbin.Model;

public class User {

    private Double distance;

    private Double percentage;

    private Double maxDistance;

    private String id;

    private String time;

    public Double getDistance ()
    {
        return distance;
    }

    public Double getPercentage ()
    {
        return percentage;
    }

    public Double getMaxDistance ()
    {
        return maxDistance;
    }

    public void setMaxDistance (Double maxDistance)
    {
        this.maxDistance = maxDistance;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

}
