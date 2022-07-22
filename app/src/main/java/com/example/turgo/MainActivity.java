package com.example.turgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.turgo.fragments.FlightsFragment;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Prepares Fragment switcher (bottom navigation view)
 *
 * Fetches Park data if it has not been fetched that day
 * Also builds the segment tree if it has to fetch new data
 *
 * Initializes variables so that when Places Fragment is selected
 * it shows the correct view, instead of the park version
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    /**
     * this value is used in the minimum segmented tree
     * it is used as precaution, a segmentation fault is not expected
     */
    public static final int segmentationFaultPrecaution = 24;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragments(item.getItemId());
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        prepareInfoForToday();
    }

    /**
     * Check to see if city information has been prepared today
     * if it hasn't fetches it
     */
    private void prepareInfoForToday()  {
        myCity = getCity();
        Date lastUpdatedDate = myCity.getUpdatedAt();
        int nowDate = LocalDate.now().getDayOfYear();
        int lastCalc = Integer.parseInt(new SimpleDateFormat("DDD").format(lastUpdatedDate));
        if (nowDate != lastCalc) {
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

    /**
     * Static method to get City info
     *
     * @return current City, right now SEA
     */

    public static City getCity()  {
        try {
            ParseQuery<City> query = ParseQuery.getQuery(City.class);
            City city = query.find().get(0);
            return city;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return new City();
        }
    }

    /**
     *
     * @param itemId Id of bottom navigation item clicked
     */
    private void switchFragments(int itemId) {
        Fragment fragment;
        switch (itemId) {
            case R.id.action_parks:
                fragment = new ParksFragment();
                break;
            case R.id.action_places:
                fragment = new PlacesFragment();
                break;
            case R.id.action_flights:
                fragment = new FlightsFragment();
                break;
            case R.id.action_profile:
            default:
                fragment = new ProfileFragment();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    /**
     * Fills out all fields in myCity
     */
    private void readyMyCity() {
        myCity.setParks(parkNames);
        myCity.setHours(hours);
        myCity.setLatitude(latitudes);
        myCity.setLongitude(longitudes);
        List<Integer> that = Collections.nCopies(parkNames.size(), 0);
        myCity.setTree((buildTree(that.stream().mapToInt(Integer::intValue).toArray())));
        try { myCity.save();  } catch (ParseException e) {}
    }

    /**
     * Parses response given by the seattle gov api
     * Then collects it into arrays, to be saved in readyMyCity
     * Finally launches build tree
     *
     * @param response data given by the seattle gov api
     */
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