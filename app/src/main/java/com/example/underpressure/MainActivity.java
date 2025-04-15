package com.example.underpressure;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.underpressure.databinding.ActivityMainBinding;
import com.example.underpressure.ui.home.HomeFragment;
import com.example.underpressure.ui.myleads.MyLeadsFragment;
import com.example.underpressure.ui.submit.SubmitFragment;
import com.example.underpressure.R;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // manually calling the click listener to select HomeFragment on app start
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // bottom nav click listeners to switch b/t tabs
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_my_leads) {
                selectedFragment = new MyLeadsFragment();
            } else if (item.getItemId() == R.id.nav_submit) {
                selectedFragment = new SubmitFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // make sure HomeFragment loads - manually triggering click listener
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
            binding.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_home, 0);
        }


    }}
