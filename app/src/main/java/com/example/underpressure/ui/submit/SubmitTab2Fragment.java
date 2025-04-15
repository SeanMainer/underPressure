package com.example.underpressure.ui.submit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.underpressure.R;
import com.example.underpressure.api.GeocodioService;
import com.example.underpressure.api.GeocodioResponse;
import com.example.underpressure.BuildConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationRequest;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubmitTab2Fragment extends Fragment {

    private ViewPager2 viewPager; // Store ViewPager reference
    private EditText editAddress;
    private FusedLocationProviderClient fusedLocationClient;

    //function to request permission for location services
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchLocationAndPopulateAddress();
                } else {
                    Toast.makeText(getContext(), "Location permission denied. Enter your address manually.", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_submit_tab2, container, false);
        editAddress = root.findViewById(R.id.edit_address);
        // Find ViewPager2 from parent activity
        viewPager = requireActivity().findViewById(R.id.viewPager);
        Button nextButton = root.findViewById(R.id.buttonNext);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        //try to request location permission on view creation
        requestLocationPermission();

        nextButton.setOnClickListener(v -> {
            String address = editAddress.getText().toString().trim();

            if (!address.isEmpty()) {
                // Save the address directly without validation
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lead_address", address);
                editor.apply();

                // Navigate to next fragment
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            } else {
                Toast.makeText(getContext(), "Please enter an address.", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndPopulateAddress();
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    //includes explicit permission check conditional wrapper
    private void fetchLocationAndPopulateAddress() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            reverseGeocodeLocation(location);
                        } else {
                            requestSingleLocationUpdate();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Unable to get location.", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }


    private void requestSingleLocationUpdate() {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMaxUpdates(1)
                .build();

        //explicit permission request from user per Androids newer strict requirements
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        fusedLocationClient.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                fusedLocationClient.removeLocationUpdates(this);  // Stop updates
                android.location.Location location = locationResult.getLastLocation();
                if (location != null) {
                    reverseGeocodeLocation(location);
                }
            }
        }, requireActivity().getMainLooper());
    } else {
            Toast.makeText(getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void reverseGeocodeLocation(Location location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.US);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            assert addresses != null;
            if (!addresses.isEmpty()) {
                Address addr = addresses.get(0);
                String fullAddress = addr.getAddressLine(0);
                editAddress.setText(fullAddress);
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to resolve address.", Toast.LENGTH_SHORT).show();
        }
    }
    //verifyAddressWithAPI is called after address is found via reverse geocoding.
    private void verifyAddressWithAPI(String address, boolean navigateNext) {
        //Step 1: Clean and log the address before calling the API
        address = address.replaceAll("\\n", " ").trim();
        //debug
        Log.d("GeocodioDebug", "Address submitted to API: " + address);

        String apiKey = BuildConfig.GEOCODIO_API_KEY;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.geocod.io/v1.7/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeocodioService service = retrofit.create(GeocodioService.class);

        Call<GeocodioResponse> call = service.validateAddress(address, apiKey);

        call.enqueue(new Callback<GeocodioResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeocodioResponse> call, Response<GeocodioResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().results.isEmpty()) {
                    String validatedAddress = response.body().results.get(0).formatted_address;

                    // Update the EditText with the cleaned-up, verified address
                    editAddress.setText(validatedAddress);

                    // Save the validated address to SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lead_address", validatedAddress);
                    editor.apply();

                    Toast.makeText(getContext(), "Validated: " + validatedAddress, Toast.LENGTH_LONG).show();

                    // Only navigate if requested
                    if (navigateNext && viewPager != null) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid address or not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodioResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to verify address.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

