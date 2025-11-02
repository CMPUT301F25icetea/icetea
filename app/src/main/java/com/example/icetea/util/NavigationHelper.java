package com.example.icetea.util;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NavigationHelper {

    public static void goBack(Fragment fragment) {
        FragmentManager fm = fragment.getParentFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }

    public static void replaceFragment(FragmentManager fm, int containerId, Fragment newFragment, boolean addToBackStack) {
        FragmentTransaction transaction = fm.beginTransaction().
                setReorderingAllowed(true).
                replace(containerId, newFragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void openActivity(Fragment fragment, Class<?> targetActivity) {
        if (fragment.getActivity() != null) {
            Context context = fragment.getActivity();
            Intent intent = new Intent(context, targetActivity);
            fragment.startActivity(intent);
            fragment.getActivity().finish();
        }
    }
}