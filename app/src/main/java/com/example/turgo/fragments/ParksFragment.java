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
import org.javatuples.Quintet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays List of parks in a recycler view
 * parks displayed are gathered through queueing
 * the segment tree, only the parks that have less (or equal) people
 * to the number displayed on the seekbar are retrieved
 */
public class ParksFragment extends Fragment {
    private static final String TAG = "ParksFragment";
    private TextView tvPeopleAmount;
    private SeekBar sbPeopleAmount;
    private final int MAXPEOPLE = 1000;
    private int[] segTree;
    private RecyclerView rvParks;
    private ParkAdapter adapter;
    private int currentTarget;
    private City myCity;
    private List<Quintet<String, String, Number, Number, Number>> allParks;
    private List<Quintet<String, String, Number, Number, Number>> parksInView;

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
        tvPeopleAmount.setText("-1 People");

        /**
         * sets seekbar logic
         */
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

    /**
     * Takes in info to display in recycler view and assigns views.
     *
     * @param city current city
     */
    private void prepareVariables(City city) {
        myCity = city;
        segTree = myCity.getTree();
        List<String> names = myCity.getParks();
        List<String> hours = myCity.getHours();
        List<Number> entireTree = Arrays.stream(myCity.getTree()).boxed().collect(Collectors.toList());
        List<Number> people = entireTree.subList((entireTree.size()-24)/2, (entireTree.size()-24)/2+names.size());
        List<Number> lats = myCity.getLatitude();
        List<Number> lng = myCity.getLongitude();
        allParks = new ArrayList<>();
        for (int i = 0; i<names.size(); ++i) allParks.add(Quintet.with(names.get(i), hours.get(i), people.get(i), lats.get(i), lng.get(i)));
        tvPeopleAmount = getView().findViewById(R.id.tvPeopleAmount);
        sbPeopleAmount = getView().findViewById(R.id.sbPeopleAmount);
        rvParks = getView().findViewById(R.id.rvParks);
        parksInView = new ArrayList<>();
        adapter = new ParkAdapter(getContext(), parksInView);
    }

    /**
     * Queues the tree for parks under current target
     */
    private void getNewParkList() {
        parksInView.clear();
        adapter.notifyDataSetChanged();
        int[] selected = queueTree(segTree, currentTarget);
        List<Integer> picked = Arrays.stream(selected).boxed().collect(Collectors.toList());
        parksInView.addAll(picked.stream().map(i -> allParks.get(i)).collect(Collectors.toList()));
        adapter.notifyDataSetChanged();
    }

    /**
     * shows the amount of people for target
     *
     * @param progress number of 1-100 represents percentage of bar filled
     */
    private void updateCount(double progress) {
        progress /= 100;
        progress *= MAXPEOPLE;
        currentTarget = (int) progress;
        tvPeopleAmount.setText(String.valueOf(currentTarget)+" People");
    }

    private native int[] queueTree(int[] segmentedTree, int target);
}