package com.example.snap_develop.activity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snap_develop.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressAcquisitionTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_acquisition_test);

        //double latitude = 33.583703;
        //double longitude = 130.4239146;

        double latitude = 35.4546424;
        double longitude = 133.8494460;
        LatLng a = new LatLng(latitude, longitude);

        StringBuffer strAddr = new StringBuffer();

        Context context = AddressAcquisitionTestActivity.this;

        try {


            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            //ここ
            for (Address addr : addresses) {
                int idx = addr.getMaxAddressLineIndex();
                for (int i = 0; i <= idx; i++) {
                    strAddr.append(addr.getAddressLine(i));
                    Log.v("addr", addr.getAddressLine(i));
                }
            }
            String newStr = strAddr.toString();
            String[] names = newStr.split(" ");
            TextView text = (TextView) findViewById(R.id.addressTextView);


            //names配列に住所が部分ごとに格納されている
            text.setText(names[2] + names[1] + names[0]);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
