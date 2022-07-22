package com.example.turgo.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
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
import com.example.turgo.ScrapperApplication;
import com.example.turgo.models.City;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Autocomplete search from google api to get a places
 * geographical location
 *
 * Map and direction api to draw a route from current location
 * to selected place on autocomplete search
 *
 * Shake listener to read text on shake
 * Starts a thread to fetch this text, happens on Get Directions
 *
 * Map is reused when coming from VisitParkFragment
 * GetDirection button hides and Leave Park Button is shown
 * segment tree is updated upon leaving park
 */
public class PlacesFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = "PlacesFragment";
    public static boolean newSummary = false;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText etSearch;
    private Button btnGetDirections;
    private GoogleMap map;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private List<LatLng> directionList;
    private LatLng origin, destination;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button btnLeavePark;
    private City myCity;
    private SensorManager sensorManager;
    private float accel;
    private float accelCurrent;
    private float accelLast;
    public static String summary;
    private static TextToSpeech TTS;

    public PlacesFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.loadLibrary("native-lib");
        findViews(view);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this::onMapReady);
        shakeListenerSetup();
        defaultLocations();
        setBtnLogic();
    }

    private void setBtnLogic() {

        /**
         * Gets directions and laucnhes scrapper to retrieve shake on listen data
         */
        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDirection();
                String placeForLink = etSearch.getText().toString().replace(' ', '_');
                ScrapperApplication thread = new ScrapperApplication(placeForLink);
                thread.start();
                Toast.makeText(getContext(), "Shake for surprise", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * creates autocomplete search fragment as an activity
         */
        etSearch.setFocusable(false);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getActivity());
                startActivityForResult(intent, 100);
            }
        });

        /**
         * Normalizes view
         */
        btnLeavePark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePeople();
                normalizeView();
            }
        });
    }

    /**
     * Work around location issues with the emulator
     */
    private void defaultLocations() {
        if (place1 == null) {
            origin = new LatLng(47.629229, -122.341229);
            place1 = new MarkerOptions().position(origin).title("Default origin");
        }
        if (place2 == null) {
            destination = new LatLng(47.615698, -122.332956);
            place2 = new MarkerOptions().position(destination).title("Default destination");
        }
    }

    /**
     * assigns views
     *
     * @param view view from where to find objects
     */
    private void findViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnGetDirections = view.findViewById(R.id.btnGetDirections);
        btnLeavePark = view.findViewById(R.id.btnLeavePark);
        btnLeavePark.setVisibility(View.INVISIBLE);
    }

    /**
     * initializes tools for the shake listener
     */
    private void shakeListenerSetup() {
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(sensorManager).registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
    }

    /**
     * Senses shake via acceleration, if shaked enough and newSummary is true
     * reads summary and sets newSummary to false
     */
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelLast = accelCurrent;
            accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = accelCurrent - accelLast;
            accel = accel * 0.9f + delta;
            if (accel > 12) {
                if (!newSummary) return;
                TTS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = TTS.setLanguage(Locale.US);
                            TTS.speak(summary, TextToSpeech.QUEUE_FLUSH, null);
                            newSummary = false;
                        }
                    }
                });

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * handles destruction of text to speech
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
    }

    /**
     * reasigns the listener
     */
    @Override
    public void onResume() {
        sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    /**
     * stops listener
     */
    @Override
    public void onPause() {
        sensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    /**
     * Launched when coming from VisitParkFragment, makes it so that
     * PlacesFragment looks like the normal PlacesFragment
     */
    private void normalizeView() {
        map.clear();
        btnLeavePark.setVisibility(View.INVISIBLE);
        btnGetDirections.setVisibility(View.VISIBLE);
    }

    /**
     * Updates tree to reflect the people leaving the park
     */
    private void removePeople() {
        int val = (int) MainActivity.visitingWith;
        int pos = (int) MainActivity.visitingPos;
        MainActivity.visitingPark = false;
        MainActivity.visitingWith = 0;
        MainActivity.visitingPos = -1;
        myCity.setTree(updateTree(myCity.getTree(), pos, val));
        myCity.saveInBackground();
    }

    /**
     * sets the device's location and gets route to put polyline in
     */
    private void getDirection() {
        setMyLocation();
        String DIRECTION_URL = getUrl(place1.getPosition(), place2.getPosition(), "steps");
        getRoute(DIRECTION_URL);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                putLine();
            }
        }, 3000);
    }

    /**
     * launches request for google's direction endpoint
     * saves the polyline
     *
     * @param DIRECTION_URL takes in the URL for the api request
     */
    private void getRoute(String DIRECTION_URL) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DIRECTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        JsonArray routes = jsonObject.getAsJsonArray("routes");
                        JsonElement narrow = routes.get(0);
                        JsonArray legs = ((JsonObject) narrow).getAsJsonArray("legs");
                        JsonObject overview_polyline  = ((JsonObject) narrow).getAsJsonObject("overview_polyline");
                        String line = overview_polyline.getAsJsonPrimitive("points").getAsString();
                        directionList = PolyUtil.decode(line);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnGetDirections.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    /**
     * Takes in information about place clicked in autocomplete search fragment
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            destination = place.getLatLng();
            place2 = new MarkerOptions().position(destination).title("Destination");
            etSearch.setText(place.getName());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, status.getStatusMessage());
        }
    }

    /**
     * Prepares map empty map, if coming from VisitingParkFragment
     * then it puts the polyline in from the beginning
     *
     * @param googleMap empty map
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (MainActivity.visitingPark) {
            btnGetDirections.setVisibility(View.INVISIBLE);
            btnLeavePark.setVisibility(View.VISIBLE);
            if (VisitParkFragment.lat.doubleValue() == 0) {
                destination = new LatLng(47.615698, -122.332956);
                place2 = new MarkerOptions().position(destination).title("Default destination");
            } else {
                destination = new LatLng(VisitParkFragment.lat.doubleValue(), VisitParkFragment.lng.doubleValue());
                place2 = new MarkerOptions().position(destination).title("destination");
            }
            myCity = MainActivity.getCity();
            getDirection();
        }
    }

    /**
     * Adds markers, line and moves map view to include both markers
     */
    public void putLine() {
        map.clear();
        defaultLocations();
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

    /**
     * Formats url for directions endpoint
     *
     * @param origin device's lat lng
     * @param dest park or autocomplete latlng
     * @param directionMode walkiking default
     * @return
     */
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" +getString(R.string.google_api_key_mine   );
        return url;
    }

    /**
     * sets location of the device
     */
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
                        defaultLocations();
                    }
                }
            });
        } else { // when permision denied
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private native int[] updateTree(int[] segmentedTree, int pos, int val);
}