package com.example.underpressure.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment. Here a user will login, register. This is the default screen when launching the app.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}