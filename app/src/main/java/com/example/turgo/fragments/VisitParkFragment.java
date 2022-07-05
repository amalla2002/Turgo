package com.example.turgo.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.turgo.MainActivity;
import com.example.turgo.R;
import com.example.turgo.adapter.ParkAdapter;
import com.example.turgo.models.City;
import com.example.turgo.models.Park;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class VisitParkFragment extends Fragment {
    private static final String TAG = "VisitParkFragment";
    private Button btnParkVisitState;
    private EditText etNumOfPeople;
    private Park park;
    private City myCity;

    public VisitParkFragment() {
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
        return inflater.inflate(R.layout.fragment_visit_park, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.loadLibrary("native-lib");
        btnParkVisitState = view.findViewById(R.id.btnParkVisitState);
        etNumOfPeople = view.findViewById(R.id.etNumOfPeople);
        park = new Park();
        ParseQuery<City> query = ParseQuery.getQuery(City.class);
        try {
             myCity = query.find().get(0);
        } catch (ParseException e) {}


        park = ParkAdapter.clickedPark;

        btnParkVisitState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = myCity.getParks().indexOf(park.getObjectId());
                int val;
                try {
                    val = Integer.parseInt(etNumOfPeople.getText().toString());
//                    val *= -1;
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Please indicate num of people", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, btnParkVisitState.getText().toString());
                if (btnParkVisitState.getText().toString().equals("GO")) {
                    btnParkVisitState.setText("Leave");
                }
                else {
                    val *= -1;
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new PlacesFragment()).commit();
                }
                myCity.setTree(updateTree(myCity.getTree(), pos, val));
                myCity.saveInBackground();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, Arrays.stream(myCity.getTree()).boxed().collect(Collectors.toList()).toString());
                    }
                }, 5000);
            }
        });
    }

    private native int[] updateTree(int tree[], int pos, int val);
}