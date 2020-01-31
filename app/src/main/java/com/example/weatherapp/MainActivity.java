package com.example.weatherapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    WeatherReport currentCity = null;
    ArrayList<WeatherReport> citiesList = new ArrayList<>();
    LocationManager locationManager;
    LocationListener locationListener;
    String URLbase = "http://api.openweathermap.org/data/2.5/weather?";
    String appID = "&appid=4d26ff9720cf1c7f6334eb77b08c7bd8";
    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ObtainGPSLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates("gps", 9000000, 0, locationListener);
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_manage_cities) {
            onManageCitiesClick();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onManageCitiesClick(){
        //new activity
    }

    private void obtainDataFromAPI(){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URLbase+"lat="+latitude.toString()+"&lon="+longitude.toString()+appID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String weatherDescription=response.getJSONArray("weather").getJSONObject(0).getString("main");
                            String weatherCity = response.getString("name");
                            String weatherHumidity=response.getJSONObject("main").getString("humidity");
                            Double temp=response.getJSONObject("main").getDouble("temp");
                            Double calculatedTemperature= temp-273.15;
                            Integer weatherTemperature=calculatedTemperature.intValue();
                            Double speed=response.getJSONObject("wind").getDouble("speed");
                            Double calculatedWindSpeed=speed*3.6;
                            Integer weatherWindSpeed=calculatedWindSpeed.intValue();

                            currentCity=new WeatherReport(weatherTemperature,weatherDescription,weatherHumidity,weatherWindSpeed, weatherCity);

                            TextView temperatureText = findViewById(R.id.temperatureText);
                            TextView cityText = findViewById(R.id.cityText);
                            TextView descriptionText = findViewById(R.id.descriptionText);
                            TextView humidityText = findViewById(R.id.humidityText);
                            TextView windSpeedText = findViewById(R.id.windSpeedText);

                            temperatureText.setText(currentCity.getTemperature().toString()+" ºC");
                            cityText.setText(currentCity.getCity());
                            descriptionText.setText(currentCity.getDescription());
                            humidityText.setText(currentCity.getHumidity().toString()+" %");
                            windSpeedText.setText(currentCity.getWindSpeed().toString()+" km/h");
                            

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView textView=findViewById(R.id.ErrorText);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void ObtainGPSLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();

                obtainDataFromAPI();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }
        else {
            locationManager.requestLocationUpdates("gps", 9000000, 0, locationListener);
        }
    }
}
