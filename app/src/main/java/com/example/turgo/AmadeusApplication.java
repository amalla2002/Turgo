package com.example.turgo;

import android.app.Application;
import android.util.Log;
import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.Response;
import com.amadeus.exceptions.ResponseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javatuples.Pair;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Makes a call to get the best flight offer for a given date
 * Between to given locations
 *
 * Retrieves list of hotel, saving both the name and Id
 * With the Id the price is later fetched for a range of dates
 */
public class AmadeusApplication extends Application {
    public static final String TAG = "AmadeusApplication";
    private static String key;
    private static String secret;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    /**
     * Fetches plane price data for a given dates and parses it
     *
     * @param origin place from where you are departing
     * @param dest place from where you are arriving
     * @param date date for flight
     * @return returns itenerary and price
     */
    public static Pair<JsonArray, Number> fetchPlane(String origin, String dest, String date) {
        key = "";
        secret = "";
        JsonArray planes = new JsonArray();
        Number price = 1e9+7;
        try {
            Amadeus amadeus = Amadeus.builder(key, secret).build();
            Response response = amadeus.get("/v2/shopping/flight-offers", Params
                    .with("originLocationCode", origin) // "SYD"
                    .and("destinationLocationCode", dest) // "BKK"
                    .and("departureDate", date) // "2022-11-01" // YYYY MM DD
                    .and("adults", 1)
                    .and("nonStop", false)
                    .and("max", 1));
            if(response.getStatusCode() != 200) {
                Log.i(TAG, "Wrong status code: " + (response.getStatusCode()));
            }
            JsonObject data =  response.getData().getAsJsonArray().get(0).getAsJsonObject();
            planes = data.getAsJsonArray("itineraries").get(0).getAsJsonObject().getAsJsonArray("segments");
            price = data.getAsJsonObject("price").getAsJsonPrimitive("grandTotal").getAsNumber();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return Pair.with(planes, price);
    }

    /**
     * Sends Amadeus an api request to the /v1/reference-data/locations/hotels/by-city endpoint
     * Parses out the names and Ids
     *
     * @param destination place where the hotels are being looked up
     * @return returns list of hotel names with their ID (amadeus)
     */
    public static List<Pair<String, String>> fetchHotels(String destination) {
        key = "";
        secret = "";
        List<Pair<String, String>> hotels = new ArrayList<>();
        try {
            Amadeus amadeus = Amadeus.builder(key,secret).build();
            Response response = amadeus.get("/v1/reference-data/locations/hotels/by-city", Params.with("cityCode", destination));
            if(response.getStatusCode() != 200) {
                Log.i(TAG, "Wrong status code: " + (response.getStatusCode()));
            }
            JsonArray data = response.getData().getAsJsonArray();
            for (JsonElement ob : data) {
                String name, hotelId;
                name = ob.getAsJsonObject().get("name").toString();
                hotelId = ob.getAsJsonObject().get("hotelId").toString();
                hotels.add(Pair.with(name, hotelId));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return hotels;
    }

    /**
     * Sends Amadeus an api request to the /v3/shopping/hotel-offers endpoint
     * parses it and returns the price for staying one night for a range of dates
     *
     * @param hotelId Id of hotel, gotten with method fetchHotel
     * @param start earliest arrival date
     * @param end latest departure date - 1
     * @return array of prices and currency type
     */
    public static Pair<double[], String> fetchHotelPrices(String hotelId, LocalDate start, LocalDate end) {
        key = "";
        secret = "";
        String currency = "USD";
        double[] prices = new double[366*2+4];
        long numOfDays = ChronoUnit.DAYS.between(start, end);
        List<LocalDate> days = LongStream.range(0, numOfDays+1).mapToObj(start::plusDays).collect(Collectors.toList());
        Amadeus amadeus = Amadeus.builder(key,secret).build();
        for (LocalDate date : days) {
            int i = date.getDayOfYear();
            try {
                Response response = amadeus.get("/v3/shopping/hotel-offers", Params
                        .with("hotelIds", hotelId)
                        .and("adults", 1)
                        .and("checkInDate", date)
                        .and("roomQuantity", 1)
                        .and("paymentPolicy", "NONE")
                        .and("bestRateOnly", "true"));
                JsonObject data = response.getData().getAsJsonArray().get(0).getAsJsonObject();
                JsonObject offer = data.getAsJsonArray("offers").getAsJsonArray().get(0).getAsJsonObject();
                JsonObject price = offer.getAsJsonObject("price").getAsJsonObject();
                currency = price.get("currency").getAsString();
                prices[i] = price.get("total").getAsDouble();
            } catch (ResponseException e) {
                Log.e(TAG, e.toString());
                prices[i] = 1e9+7;
            }
        }
        return Pair.with(prices, currency);
    }
}
