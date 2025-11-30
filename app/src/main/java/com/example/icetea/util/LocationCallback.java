package com.example.icetea.util;

/**
 * Callback interface for receiving location updates.
 * <p>
 * Implement this interface to handle successful or failed location retrievals.
 */
public interface LocationCallback {

    /**
     * Called when a location is successfully obtained.
     *
     * @param latitude  The latitude of the location in decimal degrees.
     * @param longitude The longitude of the location in decimal degrees.
     */
    void onLocationResult(double latitude, double longitude);

    /**
     * Called when location retrieval fails.
     *
     * @param e The exception describing the failure.
     */
    void onLocationFailed(Exception e);
}
