package com.example.turgo.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.turgo.MainActivity;
import com.example.turgo.ParseApplication;
import com.example.turgo.R;
import com.example.turgo.adapter.ParkAdapter;
import com.example.turgo.models.City;
import org.javatuples.Quintet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Takes in how many people are going to the park
 * updates tree and takes you to PlacesFragment
 * to display the route
 */
public class VisitParkFragment extends Fragment {
    private static final String TAG = "VisitParkFragment";
    public static Number lat, lng;
    private Button btnParkVisitState;
    private EditText etNumOfPeople;
    private Quintet<String, String, Number, Number, Number> park;
    private City myCity;
    private static final Integer INF = (int) 1e9+7, notifyForThisAmountOfPeople = 500;

    public VisitParkFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_park, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.loadLibrary("native-lib");
        setInfo(view);
        btnParkVisitState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer Npeople = prepareInfo();
                if(Npeople == INF) return;
                if (Npeople>notifyForThisAmountOfPeople) ParseApplication.parkVeryPopulated(park.getValue0(), Npeople);
                showRoute();
            }
        });
    }

    private void setInfo(View view) {
        btnParkVisitState = view.findViewById(R.id.btnParkVisitState);
        etNumOfPeople = view.findViewById(R.id.etNumOfPeople);
        myCity = MainActivity.getCity();
        park = ParkAdapter.clickedPark;
    }

    private int prepareInfo() {
        int pos = myCity.getParks().indexOf(park.getValue0());
        int val;
        try {
            val = Integer.parseInt(etNumOfPeople.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please indicate num of people", Toast.LENGTH_SHORT).show();
            return INF;
        }
        MainActivity.visitingPark = true;
        MainActivity.visitingWith = val*-1;
        MainActivity.visitingPos = pos;
        lat = park.getValue3();
        lng = park.getValue4();
        int[] updatedTree = updateTree(myCity.getTree(), pos, val);
        List<Integer> listTree = Arrays.stream(updatedTree).boxed().collect(Collectors.toList());
        myCity.setTree(updatedTree);
        myCity.saveInBackground();
        return listTree.get((listTree.size()-24)/2 + pos);
    }

    private void showRoute() {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment fragment = new PlacesFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    private native int[] updateTree(int tree[], int pos, int val);
}