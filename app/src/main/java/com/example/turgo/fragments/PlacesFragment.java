package com.example.turgo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.turgo.MainActivity;
import com.example.turgo.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;


public class PlacesFragment extends Fragment {
    public static final String TAG = "PlacesFragment";

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Button btnSearch;




    public PlacesFragment() {
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
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    // where we actually do stuff
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "it gets here");

        btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int AUTOCOMPLETE_REQUEST_CODE = 1;

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields) // try out OVERLAY option
                        .build(getActivity());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });




//        insertNestedFragment();
//        if (!Places.isInitialized()) Log.i(TAG, "Cry");
//        // Initiallize the AutocompleteSupportFragment
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        // Specify the types of place data to return.
//        List<Place.Field> fieldList = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//        Log.i(TAG, fieldList.toString());
//        autocompleteFragment.setPlaceFields(fieldList);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onError(@NonNull Status status) {
//                Log.i(TAG, status.toString());
//            }
//
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//        });
    }

//    private void insertNestedFragment() {
//        Fragment childFragment = new AutoCompleteFragment();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.child_fragment_container, childFragment).commit();
//    }
}