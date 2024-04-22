package com.example.onmytrip.ui.QRCodePage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.onmytrip.R;
import com.example.onmytrip.ui.TripPlanningPage.TripViewModel;

public class qrCodeFragment extends Fragment {

    private ImageView imageView;
    private TextView textView;
    private TripViewModel tripViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        imageView = view.findViewById(R.id.imageView2);
        textView = view.findViewById(R.id.textView2);

        // Get TripViewModel instance
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        // Generate QR code using qrcode instance from TripViewModel
        Bitmap qrBitmap = tripViewModel.getQr().generateQRCode();
        imageView.setImageBitmap(qrBitmap);

        textView.setText("This is a QR code fragment for your trip\n" + tripViewModel.getOrigin() + " to " + tripViewModel.getDestination() + " only.");

        return view;
    }
}
