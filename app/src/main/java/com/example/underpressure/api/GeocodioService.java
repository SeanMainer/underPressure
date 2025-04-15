package com.example.underpressure.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//settoing up the interface to connect to the geocodio API
public interface GeocodioService {
    @GET("geocode")
    Call<GeocodioResponse> validateAddress(
            @Query("address") String address,
            @Query("api_key") String apiKey
    );

}
