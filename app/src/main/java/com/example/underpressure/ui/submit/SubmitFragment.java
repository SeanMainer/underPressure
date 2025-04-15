package com.example.underpressure.ui.submit;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.underpressure.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.underpressure.databinding.FragmentSubmitBinding;

//SubmitFragment contains the UI for the submit functionality, with fragment_submit.xml as the layout.
//Sets up ViewPager2 and TabLayout (via TabLayoutMediator) for the submit process.
//This makes the UI intuitive with a L-->R swipe (or next button click).

public class SubmitFragment extends Fragment {

    private FragmentSubmitBinding binding;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubmitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // init ViewPager2 and TabLayout
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        // setup ViewPager adapter
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new SubmitTab1Fragment();
                    case 1: return new SubmitTab2Fragment();
                    case 2: return new SubmitTab3Fragment();
                    case 3: return new SubmitTab4Fragment();
                    default: return new SubmitTab1Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 4; // Number of tabs
            }
        });

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Job Details"); break;
                case 1: tab.setText("Address"); break;
                case 2: tab.setText("Photos"); break;
                case 3: tab.setText("Review & Submit"); break;
            }
        }).attach();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
