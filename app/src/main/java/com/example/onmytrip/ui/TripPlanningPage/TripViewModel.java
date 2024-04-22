package com.example.onmytrip.ui.TripPlanningPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onmytrip.Object.qrcode;

public class TripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final qrcode qr;
    private String origin;
    private String destination;

    public TripViewModel() {
        mText = new MutableLiveData<>();
        qr = new qrcode();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public qrcode getQr(){
        return qr;
    }

    public void setTripAddress(String origin, String destination){
        this.origin = origin;
        this.destination = destination;
    }

    public String getOrigin(){
        return origin;
    }

    public String getDestination(){
        return destination;
    }

}