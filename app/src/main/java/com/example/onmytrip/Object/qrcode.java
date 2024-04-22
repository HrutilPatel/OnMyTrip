package com.example.onmytrip.Object;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class qrcode{

    private final static int QRCODE_WIDTH = 300;
    private final static int QRCODE_HEIGHT = 300;
    private static String data;

    // Function to generate QR code from input text
    public static Bitmap generateQRCode() {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            BitMatrix bitMatrix = barcodeEncoder.encode(data, BarcodeFormat.QR_CODE, QRCODE_WIDTH, QRCODE_HEIGHT);
            return toBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert BitMatrix to Bitmap
    private static Bitmap toBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    public void setData(String data){
        this.data = data;
    }
}
