package com.example.turgo;

import android.app.Application;
import android.util.Log;
import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.javatuples.Pair;

public class AmadeusApplication extends Application {
    public static final String TAG = "AmadeusApplication";
    private static String key;
    private static String secret;
    @Override
    public void onCreate() {
        super.onCreate();
        key = getString(R.string.amadeus_api_key);
        secret = getString(R.string.amedeus_api_secret);
//        // Hotel autocomplete names
//        Hotel[] result = amadeus.referenceData.locations.hotel.get(Params
//                .with("keyword", "PARI")
//                .and("subType", "HOTEL_GDS")
//                .and("countryCode", "FR")
//                .and("lang", "EN")
//                .and("max", "20"));
//        // Hotel Offers Search API v3
//        // Get multiple hotel offers
//        HotelOfferSearch[] offers = amadeus.shopping.hotelOffersSearch.get(Params
//                .with("hotelIds", "MCLONGHM")
//                .and("adults", 1)
//                .and("checkInDate", "2022-11-22")
//                .and("roomQuantity", 1)
//                .and("paymentPolicy", "NONE")
//                .and("bestRateOnly", true));
//        // Get hotel offer pricing by offer id
//        HotelOfferSearch offer = amadeus.shopping.hotelOfferSearch("QF3MNOBDQ8").get();
    }

    public static Pair<JsonArray, Number> fetchPlane(String origin, String dest, String date) {
        JsonArray planes = null;
        Number price = null;
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
            Log.i(TAG, e.toString());
        }
        return Pair.with(planes, price);
    }
}
