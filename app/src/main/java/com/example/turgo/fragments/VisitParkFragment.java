package com.example.turgo.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.turgo.MainActivity;
import com.example.turgo.R;
import com.example.turgo.models.Park;
import com.parse.Parse;
import com.parse.ParseQuery;


public class VisitParkFragment extends Fragment {
    private static final String TAG = "VisitParkFragment";
    private Button btnParkVisitState;
    private EditText etNumOfPeople;


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
        btnParkVisitState = view.findViewById(R.id.btnParkVisitState);
        etNumOfPeople = view.findViewById(R.id.etNumOfPeople);

        Bundle bundle = this.getArguments();
        Park park = bundle.getParcelable("clickedPark");

        btnParkVisitState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = MainActivity.myCity.getParks().indexOf(park.getObjectId());
                if (btnParkVisitState.getText()=="GO") {
                    if (etNumOfPeople.getText().toString() == null) {
                        Toast.makeText(getContext(), "Please Indicate Number of People", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btnParkVisitState.setText("Leave");
                    MainActivity.myCity.setTree(updateTree(MainActivity.myCity.getTree(), pos, Integer.parseInt(etNumOfPeople.getText().toString())));

                }
                else {
                    MainActivity.myCity.setTree(updateTree(MainActivity.myCity.getTree(), pos, -Integer.parseInt(etNumOfPeople.getText().toString())));
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new ParksFragment()).commit();
                }

            }
        });

    }

    private native int[] updateTree(int tree[], int pos, int val);
}