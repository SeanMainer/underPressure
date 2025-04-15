package com.example.underpressure.ui.submit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubmitViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SubmitViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the Submit a Lead page. This is where a user will be guided step-by-step to submit a new lead.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}