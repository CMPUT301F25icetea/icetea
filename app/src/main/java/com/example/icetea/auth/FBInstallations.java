package com.example.icetea.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * Utility class for handling Firebase Installation IDs (FID).
 * <p>
 * Provides methods to retrieve the current FID asynchronously and
 * store it in a static variable for app-wide access.
 */
public class FBInstallations {

    /** Cached Firebase Installation ID */
    private static String fid;

    /**
     * Asynchronously retrieves the current Firebase Installation ID (FID).
     *
     * @param listener an {@link OnCompleteListener} that receives the FID once the task completes
     */
    public static void getCurrentFID(OnCompleteListener<String> listener) {
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(listener);
    }

    /**
     * Returns the cached Firebase Installation ID.
     *
     * @return the current FID as a String, or {@code null} if not set
     */
    public static String getFid() {
        return fid;
    }

    /**
     * Sets the cached Firebase Installation ID.
     *
     * @param fid the FID to store
     */
    public static void setFid(String fid) {
        FBInstallations.fid = fid;
    }
}
