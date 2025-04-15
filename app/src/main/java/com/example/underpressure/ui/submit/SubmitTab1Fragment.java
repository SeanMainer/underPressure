package com.example.underpressure.ui.submit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.underpressure.R;

//Job Details Tab - setup to allow for selection + saving job details via userPref

public class SubmitTab1Fragment extends Fragment {

    private ViewPager2 viewPager; // Store ViewPager reference
    private CheckBox checkPressureWashing;
    private CheckBox checkExteriorPaint;
    private RadioGroup radioGroupSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_submit_tab1, container, false);

        //find ViewPager2 from parent activity
        viewPager = requireActivity().findViewById(R.id.viewPager);

        //ui elements
        checkPressureWashing = root.findViewById(R.id.check_pressure_washing);
        checkExteriorPaint = root.findViewById(R.id.check_exterior_paint);
        radioGroupSize = root.findViewById(R.id.radio_group_size);

        Button nextButton = root.findViewById(R.id.buttonNext);

        //load previously saved selections
        loadSavedSelections();

        nextButton.setOnClickListener(v -> {
            //saving selections needs to happen before next
            saveSelections();
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true); // Move to next tab smoothly
            }
        });

        return root;
    }

    // This (locally) saves the selections to UserLeads SharedPreferences
    private void saveSelections() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save checkbox selections
        boolean isPressureWashingSelected = checkPressureWashing.isChecked();
        boolean isExteriorPaintSelected = checkExteriorPaint.isChecked();
        editor.putBoolean("pressure_washing_selected", isPressureWashingSelected);
        editor.putBoolean("exterior_paint_selected", isExteriorPaintSelected);

        // Save radio button selection
        int selectedRadioId = radioGroupSize.getCheckedRadioButtonId();
        String houseSizeText = "";

        if (selectedRadioId == R.id.size_less_1000) {
            houseSizeText = "Less than 1000 sq ft";
        } else if (selectedRadioId == R.id.size_1000_2000) {
            houseSizeText = "1001-2000 sq ft";
        } else if (selectedRadioId == R.id.size_2000_3500) {
            houseSizeText = "2001-3500 sq ft";
        } else if (selectedRadioId == R.id.size_3500_plus) {
            houseSizeText = "3500+ sq ft";
        }

        editor.putString("house_size", houseSizeText);

        // Generate a consolidated job details string - will be used to review lead info
        StringBuilder jobDetails = new StringBuilder();

        if (isPressureWashingSelected) {
            jobDetails.append("Pressure Washing");
        }

        if (isExteriorPaintSelected) {
            if (jobDetails.length() > 0) {
                jobDetails.append(", ");
            }
            jobDetails.append("Exterior Paint");
        }

        if (!houseSizeText.isEmpty()) {
            if (jobDetails.length() > 0) {
                jobDetails.append(", ");
            }
            jobDetails.append(houseSizeText);
        }

        // Save the consolidated job details
        editor.putString("lead_service", jobDetails.toString());

        //debugging help! trying to get job details selected/ saved for display in frag4
        // After creating the jobDetails StringBuilder
        editor.putString("lead_service", jobDetails.toString());

        // Log what we're saving
        //Log.d("JobDetailsDebug", "Saving job details: " + jobDetails.toString());

        // Apply changes
        editor.apply();
    }

    //logic to load previously saved selections
    private void loadSavedSelections() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);

        //get saved selections for pressure washing or painting
        checkPressureWashing.setChecked(sharedPreferences.getBoolean("pressure_washing_selected", false));
        checkExteriorPaint.setChecked(sharedPreferences.getBoolean("exterior_paint_selected", false));

        //get saved selection for house size
        String houseSize = sharedPreferences.getString("house_size", "");

        if (houseSize.equals("Less than 1000 sq ft")) {
            radioGroupSize.check(R.id.size_less_1000);
        } else if (houseSize.equals("1001-2000 sq ft")) {
            radioGroupSize.check(R.id.size_1000_2000);
        } else if (houseSize.equals("2001-3500 sq ft")) {
            radioGroupSize.check(R.id.size_2000_3500);
        } else if (houseSize.equals("3500+ sq ft")) {
            radioGroupSize.check(R.id.size_3500_plus);
        }

    }
}

