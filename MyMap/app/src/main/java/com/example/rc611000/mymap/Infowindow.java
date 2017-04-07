package com.example.rc611000.mymap;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by rc611000 on 01/04/2017.
 */

public class Infowindow implements GoogleMap.InfoWindowAdapter{

    LayoutInflater inflater = null;
    private TextView textViewTitle;

    public Infowindow(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override //ME QUEDE EN CREAR EL INFOWINDOW https://www.hrupin.com/2013/05/how-to-create-custom-background-for-infowindow-on-google-maps-v2-for-android
    public View getInfoWindow(Marker marker) {
        View v = inflater.inflate(R.layout.InfoWindow, null);
        if (marker != null) {
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            textViewTitle.setText(marker.getTitle());
        }
        return (v);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
