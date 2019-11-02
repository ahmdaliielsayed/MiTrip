package com.mi.mitrip.activity;

import android.os.Parcel;

public class Trip {

    String tripID, tripStartPoint, tripEndPoint, tripName, tripDate, tripTime, tripStatus, userID;
    double tripStartPointLongitude, tripStartPointLatitude, tripEndPointLongitude, tripEndPointLatitude;
    long tripCalendar;

    public Trip(){

    }

    public Trip(String tripID, String tripStartPoint, double tripStartPointLongitude, double tripStartPointLatitude, String tripEndPoint, double tripEndPointLongitude, double tripEndPointLatitude, String tripName, String tripDate, String tripTime, String tripStatus, String userID, long tripCalendar) {
        this.tripID = tripID;
        this.tripStartPoint = tripStartPoint;
        this.tripStartPointLongitude = tripStartPointLongitude;
        this.tripStartPointLatitude = tripStartPointLatitude;
        this.tripEndPoint = tripEndPoint;
        this.tripEndPointLongitude = tripEndPointLongitude;
        this.tripEndPointLatitude = tripEndPointLatitude;
        this.tripName = tripName;
        this.tripDate = tripDate;
        this.tripTime = tripTime;
        this.tripStatus = tripStatus;
        this.userID = userID;
        this.tripCalendar = tripCalendar;
    }

    protected Trip(Parcel in) {
        tripID = in.readString();
        tripStartPoint = in.readString();
        tripEndPoint = in.readString();
        tripName = in.readString();
        tripDate = in.readString();
        tripTime = in.readString();
        tripStatus = in.readString();
        userID = in.readString();
        tripStartPointLongitude = in.readDouble();
        tripStartPointLatitude = in.readDouble();
        tripEndPointLongitude = in.readDouble();
        tripEndPointLatitude = in.readDouble();
    }

    public String getTripID() {
        return tripID;
    }

    public String getTripStartPoint() {
        return tripStartPoint;
    }

    public String getTripEndPoint() {
        return tripEndPoint;
    }

    public String getTripName() {
        return tripName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public String getTripTime() {
        return tripTime;
    }

    public String getTripStatus() { return tripStatus; }

    public String getUserID() { return userID; }

    public double getTripStartPointLongitude() { return tripStartPointLongitude; }

    public double getTripStartPointLatitude() { return tripStartPointLatitude; }

    public double getTripEndPointLongitude() { return tripEndPointLongitude; }

    public double getTripEndPointLatitude() { return tripEndPointLatitude; }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public void setTripStartPoint(String tripStartPoint) {
        this.tripStartPoint = tripStartPoint;
    }

    public void setTripEndPoint(String tripEndPoint) {
        this.tripEndPoint = tripEndPoint;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setTripStartPointLongitude(double tripStartPointLongitude) {
        this.tripStartPointLongitude = tripStartPointLongitude;
    }

    public void setTripStartPointLatitude(double tripStartPointLatitude) {
        this.tripStartPointLatitude = tripStartPointLatitude;
    }

    public void setTripEndPointLongitude(double tripEndPointLongitude) {
        this.tripEndPointLongitude = tripEndPointLongitude;
    }

    public void setTripEndPointLatitude(double tripEndPointLatitude) {
        this.tripEndPointLatitude = tripEndPointLatitude;
    }

    public long getTripCalendar() {
        return tripCalendar;
    }

    public void setTripCalendar(long tripCalendar) {
        this.tripCalendar = tripCalendar;
    }
}
