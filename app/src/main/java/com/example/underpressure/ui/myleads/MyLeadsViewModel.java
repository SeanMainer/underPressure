package com.example.underpressure.ui.myleads;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyLeadsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyLeadsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the My Leads page. This is where a user will be able to view their previously submitted leads.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}