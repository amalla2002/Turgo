package com.example.turgo;


import android.app.Application;

import com.amadeus.Amadeus;
import com.amadeus.Params;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.referenceData.Locations;
import com.amadeus.resources.Location;

public class AmadeusApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Amadeus amadeus = Amadeus.builder(getString(R.string.amadeus_api_key), getString(R.string.amedeus_api_secret)).build();

        // Flight Price Analysis
        ItineraryPriceMetric[] metrics = amadeus.analytics.itineraryPriceMetrics.get(Params
                .with("originIataCode", "MAD")
                .and("destinationIataCode", "CDG")
                .and("departureDate", "2021-03-21"));
        // Flight Cheapest Date Search
        FlightDate[] flightDates = amadeus.shopping.flightDates.get(Params
                .with("origin", "MAD")
                .and("destination", "MUC"));
        // Flight Availabilites Search POST
        // body can be a String version of your JSON or a JsonObject
        FlightAvailability[] flightAvailabilities
                = amadeus.shopping.availability.flightAvailabilities.post(body);
        // Flight Price Analysis
        ItineraryPriceMetric[] metrics = amadeus.analytics.itineraryPriceMetrics.get(Params
                .with("originIataCode", "MAD")
                .and("destinationIataCode", "CDG")
                .and("departureDate", "2021-03-21"));

        // Hotel autocomplete names
        Hotel[] result = amadeus.referenceData.locations.hotel.get(Params
                .with("keyword", "PARI")
                .and("subType", "HOTEL_GDS")
                .and("countryCode", "FR")
                .and("lang", "EN")
                .and("max", "20"));
        // Hotel Offers Search API v3
        // Get multiple hotel offers
        HotelOfferSearch[] offers = amadeus.shopping.hotelOffersSearch.get(Params
                .with("hotelIds", "MCLONGHM")
                .and("adults", 1)
                .and("checkInDate", "2022-11-22")
                .and("roomQuantity", 1)
                .and("paymentPolicy", "NONE")
                .and("bestRateOnly", true));
        // Get hotel offer pricing by offer id
        HotelOfferSearch offer = amadeus.shopping.hotelOfferSearch("QF3MNOBDQ8").get();





    }
}
