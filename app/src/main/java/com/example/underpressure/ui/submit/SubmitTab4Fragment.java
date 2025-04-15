package com.example.underpressure.ui.submit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.underpressure.R;
import com.example.underpressure.ui.myleads.MyLeadsFragment;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SubmitTab4Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_submit_tab4, container, false);

        //Step 1: Reference all UI elements
        TextView jobDetailsText = root.findViewById(R.id.textView_job_details);
        TextView addressText = root.findViewById(R.id.textView_address);
        TextView emailText = root.findViewById(R.id.textView_email);
        Button submitButton = root.findViewById(R.id.button_submit);
        ImageView imagePreview = root.findViewById(R.id.image_preview);

        //Step 2: Pull stored data from SharedPreferences
        SharedPreferences sharedPrefs = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
        String jobDetails = sharedPrefs.getString("lead_service", "Not specified");
        String address = sharedPrefs.getString("lead_address", "Not provided");
        String email = sharedPrefs.getString("lead_email", "Not logged in");

        //getting image data from sharedPref
        String photoPath = sharedPrefs.getString("lead_photos", null);
        if (photoPath != null) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imagePreview.setImageBitmap(bitmap);
            }
        }

        //formatting's sake
        jobDetails = formatJobDetails(jobDetails);

        //Step 3: Populate the text fields for user confirmation
        jobDetailsText.setText(jobDetails);
        addressText.setText(address);
        emailText.setText(email);

        final String finalJobDetails = jobDetails;
        final String finalAddress = address;
        final String finalEmail = email;


        //debugging for getting jobDetails to show in frag4:
        //Log.d("JobDetailsDebug", "Retrieved job details: " + jobDetails);


        //Step 4: Handle submission
        submitButton.setOnClickListener(v -> {
            saveLeadData(finalJobDetails, finalAddress, finalEmail);  //saves current values
            clearJobDetails(); //clears checkboxes and radio buttons after submission
            Toast.makeText(getContext(), "Thanks for submitting a lead!", Toast.LENGTH_LONG).show();
            navigateToMyLeads();
        });

        return root;
    }



    private String formatJobDetails(String details) {
        if (details == null || details.isEmpty() || details.equals("Not specified")) {
            return "Not specified";
        }
        // Convert commas into bullet points, starting with a newline and bullet
        return "• " + details.replace(", ", "\n• ");
    }

    //helper method to clear the job details data - this is called after submission so checkboxes aren't pre-checked in new subimssion
    private void clearJobDetails() {
        SharedPreferences sharedPrefs = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove("pressure_washing_selected");
        editor.remove("exterior_paint_selected");
        editor.remove("house_size");
        editor.remove("lead_service");
        editor.apply();
    }

    //using shared preferences -userPrefs to save lead data and associate with user
    private void saveLeadData(String job, String address, String email) {
        // Retrieve the current user's email from the login SharedPreferences (assumed stored in "UserPrefs")
        SharedPreferences userPrefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserEmail = userPrefs.getString("email", "defaultUser");

        // Construct a key specific to this user
        String leadKey = "all_leads_" + currentUserEmail;

        SharedPreferences sp = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // Get current timestamp for the lead submission
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        // Create a unique ID for this lead
        String leadId = UUID.randomUUID().toString();

        // Retrieve the photo path saved in SubmitTab3Fragment
        String photoPath = sp.getString("lead_photos", "No Photo Taken");

        // Build a formatted record for this lead (line breaks added for clarity)
        String newLead = "ID: " + leadId + "\n" +
                "Time: " + currentTime + "\n" +
                "Job Details: " + job + "\n" +
                "Address: " + address + "\n" +
                "Submitted by: " + email + "\n" +
                "Photos: " + photoPath + "\n";

        // Get any existing leads for this user and append the new one with a delimiter.
        String existingLeads = sp.getString(leadKey, "");
        existingLeads = existingLeads + "\n" + newLead + "##END##\n";
        editor.putString(leadKey, existingLeads);

        // Also clear out the temporary photo path for next submission
        editor.remove("lead_photos");
        editor.apply();
    }

    private void navigateToMyLeads() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MyLeadsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}