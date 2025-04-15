package com.example.underpressure.ui.submit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.underpressure.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;


public class SubmitTab3Fragment extends Fragment {

    private ViewPager2 viewPager; // Store ViewPager reference
    private Button buttonUploadPhoto;
    private ImageView imagePreview;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_submit_tab3, container, false);

        // Find ViewPager2 from parent activity
        viewPager = requireActivity().findViewById(R.id.viewPager);
        // UI elements for camera functionality
        buttonUploadPhoto = root.findViewById(R.id.button_upload_photos);
        imagePreview = root.findViewById(R.id.image_preview);

        // Initialize the launcher to request CAMERA permission
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Initialize the launcher to capture an image using the camera Intent
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.containsKey("data")) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                // Display the captured photo in the ImageView
                                imagePreview.setImageBitmap(imageBitmap);
                                // Save the captured photo locally and store its file path in SharedPreferences
                                savePhoto(imageBitmap);
                            }
                        } else {
                            Toast.makeText(getContext(), "No image data captured", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No image data returned", Toast.LENGTH_SHORT).show(); //add'l error check
                    }
                }
        );

        // Set up the camera upload button click event
        buttonUploadPhoto.setOnClickListener(v -> {
            // Check for camera permission before launching camera
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Next button to move to the review fragment
        Button nextButton = root.findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });

        return root;
    }

    // Method to launch the camera intent
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save the captured photo to internal storage aand assign name using time date info
    private void savePhoto(Bitmap bitmap) {
        // Generate a unique filename for this capture
        String filename = "captured_photo_" + System.currentTimeMillis() + ".jpg";  // CHANGED
        File file = new File(requireActivity().getFilesDir(), filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Compress and write the bitmap to the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            // Save the captured photo’s file path in SharedPreferences under "lead_photos"
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lead_photos", file.getAbsolutePath());
            editor.apply();

            //Log.d("PhotoDebug", "Photo saved at: " + file.getAbsolutePath());
            Toast.makeText(getContext(), "Photo saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save photo", Toast.LENGTH_SHORT).show();
        }
    }
}