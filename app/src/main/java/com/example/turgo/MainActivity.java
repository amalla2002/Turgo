package com.example.turgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.turgo.models.Park;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private boolean getInfo = false;
    public static City myCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("native-lib");
        String google_api = getString(R.string.google_api_key);
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

    private void prepareInfoForToday() {
        ParseQuery<City> query = ParseQuery.getQuery(City.class);
        query.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> city, ParseException e) {
                myCity = city.get(0); // IF THERE WERE MORE CITYS, LOOK FOR THE ONE THE USER IS IN
                int nowDate = Calendar.getInstance().getTime().getDate();
                int lastCalc = myCity.getUpdatedAt().getDate();
                if (nowDate == nowDate) {
//                    for (int i = 0; i<myCity.getParks().size(); i += 20) eraseData(); // Needs polishing // TODO: MAKE SURE THIS WORKS, BUT ANOTHER DAY (NOT THAT IMPORTANT =) )
                    String PARKS_URL = "https://data.seattle.gov/resource/j9km-ydkc.json";
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, PARKS_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    saveParks(response);
                                    // Handler delay ensures it works? its possible that im capping out without it
                                    Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            readyMyCity();
                                        }}, 5000);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "myCity?");
                        }
                    });
                    queue.add(stringRequest);
                }
                Log.i(TAG, "Can switch");
            }
        });
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
        List<String> parksOfCity = new ArrayList<>();
        ParseQuery<Park> query = ParseQuery.getQuery(Park.class);
        query.setLimit(200);
        List<Park> parks = null;
        try {parks = query.find();  } catch (ParseException e) {}
        for (Park park : parks) parksOfCity.add(park.getObjectId());
        myCity.setParks(parksOfCity);
        myCity.setPeople(Collections.nCopies(parksOfCity.size(), 0));
        List<Integer> that = Collections.nCopies(parksOfCity.size(), 0);
        myCity.setTree((buildTree(that.stream().mapToInt(Integer::intValue).toArray())));
        try { myCity.save();  } catch (ParseException e) {}
    }

    private void saveParks(String response) {
        Gson gson = new Gson();
        JsonArray jsonParks = new JsonParser().parse(response).getAsJsonArray();
        for (int i = 0; i < jsonParks.size(); ++i) {
            JsonObject thisPark = jsonParks.get(i).getAsJsonObject();
            Park park = new Park();
            park.setParkName(thisPark.get("name").toString());
            park.setNpeople(0);
            try {
                JsonObject loc = thisPark.get("location").getAsJsonObject();
                park.setLatitude(loc.get("latitude").getAsDouble());
                park.setLongitude(loc.get("longitude").getAsDouble());
            } catch (Exception e) {}
            try {
                park.setHours(thisPark.get("hours").getAsString());
            } catch (Exception e) {}
            park.saveInBackground();
        }
    }

    public void eraseData() {
        ParseQuery<Park> query = ParseQuery.getQuery(Park.class);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Park>() {
            @Override
            public void done(List<Park> parks, ParseException e) {
                if (parks == null) return;
                for (Park park : parks) park.deleteInBackground();
            }
        });
    }
    private native int[] buildTree(int[] people);
}