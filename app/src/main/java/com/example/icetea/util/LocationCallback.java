package com.example.icetea.util;

public interface LocationCallback {
    void onLocationResult(double latitude, double longitude);
    void onLocationFailed(Exception e);
}