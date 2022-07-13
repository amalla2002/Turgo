package com.example.turgo.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.turgo.AmadeusApplication;
import com.example.turgo.R;
import com.example.turgo.adapter.HotelAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.JsonArray;
import org.javatuples.Pair;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class FlightsFragment extends Fragment {
    private static final String TAG  = "FlightsFragment";
    private List<LocalDate> goOn, leaveOn;
    private String origin, dest, currencyForHotel;
    private Boolean editingGoOn = false;
    private EditText etOrigin, etDestination;
    private Button btnGoOn, btnLeaveOn, btnFind, btnHotel;
    private Pair<JsonArray, Number> flight;
    private JsonArray[] goingItenerary = new JsonArray[366*2], returningItenerary = new JsonArray[366*2];
    private List<Integer> goOnIndices = new ArrayList<>(), leaveOnIndices = new ArrayList<>();
    private double[] goingPrice = new double[366*2], returningPrice = new double[366*2], hotelPrice = new double[366*2];
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

                getFields();
//                fillFlightCostsAndIteneraryArray();
//                findHotelCost();
                generateTestData(1);
                for (int i = 194; i<205; ++i) {
                    Log.i(TAG, " " +
                            String.valueOf(goingPrice[i]) + " " +
                            String.valueOf(returningPrice[i]) + " " +
                            String.valueOf(hotelPrice[i]));
                }

                int[] thiz = {goOnIndices.get(0), goOnIndices.get(goOnIndices.size()-1)}, that = {leaveOnIndices.get(0), leaveOnIndices.get(leaveOnIndices.size()-1)};
                Log.i(TAG, Arrays.stream(thiz).boxed().collect(Collectors.toList()).toString());
                Log.i(TAG, Arrays.stream(that).boxed().collect(Collectors.toList()).toString());
                double[] ans = findBestCombination(goingPrice, returningPrice, hotelPrice, thiz, that);
                Log.i(TAG, Arrays.stream(ans).boxed().collect(Collectors.toList()).toString());
            }
        });
        btnHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, new HotelSelectFragment()).commit();
            }
        });
    }

    private void generateTestData(int testing) {
        // TODO: FILL goingPrice, returningPrice, hotelPrice, thiz, that
        //            |           doubles 366*2             |
        switch (testing) {
            case 1: // ans is both extremes, longest flight, 408.9
                goingPrice[195] = 200;
                goingPrice[196] = 300;
                goOnIndices.add(195);
                goOnIndices.add(196);

                returningPrice[202] = 300;
                returningPrice[203] = 200;
                leaveOnIndices.add(202);
                leaveOnIndices.add(203);
                for (int i = 0; i<hotelPrice.length-1; ++i) hotelPrice[i] = 0.1;
                break; //
            case 2:
                goingPrice[195] = 200;
                goingPrice[196] = 300;
                goOnIndices.add(195);
                goOnIndices.add(196);

                returningPrice[202] = 300;
                returningPrice[203] = 200;
                leaveOnIndices.add(202);
                leaveOnIndices.add(203);
                for (int i = 0; i<hotelPrice.length-1; ++i) hotelPrice[i] = 150.0;
        }


    }

    private void findHotelCost() {
        Pair<double[], String> data = AmadeusApplication.fetchHotelPrices(HotelAdapter.clickedHotel, goOn.get(0), leaveOn.get(leaveOn.size()-1));
        hotelPrice = data.getValue0();
        currencyForHotel = data.getValue1();
    }

    private void fillFlightCostsAndIteneraryArray() {
        for (LocalDate thisDate : goOn) {
            flight = AmadeusApplication.fetchPlane(origin, dest, thisDate.toString());
            int i = getIndex(thisDate);
            goOnIndices.add(i);
            goingItenerary[i] = flight.getValue0();
            goingPrice[i] =  flight.getValue1().doubleValue();
        }
        for (LocalDate thisDate : leaveOn) {
            flight = AmadeusApplication.fetchPlane(origin, dest, thisDate.toString());
            int i = getIndex(thisDate);
            leaveOnIndices.add(i);
            returningItenerary[i] = flight.getValue0();
            returningPrice[i] =  flight.getValue1().doubleValue();
        }
    }

    private int getIndex(LocalDate thisDate) {
        int year = thisDate.getYear()-LocalDate.now().getYear();
        return thisDate.getDayOfYear()+Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)*year;
    }

    private void getFields() {
        origin = etOrigin.getText().toString();
        dest = etDestination.getText().toString();
    }

    private void findViews(View view) {
        btnHotel = view.findViewById(R.id.btnHotel);
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
                LocalDate startDate = formatDate(selection.first).plusDays(1);
                LocalDate endDate = formatDate(selection.second).plusDays(1);
                List<LocalDate> dates = getDays(startDate, endDate);
                if (editingGoOn == true) goOn = dates;
                else leaveOn = dates;
            }

            private List<LocalDate> getDays(LocalDate startDate, LocalDate endDate) {
                long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);
                List<LocalDate> days = LongStream.range(0, numOfDays+1).mapToObj(startDate::plusDays).collect(Collectors.toList());
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

    private native double[] findBestCombination(double[] goingPriceFlights, double[] returningPriceFlights, double[] hotelPrice, int[] goingIndices, int[] returningIndices);// the two days on which to book
}