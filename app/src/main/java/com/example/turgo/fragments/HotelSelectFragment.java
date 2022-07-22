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
import com.example.turgo.AmadeusApplication;
import com.example.turgo.R;
import com.example.turgo.adapter.HotelAdapter;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.List;
/**
 * Gets hotels from AmadeusApplication
 * Displays them using a recycler view and HotelAdapter
 */
public class HotelSelectFragment extends Fragment {
    private static final String TAG = "HotelSelectFragment";
    private RecyclerView rvHotels;
    private HotelAdapter adapter;
    private List<Pair<String, String>> allHotels;

    public HotelSelectFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hotel_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvHotels = view.findViewById(R.id.rvHotels);
        allHotels = new ArrayList<>();
        adapter = new HotelAdapter(getContext(), allHotels);
        rvHotels.setAdapter(adapter);
        rvHotels.setLayoutManager(new LinearLayoutManager(getContext()));
        allHotels.addAll(AmadeusApplication.fetchHotels("JFK"));
        adapter.notifyDataSetChanged();
    }
}