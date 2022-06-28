package com.example.turgo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.turgo.R;

import java.util.Arrays;
import java.util.List;


public class ParksFragment extends Fragment {

    private TextView tvTest;
    private static final String TAG = "ParksFragment";

    public ParksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flight, container, false);
    }
    // where we actually do stuff
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.loadLibrary("native-lib");
        tvTest = view.findViewById(R.id.tvParksTest);

        tvTest.setText(getText()+getInt());
        int[] tree = {0,0,0};
        Log.i(TAG, String.valueOf((queueTree(tree))[0]));


    }
    private native int getInt();
    private native int[] queueTree(int[] tree);
    private native String getText();
}