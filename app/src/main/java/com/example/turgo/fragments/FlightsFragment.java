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
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Allows you to select range of dates for
 * arrival, departure and days of stay.
 * Airports for origin must be written destination
 * Select Hotel button opens a list of hotels in another fragment
 *
 * When Find button is pressed, information is collected
 * and findBestCombination cpp function is launched
 */
public class FlightsFragment extends Fragment {
    private static final String TAG  = "FlightsFragment";
    private List<LocalDate> goOn, leaveOn;
    private String origin, dest, currencyForHotel;
    private Boolean editingGoOn = false;
    private EditText etOrigin, etDestination, etMinDays, etMaxDays;
    private Button btnGoOn, btnLeaveOn, btnFind, btnHotel;
    private TextView tvBestCombination;
    private Pair<JsonArray, Number> flight;
    private JsonArray[] goingItenerary = new JsonArray[366*2], returningItenerary = new JsonArray[366*2];
    private List<Integer> goOnIndices = new ArrayList<>(), leaveOnIndices = new ArrayList<>();
    private double[] goingPrice = new double[366*2], returningPrice = new double[366*2], hotelPrice = new double[366*2];
    private MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
    int minDays, maxDays;

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

        /**
         * opens calendar and saves date range to go on
         */
        btnGoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDateBuilder.setTitleText("SELECT ARRIVAL DATES");
                editingGoOn = true; displayCalendar();
            }
        });

        /**
         * opens calendar and saves date range to leave on
         */
        btnLeaveOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDateBuilder.setTitleText("SELECT RETURNING DATES");
                editingGoOn = false; displayCalendar();
            }
        });

        /**
         * gets flight data, hotel data and launches best combination computation
         */
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFields();
//                fillFlightCostsAndIteneraryArray();
//                findHotelCost();
                generateTestData();
                int[] thiz = {goOnIndices.get(0), goOnIndices.get(goOnIndices.size()-1)}, that = {leaveOnIndices.get(0), leaveOnIndices.get(leaveOnIndices.size()-1)};
                double[] ans = findBestCombination(goingPrice, returningPrice, hotelPrice, thiz, that, minDays, maxDays);
                tvBestCombination.setText("FOR " + String.valueOf(ans[0])+" YOU CAN GO TO " + dest +
                        " ON " + LocalDate.ofYearDay(LocalDate.now().getYear(), (int) ans[1])+" AND RETURN ON "
                        + LocalDate.ofYearDay(LocalDate.now().getYear(), (int) ans[2]) + "WHILE STAYING ON " + HotelAdapter.clickedHotelName);
            }
        });

        /**
         * shows recycler view of hotels in area
         */
        btnHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, new HotelSelectFragment()).commit();
            }
        });
    }

    /**
     * work around to Amadeus errors
     *
     * Airport: SEA to LCY
     * arrival date: 08-04 to 08-05
     * return date: 08-11 to 08-12
     * staying at JW Marriott Grosvenor House London airport
     * hotel id: MCLONGHM
     */
    private void generateTestData() {
        goingPrice[216] = 707.60;
        goingPrice[217] = 1601.90;
        goOnIndices.add(216);
        goOnIndices.add(217);
        returningPrice[223] = 1198.66;
        returningPrice[224] = 1209.96;
        leaveOnIndices.add(223);
        leaveOnIndices.add(224);
        hotelPrice[216] = 600.00;
        hotelPrice[217] = 600.00;
        hotelPrice[218] = 697.00;
        hotelPrice[219] = 600.00;
        hotelPrice[220] = 600.00;
        hotelPrice[221] = 600.00;
        hotelPrice[222] = 600.00;
        hotelPrice[223] = 600.00;
    }

    /**
     * finds hotel cost for range of stay
     */
    private void findHotelCost() {
        Pair<double[], String> data = AmadeusApplication.fetchHotelPrices(HotelAdapter.clickedHotel, goOn.get(0), leaveOn.get(leaveOn.size()-1));
        hotelPrice = data.getValue0();
        currencyForHotel = data.getValue1();
    }

    /**
     * finds flights cost for range of stay
     */
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

    /**
     * Transform a date into an index. index is day of year, current year jan1 being 0,
     * next year jan1 being 365 or 366 depending on year
     *
     * @param thisDate date to be converted
     * @return index in array for thisDate
     */
    private int getIndex(LocalDate thisDate) {
        int year = thisDate.getYear()-LocalDate.now().getYear();
        return thisDate.getDayOfYear()+Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)*year;
    }

    /**
     * Collects info from fields
     */
    private void getFields() {
        origin = etOrigin.getText().toString();
        dest = etDestination.getText().toString();
        minDays = Integer.valueOf(etMinDays.getText().toString());
        maxDays = Integer.valueOf(etMaxDays.getText().toString());
    }

    /**
     * Assigns views
     *
     * @param view view where objects reside
     */
    private void findViews(View view) {
        btnHotel = view.findViewById(R.id.btnHotel);
        etOrigin = view.findViewById(R.id.etOrigin);
        etDestination = view.findViewById(R.id.etDestination);
        etMaxDays = view.findViewById(R.id.etMaxDays);
        etMinDays = view.findViewById(R.id.etMinDays);
        btnGoOn = view.findViewById(R.id.btnGoOn);
        btnLeaveOn = view.findViewById(R.id.btnLeaveOn);
        btnFind = view.findViewById(R.id.btnFind);
        tvBestCombination = view.findViewById(R.id.tvBestCombination);
    }

    /**
     * opens materia design date picker and when a date is selected it assigns
     * the list of days to its corresponding category (going/leaving)
     */
    private void displayCalendar() {
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

    private native double[] findBestCombination(double[] goingPriceFlights, double[] returningPriceFlights, double[] hotelPrice, int[] goingIndices, int[] returningIndices, int minDays, int maxDays);// the two days on which to book
}