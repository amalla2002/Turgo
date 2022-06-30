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
import com.parse.ParseQuery;

import java.util.List;


public class VisitParkFragment extends Fragment {
    private static final String TAG = "VisitParkFragment";
    private Button btnParkVisitState;
    private EditText etNumOfPeople;
    private Park park;
    private GoogleMap map;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private List<LatLng> directionList;
    private LatLng origin, destination;
    private boolean showMap = false;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;


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
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFragmentPark);
        mapFragment.getMapAsync(this::onMapReady);

        park = ParkAdapter.clickedPark;

        btnParkVisitState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = MainActivity.myCity.getParks().indexOf(park.getObjectId());
                int val = Integer.parseInt(etNumOfPeople.getText().toString());
                if (btnParkVisitState.getText()=="GO") {
                    if (etNumOfPeople.getText().toString() == null) {
                        Toast.makeText(getContext(), "Please Indicate Number of People", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btnParkVisitState.setText("Leave");
                }
                else {
                    val *= -1;
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new ParksFragment()).commit();
                }
                MainActivity.myCity.setTree(updateTree(MainActivity.myCity.getTree(), pos, val));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        setMyLocation();
        if (place1==null) {
            origin = new LatLng(47.628800, -122.342840);
            place1 = new MarkerOptions().position(origin).title("Default ;c");
        }
        destination = new LatLng(park.getLatitude().doubleValue(), park.getLongitude().doubleValue());
        place2 = new MarkerOptions().position(destination).title("Destination");
        String DIRECTION_URL = getUrl(origin, destination, "steps");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DIRECTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        JsonArray routes = jsonObject.getAsJsonArray("routes");
                        JsonElement narrow = routes.get(0);
                        JsonArray legs = ((JsonObject) narrow).getAsJsonArray("legs");
                        JsonObject overview_polyline  = ((JsonObject) narrow).getAsJsonObject("overview_polyline");
                        String line = overview_polyline.getAsJsonPrimitive("points").getAsString();
                        directionList = PolyUtil.decode(line);
                        Log.i(TAG, line);
                        putLine();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(stringRequest);

    }
    private void setMyLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null) {
                        origin = new LatLng(location.getLatitude(), location.getLongitude());
                        place1 = new MarkerOptions().position(origin).title("origin");
                    } else {
                        Log.i(TAG, "NULL LOCATION");
                    }

                }
            });
        } else { // when permision denied
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude; // TODO: CHECK if works
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_api_key);
        return url;
    }

    public void putLine() {
        map.clear();
        map.addMarker(place1);
        map.addMarker(place2);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(place1.getPosition());
        builder.include(place2.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);// googleMap.moveCamera(cu);
        Polyline polyline1 = map.addPolyline( new PolylineOptions().addAll(directionList));
    }



    private native int[] updateTree(int tree[], int pos, int val);
}