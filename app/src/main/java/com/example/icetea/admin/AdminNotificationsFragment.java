package com.example.icetea.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.R;

public class AdminNotificationsFragment extends Fragment {

    public AdminNotificationsFragment() {
        // Required empty public constructor
    }

    public static AdminNotificationsFragment newInstance() {
        return new AdminNotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_notifications, container, false);
    }
}