package com.example.icetea.util;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Utility class for fragment and activity navigation.
 */
public class NavigationHelper {

    /**
     * Pops the top fragment from the back stack if any exist.
     *
     * @param fragment The fragment whose parent fragment manager will be used.
     */
    public static void goBack(Fragment fragment) {
        FragmentManager fm = fragment.getParentFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }

    /**
     * Replaces a container with a new fragment.
     *
     * @param fm              The fragment manager used for the transaction.
     * @param containerId     The ID of the container to replace.
     * @param newFragment     The fragment to display.
     * @param addToBackStack  Whether to add the transaction to the back stack.
     */
    public static void replaceFragment(FragmentManager fm, int containerId, Fragment newFragment, boolean addToBackStack) {
        FragmentTransaction transaction = fm.beginTransaction().
                setReorderingAllowed(true).
                replace(containerId, newFragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Opens a new activity and finishes the current one.
     *
     * @param fragment       The fragment requesting the activity start.
     * @param targetActivity The class of the activity to open.
     */
    public static void openActivity(Fragment fragment, Class<?> targetActivity) {
        if (fragment.getActivity() != null) {
            Context context = fragment.getActivity();
            Intent intent = new Intent(context, targetActivity);
            fragment.startActivity(intent);
            fragment.getActivity().finish();
        }
    }
}
