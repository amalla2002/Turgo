package com.example.turgo.fragments;

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
import com.example.turgo.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.JsonArray;
import org.javatuples.Pair;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class FlightsFragment extends Fragment {
    private static final String TAG  = "FlightsFragment";
    private List<LocalDate> goOn, leaveOn;
    private String hotelName;
    private String origin;
    private String dest;
    private Boolean editingGoOn = false;
    private EditText etHotel, etOrigin, etDestination;
    private Button btnGoOn, btnLeaveOn, btnFind;
    private Pair<JsonArray, Number> flight;
    private JsonArray[] goingItenerary, returningItenerary;
    private double[] goingPrice = new double[384*2], returningPrice = new double[384*2]; // 32 days per month (null days will have 0 cost
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
                Log.i(TAG, "\n"+hotelName+"\n"+goOn+"\n"+leaveOn);
                getFields();
                fillFlightCostsAndIteneraryArray();
            }
        });
    }

    private void fillFlightCostsAndIteneraryArray() {
        for (LocalDate thisDate : goOn) {
//            flight = AmadeusApplication.fetchPlane(origin, dest, thisDate.toString());
            int i = getIndex(thisDate);
            goingItenerary[i] = flight.getValue0();
            goingPrice[i] =  flight.getValue1().doubleValue();
        }
        for (LocalDate thisDate : leaveOn) {
//            flight = AmadeusApplication.fetchPlane(origin, dest, thisDate.toString());
            int i = getIndex(thisDate);
            returningItenerary[i] = flight.getValue0();
            returningPrice[i] =  flight.getValue1().doubleValue();
        }
    }

    private int getIndex(LocalDate thisDate) {
        int year, month, day;
        year = thisDate.getYear()-LocalDate.now().getYear();
        month = thisDate.getMonthValue();
        day = thisDate.getDayOfMonth();
        return year*384+month*32+day;
    }

    private void getFields() {
        hotelName = etHotel.getText().toString();
        origin = etOrigin.getText().toString();
        dest = etDestination.getText().toString();
    }

    private void findViews(View view) {
        etHotel = view.findViewById(R.id.etHotel);
        etOrigin = view.findViewById(R.id.etOrigin);
        etDestination = view.findViewById(R.id.etDestination);
        btnGoOn = view.findViewById(R.id.btnGoOn);
        btnLeaveOn = view.findViewById(R.id.btnLeaveOn);
        btnFind = view.findViewById(R.id.btnFind);
    }

    private void displayCallendar() {
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<androidx.core.util.Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(androidx.core.util.Pair<Long, Long> selection) {
                LocalDate startDate = formatDate(selection.first);
                LocalDate endDate = formatDate(selection.second);
                List<LocalDate> dates = getDays(startDate, endDate);
                if (editingGoOn == true) goOn = dates;
                else leaveOn = dates;
            }

            private List<LocalDate> getDays(LocalDate startDate, LocalDate endDate) {
                long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);
                List<LocalDate> days = LongStream.range(0, numOfDays).mapToObj(startDate::plusDays).collect(Collectors.toList());
                return days;
            }

            private LocalDate formatDate(Long date) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(date);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate  = format.format(calendar.getTime());
                return LocalDate.parse(formattedDate);
            }
        });
    }
}