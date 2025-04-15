package com.example.underpressure.api;

import java.util.List;

public class GeocodioResponse {
    public List<Result> results;

    public static class Result {
        public String formatted_address;
        public Location location;
    }

    public static class Location {
        public double lat;
        public double lng;
    }
}
