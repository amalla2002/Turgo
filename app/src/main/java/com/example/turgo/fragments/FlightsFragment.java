package com.example.turgo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.turgo.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;


public class FlightsFragment extends Fragment {

    private static final String TAG  = "FlightsFragment";
    private String goOn;
    private String leaveOn;
    private Boolean editingGoOn = false;
    private String hotelName;
    private EditText etHotel;
    private Button btnGoOn;
    private Button btnLeaveOn;
    private Button btnFind;
    private MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

    public FlightsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setBtnLogic();
    }

    private void setBtnLogic() {
        btnGoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDateBuilder.setTitleText("SELECT ARRIVAL DATES");
                editingGoOn = true; displayCallendar();
            }
        });

        btnLeaveOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDateBuilder.setTitleText("SELECT RETURNING DATES");
                editingGoOn = false; displayCallendar();
            }
        });
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotelName = etHotel.getText().toString();
                Log.i(TAG, "\n"+hotelName+"\n"+goOn+"\n"+leaveOn);
            }
        });
    }

    private void findViews(View view) {
        etHotel = view.findViewById(R.id.etHotel);
        btnGoOn = view.findViewById(R.id.btnGoOn);
        btnLeaveOn = view.findViewById(R.id.btnLeaveOn);
        btnFind = view.findViewById(R.id.btnFind);
    }

    private void displayCallendar() {
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                if (editingGoOn == true) goOn = materialDatePicker.getHeaderText();
                else leaveOn = materialDatePicker.getHeaderText();
            }
        });
    }
}