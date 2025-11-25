package com.example.icetea.util;

/**
 * Generic callback interface for asynchronous operations.
 *
 * @param <T> The type of the result expected on success.
 */
public interface Callback<T> {

    /**
     * Called when the asynchronous operation completes successfully.
     *
     * @param result The result of the operation.
     */
    void onSuccess(T result);

    /**
     * Called when the asynchronous operation fails.
     *
     * @param e The exception representing the failure.
     */
    void onFailure(Exception e);
}
