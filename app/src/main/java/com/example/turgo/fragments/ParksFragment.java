package com.example.turgo.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParksFragment extends Fragment {
    private static final String TAG = "ParksFragment";
    private TextView tvPeopleAmount;
    private SeekBar sbPeopleAmount;
    private final int MAXPEOPLE = 1000;
    private int[] segTree;
    private RecyclerView rvParks;
    private ParkAdapter adapter;
    private List<Park> allPark;
    private int currentTarget;
    private List<String> allParkIds;
    City myCity;

    public ParksFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.loadLibrary("native-lib");

        myCity = MainActivity.getCity();
        prepareVariables(myCity);
        rvParks.setAdapter(adapter);
        rvParks.setLayoutManager(new LinearLayoutManager(getContext()));
        tvPeopleAmount.setText("0 People");
        sbPeopleAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateCount(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getNewParkList();
            }
        });
    }

    private void prepareVariables(City city) {
        myCity = city;
        segTree = myCity.getTree();
        allParkIds = myCity.getParks();
        tvPeopleAmount = getView().findViewById(R.id.tvPeopleAmount);
        sbPeopleAmount = getView().findViewById(R.id.sbPeopleAmount);
        rvParks = getView().findViewById(R.id.rvParks);
        allPark = new ArrayList<>();
        adapter = new ParkAdapter(getContext(), allPark);
    }

    private void getNewParkList() {
        allPark.clear();
        adapter.notifyDataSetChanged();
        int[] selected = queueTree(segTree, currentTarget);
        List<Integer> picked = Arrays.stream(selected).boxed().collect(Collectors.toList());
        List<String> parkId = new ArrayList<>();
        ParseQuery<Park> query = ParseQuery.getQuery(Park.class);
        for (Integer i : picked) {
            parkId.add(allParkIds.get(i));
            try {
                allPark.add(query.get(String.valueOf(allParkIds.get(i))));
                adapter.notifyDataSetChanged();
            } catch (ParseException e) {}
        }
    }

    private void updateCount(double progress) {
        progress /= 100;
        progress *= MAXPEOPLE;
        currentTarget = (int) progress;
        tvPeopleAmount.setText(String.valueOf(currentTarget)+" People");
    }

    private native int[] queueTree(int[] segmentedTree, int target);
}