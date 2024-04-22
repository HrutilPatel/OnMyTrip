package com.example.onmytrip.ui.StopsPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onmytrip.Object.qrcode;

public class StopsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StopsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}