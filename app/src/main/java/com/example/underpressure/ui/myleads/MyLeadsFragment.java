package com.example.underpressure.ui.myleads;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.underpressure.R;
import com.example.underpressure.databinding.FragmentMyLeadsBinding;
import java.io.File;

public class MyLeadsFragment extends Fragment {

    private FragmentMyLeadsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyLeadsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the current user's email from UserPrefs:
        SharedPreferences userPrefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserEmail = userPrefs.getString("email", "defaultUser");

        // Build the user-specific key
        String leadKey = "all_leads_" + currentUserEmail;

        LinearLayout leadsContainer = binding.leadsContainer; // defined in fragment_my_leads.xml

        // Load the leads string for this user from UserLeads SharedPreferences
        SharedPreferences sp = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
        String allLeads = sp.getString(leadKey, "");

        if (TextUtils.isEmpty(allLeads.trim())) {
            TextView noLeadsText = new TextView(getContext());
            noLeadsText.setText("No leads submitted yet.");
            noLeadsText.setTextColor(getResources().getColor(android.R.color.white));
            leadsContainer.addView(noLeadsText);
            return root;
        }

        // Split the leads string using the delimiter we added ("##END##")
        String[] leadEntries = allLeads.split("##END##");
        for (String leadBlock : leadEntries) {
            leadBlock = leadBlock.trim();
            if (leadBlock.isEmpty()) continue;

            // Inflate a card (item_lead.xml) for this lead
            View cardView = inflater.inflate(R.layout.item_lead, leadsContainer, false);
            TextView leadInfoText = cardView.findViewById(R.id.leadInfoText);
            ImageView leadPhoto = cardView.findViewById(R.id.leadPhoto);

            // Set the lead info text (includes ID, Time, Job Details, Address, etc.)
            leadInfoText.setText(leadBlock);

            // Extract the photo path from the leadBlock (line beginning with "Photos: ")
            String photoPath = null;
            for (String line : leadBlock.split("\n")) {
                if (line.startsWith("Photos: ")) {
                    photoPath = line.substring("Photos: ".length()).trim();
                    break;
                }
            }

            // If a valid photo path exists and the file is present, display the image
            if (photoPath != null && !photoPath.equals("No Photo Taken")) {
                File imgFile = new File(photoPath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    leadPhoto.setImageBitmap(bitmap);
                } else {
                    leadPhoto.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                // If there is no photo, show a placeholder
                leadPhoto.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // Add the card to the container
            leadsContainer.addView(cardView);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
