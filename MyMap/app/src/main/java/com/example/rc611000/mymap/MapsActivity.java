package com.example.rc611000.mymap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter/*, GoogleMap.OnMarkerClickListener*/ {

    LayoutInflater inflater = null;

    //Variables
    private GoogleMap mMap; //Variable que guarda nuestro mapa
    private Marker marker; //Variable que guarda el marcador

    //Funcion que ejecuta el mapa cuando la aplicacion se abre
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.inflater = getLayoutInflater();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //Se ejecuta cuando el mapa esta listo
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //Cargamos el mapa
        miUbicacion(); //Cargamos nuestra ubicacion
        mMap.setInfoWindowAdapter(this);

        //START INTENT
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Meteo meteo = (Meteo) marker.getTag();
                Intent secondActivity = new Intent(MapsActivity.this,segAct.class);
                secondActivity.putExtra("name",meteo.getName());
                secondActivity.putExtra("temp",meteo.getMain().getTemp());
                secondActivity.putExtra("tempMax",meteo.getMain().getTemp_max());
                secondActivity.putExtra("tempMin",meteo.getMain().getTemp_min());
                secondActivity.putExtra("icon",meteo.getWeather().get(0).getIcon());
                startActivity(secondActivity);
            }
        });
    }

    //Clase que agrega el marcador
    private void agregarMarcador(double lat, double lon, Meteo maMeteo) {
        if(mMap != null){
            if(marker != null){
                marker.setPosition(new LatLng(lat, lon));
                marker.setTag(maMeteo);
            }else{
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)));
                marker.setTag(maMeteo);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
        }
    }

    //Clase que busca nuestra posicion
    public void miUbicacion() { //Funcion que recupera la ubicacion

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //Se accede al sistema para el servicio de locatizacion
        Location location = locationManager.getLastKnownLocation(GPS_PROVIDER); //Se recupera la ultima localizacion conocida
        if(location  != null) {
            new DownloadWebPageTask(location).execute("http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() +
                    "&units=metric&lang=fr&appid=5bdfb081811a28abc515bc673fc0d20f");
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000,0,locListenner); //Esto se hace cada 10s
    }

    LocationListener locListenner = new LocationListener() { //Se crea una variable que escucha nuestra ubicacion cuando:
        @Override
        public void onLocationChanged(Location location) {
            //Aqui creamos la funcion DownloadWebPageTask para poder descargar los archivos desde la pagina del api
            DownloadWebPageTask task = new DownloadWebPageTask(location);
            task.execute(new String[] { "http://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+
                    "&lon="+location.getLongitude()+"&units=metric&lang=fr&appid=5bdfb081811a28abc515bc673fc0d20f" });
            Log.d("DEBUGGGGGGGGGGGGGGGGGG", String.valueOf(location.getLatitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //RECUPERACION INFO WINDOW
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    //DIBUJO DE LA PANTALLA
    @Override
    public View getInfoContents(Marker marker) {

        //PARAMS GENERALES
        int size = 18;

        //DIBUJAMOS dentro de los elementos que tenemos
        View popupView = inflater.inflate(R.layout.popupmap, null);
        Meteo meteo = (Meteo) marker.getTag();
        TextView ttvTitle = (TextView) popupView.findViewById(R.id.title);
        TextView txtDesc = (TextView) popupView.findViewById(R.id.desc);
        TextView txtTemp = (TextView) popupView.findViewById(R.id.temp);
        TextView txtTempMin = (TextView) popupView.findViewById(R.id.tempMin);
        TextView txtTempMax = (TextView) popupView.findViewById(R.id.tempMax);
        ImageView imageView = (ImageView) popupView.findViewById(R.id.imageView);
        TextView txtWind = (TextView) popupView.findViewById(R.id.wind);

        //TITLE and PARAMS
        ttvTitle.setText(meteo.getName());
        ttvTitle.setTextSize(25);
        ttvTitle.setTextColor(Color.WHITE);
        ttvTitle.setBackgroundColor(Color.BLUE);

        //IMAGE
        imageView.setImageDrawable(getDrawable(getResources().getIdentifier("_" + meteo.getWeather().get(0).getIcon(), "drawable", getPackageName())));

        //TEXT VIEWS
        txtDesc.setText("Description: " + meteo.getWeather().get(0).getDescription());
        txtTemp.setText("Température: " + meteo.getMain().getTemp() + "°C");
        txtTempMax.setText("MAX: " + meteo.getMain().getTemp_max() + "°C");
        txtTempMin.setText("MIN: " + meteo.getMain().getTemp_min() + "°C");
        txtWind.setText("Force du vent: " + meteo.getWind().getSpeed() + " Deg: " + meteo.getWind().getDeg());

        //PARAMS TEXT VIEWS
        txtDesc.setTextSize(size);
        txtTemp.setTextSize(size);
        txtTempMax.setTextSize(size);
        txtTempMin.setTextSize(size);
        txtWind.setTextSize(size);

        return popupView ;
    }

    //Creamos la clase que recupera los datos de la pag web
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        Location location;
        public DownloadWebPageTask(Location location){
            this.location = location;
        }

        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            OkHttpClient client = new OkHttpClient();
            Request request =
                    new Request.Builder()
                            .url(urls[0])
                            .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download failed";
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("DEBUGGGGGGGGGGGGGGGGGG", result);
            //Creamos la estancia que utiliza el gson
            Gson gson = new GsonBuilder().create();
            //Entramos
            Meteo maMeteo = gson.fromJson(result, Meteo.class);
            Log.d("DEBUGGGGGGGGGGGGGGGGGG", maMeteo.getName());

            //Create marekr from meteo Object ...
            agregarMarcador(maMeteo.getCoord().getLat(),maMeteo.getCoord().getLon(), maMeteo);

        }
    }
}

