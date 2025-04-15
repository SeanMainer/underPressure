package com.example.underpressure.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.underpressure.R;
import com.example.underpressure.databinding.FragmentHomeBinding;
import com.example.underpressure.ui.myleads.MyLeadsFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI elements
        editTextEmail = binding.editTextEmail;
        editTextPassword = binding.editTextPassword;
        buttonLogin = binding.buttonLogin;
        buttonRegister = binding.buttonRegister;

        // SharedPreferences for storing user data
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Login button click event
        buttonLogin.setOnClickListener(v -> loginUser());

        // Register button click event
        buttonRegister.setOnClickListener(v -> registerUser());

        return root;
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save user credentials in the "UserPrefs" SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();

        // Also save the email in the "UserLeads" SharedPreferences for later use in SubmitTab4Fragment
        SharedPreferences leadsPrefs = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
        SharedPreferences.Editor leadsEditor = leadsPrefs.edit();
        leadsEditor.putString("lead_email", email);
        leadsEditor.apply();

        Toast.makeText(getContext(), "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
    }

    //a function for logging in, which saves the logged in email to "UserLeads" SharedPreferences
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //get stored credentials from "UserPrefs"
        String registeredEmail = sharedPreferences.getString("email", null);
        String registeredPassword = sharedPreferences.getString("password", null);

        if (email.equals(registeredEmail) && password.equals(registeredPassword)) {
            // Save the logged in email to "UserLeads" SharedPreferences
            SharedPreferences leadsPrefs = requireActivity().getSharedPreferences("UserLeads", Context.MODE_PRIVATE);
            SharedPreferences.Editor leadsEditor = leadsPrefs.edit();
            leadsEditor.putString("lead_email", email);  //expected by SubmitTab4Fragment
            leadsEditor.apply();

            Toast.makeText(getContext(), "Login successful. Ready to earn? Submit a new lead by clicking 'Submit' in the bottom right corner, and we'll walk you through the step-by-step process!", Toast.LENGTH_SHORT).show();
            navigateToMyLeads();
        } else {
            Toast.makeText(getContext(), "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    private void navigateToMyLeads() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MyLeadsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
