package com.example.turgo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.turgo.MainActivity;
import com.example.turgo.R;
import com.example.turgo.adapter.ParkAdapter;
import com.example.turgo.models.City;
import com.example.turgo.models.Park;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ParksFragment extends Fragment {

    private TextView tvPeopleAmount;
    private SeekBar sbPeopleAmount;
    private final int MAXPEOPLE = 1000;
    private static final String TAG = "ParksFragment";
    private int[] segTree;
    private RecyclerView rvParks;
    private ParkAdapter adapter;
    private List<Park> allPark;
    private int currentTarget;
    private List<String> allParkIds;

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
        return inflater.inflate(R.layout.fragment_parks, container, false);
    }
    // where we actually do stuff
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.loadLibrary("native-lib");
        segTree = MainActivity.myCity.getTree();
        allParkIds = MainActivity.myCity.getParks();
        tvPeopleAmount = view.findViewById(R.id.tvPeopleAmount);
        sbPeopleAmount = view.findViewById(R.id.sbPeopleAmount);
        rvParks = view.findViewById(R.id.rvParks);
        allPark = new ArrayList<>();
        adapter = new ParkAdapter(getContext(), allPark);
        rvParks.setAdapter(adapter);
        rvParks.setLayoutManager(new LinearLayoutManager(getContext()));

        tvPeopleAmount.setText("0 People");


        sbPeopleAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double amount = progress;
                amount /= 100;
                amount *= MAXPEOPLE;
                currentTarget = (int) amount;
                tvPeopleAmount.setText(String.valueOf(currentTarget)+" People");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                allPark.clear();
                adapter.notifyDataSetChanged();
                Log.i(TAG, Arrays.stream(segTree).boxed().collect(Collectors.toList()).toString());
                int[] selected = queueTree(segTree, currentTarget);
                List<Integer> picked = Arrays.stream(selected).boxed().collect(Collectors.toList());
                Log.i(TAG, picked.toString());
                List<String> parkId = new ArrayList<>();

                ParseQuery<Park> query = ParseQuery.getQuery(Park.class);
                for (Integer i : picked) {
                    parkId.add(allParkIds.get(i));
//                    query.whereEqualTo("objectID", )
                    try {
                        allPark.add(query.get(String.valueOf(allParkIds.get(i))));
                        adapter.notifyDataSetChanged();
                    } catch (ParseException e) {}
                }
            }
        });
    }
    private native int[] queueTree(int[] segmentedTree, int target);
}