package com.example.turgo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.turgo.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;

import java.util.Arrays;
import java.util.List;


public class PlacesFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {
    public static final String TAG = "PlacesFragment";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText etSearch;
    private Button btnGetDirections;
    private GoogleMap map;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private List<LatLng> directionList;
    private LatLng origin, destination;
    private boolean showMap = false;


    public PlacesFragment() {    }
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
        etSearch = view.findViewById(R.id.etSearch);
        btnGetDirections = view.findViewById(R.id.btnGetDirections);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this::onMapReady);
//        mapFragment.getView().setVisibility(View.GONE);


        // make link for directions

        origin = new LatLng(47.606209, -122.332069);
        destination = new LatLng(47.620422, -122.349358);
        // mark place
        place1 = new MarkerOptions().position(origin).title("Origin");
        place2 = new MarkerOptions().position(destination).title("Origin");





        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String DIRECTION_URL = getUrl(place1.getPosition(), place2.getPosition(), "steps");
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, DIRECTION_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
//                                btnGetDirections.setText("Response is: " + response.substring(0,100));
        //                        tvJsonResponse.setText(response.get);
                                Gson gson = new Gson();
                                JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                                JsonArray routes = jsonObject.getAsJsonArray("routes");
                                Log.i(TAG, routes.toString());
                                JsonElement narrow = routes.get(0);
                                Log.i(TAG, narrow.toString());
                                JsonArray legs = ((JsonObject) narrow).getAsJsonArray("legs");
                                Log.i(TAG, legs.toString());
//                                mapFragment.getView().setVisibility(View.VISIBLE);
//
//                                showMap = true;
                                // make direction line
                                map.addMarker(place1);
                                map.addMarker(place2);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                builder.include(place1.getPosition());
                                builder.include(place2.getPosition());
                                LatLngBounds bounds = builder.build();
                                int padding = 100;
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        googleMap.moveCamera(cu);
                                map.animateCamera(cu);
                                JsonObject overview_polyline  = ((JsonObject) narrow).getAsJsonObject("overview_polyline");
                                String line = overview_polyline.getAsJsonPrimitive("points").getAsString();
                                Log.i(TAG, line.toString());
                                directionList = PolyUtil.decode(line);
                                Log.i(TAG, line.toString());
                                Polyline polyline1 = map.addPolyline( new PolylineOptions().addAll(directionList));
//                                map.addPolyline(PolyUtil.decode(((JsonObject) narrow).getAsJsonArray("legs");))
//                                Polyline polyline1 = map.addPolyline(new PolylineOptions()
//                                        .clickable(true)
//                                        .add(place1.getPosition()).add(place2.getPosition()));
//                        .addAll(directionList));
//        map.addPolyline(PolylineOptions )


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnGetDirections.setText("That didn't work!");
                    }
                });
                // Add the request to the RequestQueue.

                queue.add(stringRequest);
            }
        });


        // AutocompleteSearch
        etSearch.setFocusable(false);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fieldList).build(getActivity());
                startActivityForResult(intent, 100);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            destination = place.getLatLng();
            place2 = new MarkerOptions().position(destination).title("Destination");
            etSearch.setText(place.getName());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, status.getStatusMessage());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + origin.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" +getString(R.string.google_api_key);
        return url;
    }
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }
}