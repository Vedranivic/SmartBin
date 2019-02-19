package com.example.vedranivic.smartbin.model;

public class User {

    private Double distance;

    private Double percentage;

    private Double maxDistance;

    private String collectionTime;

    private String collectionDay;

    private String binSize;

    private String reminderOption;

    private int collectionNumber;

    private int lastCollectionNumber;

    private Double wasteAmount;

    private Double lastWasteAmount;

    private Boolean scheduledForCollection;

    private int currentMonth;


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

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public int getLastCollectionNumber() {
        return lastCollectionNumber;
    }

    public Double getWasteAmount() {
        return wasteAmount;
    }

    public Double getLastWasteAmount() {
        return lastWasteAmount;
    }

    public Boolean getScheduledForCollection() {
        return scheduledForCollection;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }
}
