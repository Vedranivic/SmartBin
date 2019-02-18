package com.example.vedranivic.smartbin.model;

public class User {

    private Double distance;

    private Double percentage;

    private Double maxDistance;

    private String collectionTime;

    private String collectionDay;

    private String binSize;

    private String reminderOption;

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

    public String getCollectionTime() {
        return collectionTime;
    }

    public String getCollectionDay() {
        return collectionDay;
    }

    public String getBinSize() {
        return binSize;
    }

    public String getReminderOption() {
        return reminderOption;
    }
}
