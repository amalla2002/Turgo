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

import com.example.turgo.R;
import com.example.turgo.adapter.ParkAdapter;
import com.example.turgo.models.City;
import com.example.turgo.models.Park;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ParksFragment extends Fragment {

    private TextView tvPeopleAmount;
    private SeekBar sbPeopleAmount;
    private final int MAXPEOPLE = 1000;
    private static final String TAG = "ParksFragment";
    private City myCity;
    private int[] segTree;
    private RecyclerView rvParks;
    private ParkAdapter adapter;
    private List<Park> allPark;
    private int currentTarget;

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
        tvPeopleAmount = view.findViewById(R.id.tvPeopleAmount);
        sbPeopleAmount = view.findViewById(R.id.sbPeopleAmount);
        rvParks = view.findViewById(R.id.rvParks);
        allPark = new ArrayList<>();
        Park test = new Park();
        test.setParkName("Bestest SEA park");
        test.setHours("24/7");
        test.setNpeople(42);
        allPark.add(test);
        adapter = new ParkAdapter(getContext(), allPark);
        rvParks.setAdapter(adapter);
        rvParks.setLayoutManager(new LinearLayoutManager(getContext()));

        tvPeopleAmount.setText("0 People");


        sbPeopleAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO: Say How many people
                double amount = progress;
                amount /= 100;
                amount *= MAXPEOPLE;
                currentTarget = (int) amount;
                tvPeopleAmount.setText(String.valueOf(currentTarget)+" People");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO: NOTHING! =)
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO: Here we queue tree and update rvParks
                allPark.clear();
                adapter.notifyDataSetChanged();
                // get new post meeting required num of people < currentTarget
                Park test2 = new Park();
                test2.setParkName("ACTUAL BESTEST SEA PARK"); test2.setHours("All day"); test2.setNpeople(72);
                allPark.add(test2);
                adapter.notifyDataSetChanged();
            }
        });

//        ParseQuery<City> query = ParseQuery.getQuery(City.class);
//        query.findInBackground(new FindCallback<City>() {
//            @Override
//            public void done(List<City> city, ParseException e) {
//                myCity = city.get(0); // IF THERE WERE MORE CITYS, LOOK FOR THE ONE THE USER IS IN
//                segTree = myCity.getTree();
//                if (segTree.length==0) {
//                    int[] data = myCity.getData();
//                    segTree = buildTree(data);
//                    myCity.setTree(segTree);
//                    myCity.saveInBackground();
//                }
//            }
//        });
    }
//    private native int[] queueTree(int[] segmentedTree);
//    private native int[] buildTree(int[] data);
//    private native String getText();
}