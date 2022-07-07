package com.example.turgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.turgo.fragments.ParksFragment;
import com.example.turgo.fragments.PlacesFragment;
import com.example.turgo.fragments.ProfileFragment;
import com.example.turgo.models.City;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static Boolean visitingPark = false;
    public static Number visitingWith = 0;
    public static Number visitingPos = -1;
    private String PARKS_URL = "https://data.seattle.gov/resource/j9km-ydkc.json";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private City myCity;
    private List<String> hours, parkNames;
    private List<Number> latitudes , longitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("native-lib");
        String google_api = getString(R.string.google_api_key_mine);
        Places.initialize(getApplicationContext(), google_api);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragments(item.getItemId());
                return true;
            }
        });
        prepareInfoForToday();
    }

    private void prepareInfoForToday()  {
        myCity = getCity();
        int nowDate = Calendar.getInstance().getTime().getDate();
        int lastCalc = myCity.getUpdatedAt().getDate();
        if (nowDate == nowDate) {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, PARKS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            saveParks(response);
                            readyMyCity();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
            });
            queue.add(stringRequest);
        }
    }

    public static City getCity()  {
        try {
            ParseQuery<City> query = ParseQuery.getQuery(City.class);
            City city = query.find().get(0);
            return city;
        } catch (Exception e) {
            return new City();
        }
    }

    private void switchFragments(int itemId) {
        Fragment fragment;
        switch (itemId) {
            case R.id.action_parks:
                fragment = new ParksFragment();
                break;
            case R.id.action_places:
                fragment = new PlacesFragment();
                break;
            case R.id.action_profile:
            default:
                fragment = new ProfileFragment();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    private void readyMyCity() {
        myCity.setParks(parkNames);
        myCity.setHours(hours);
        myCity.setLatitude(latitudes);
        myCity.setLongitude(longitudes);
        List<Integer> that = Collections.nCopies(parkNames.size(), 0);
        myCity.setTree((buildTree(that.stream().mapToInt(Integer::intValue).toArray())));
        try { myCity.save();  } catch (ParseException e) {}
    }

    private void saveParks(String response) {
        JsonArray jsonParks = new JsonParser().parse(response).getAsJsonArray();
        parkNames = new ArrayList<>(); hours = new ArrayList<>(); latitudes = new ArrayList<>(); longitudes = new ArrayList<>();
        for (int i = 0; i < jsonParks.size(); ++i) {
            JsonObject thisPark = jsonParks.get(i).getAsJsonObject();
            parkNames.add(thisPark.get("name").toString());
            try {
                JsonObject loc = thisPark.get("location").getAsJsonObject();
                latitudes.add(loc.get("latitude").getAsDouble());
                longitudes.add(loc.get("longitude").getAsDouble());
            } catch (Exception e) {
                latitudes.add(0);
                longitudes.add(0);
            }
            try {
                hours.add(thisPark.get("hours").getAsString());
            } catch (Exception e) {
                hours.add("Not provided");
            }
        }
    }
    private native int[] buildTree(int[] people);
}