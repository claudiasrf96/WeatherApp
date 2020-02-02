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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private WeatherReport currentCity = null;
    private final String URLbase = "http://api.openweathermap.org/data/2.5/weather?";
    private final String appID = "&appid=4d26ff9720cf1c7f6334eb77b08c7bd8";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<WeatherReport> listOfCities;

    //Create view objects
    TextView temperatureText;
    TextView cityText;
    TextView descriptionText;
    TextView humidityText;
    TextView windSpeedText;
    ImageView weatherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        temperatureText = findViewById(R.id.temperatureText);
        cityText = findViewById(R.id.cityText);
        descriptionText = findViewById(R.id.descriptionText);
        humidityText = findViewById(R.id.humidityText);
        windSpeedText = findViewById(R.id.windSpeedText);
        weatherImage = findViewById(R.id.weatherImage);

        ObtainGPSLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.requestLocationUpdates("gps", 9000000, 0, locationListener);
                    } catch (SecurityException e) {
                        System.out.println(e.getMessage());
                    }
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
        Intent intent = new Intent(this, ManageCitiesActivity.class);
        intent.putExtra("currentCityName", currentCity.getCity());
        intent.putExtra("currentCityDescription", currentCity.getDescription());
        intent.putExtra("currentCityHumidity", currentCity.getHumidity());
        intent.putExtra("currentCityWindSpeed", currentCity.getWindSpeed());
        intent.putExtra("currentCityTemperature", currentCity.getTemperature());
        startActivityForResult(intent, 1);
    }

    private void obtainDataFromAPI(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
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

                            setUpLayout();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //clean User Interface
                        temperatureText.setVisibility(View.INVISIBLE);
                        cityText.setVisibility(View.INVISIBLE);
                        humidityText.setVisibility(View.INVISIBLE);
                        windSpeedText.setVisibility(View.INVISIBLE);
                        descriptionText.setVisibility(View.INVISIBLE);
                        weatherImage.setVisibility(View.INVISIBLE);

                        //Inform the user: it was impossible to retrieve the data
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
                Double latitude=location.getLatitude();
                Double longitude=location.getLongitude();

                obtainDataFromAPI(URLbase+"lat="+latitude.toString()+"&lon="+longitude.toString()+appID);
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

    public void setUpLayout(){
        temperatureText.setText(currentCity.getTemperature().toString()+" ºC");
        cityText.setText(currentCity.getCity());
        descriptionText.setText(currentCity.getDescription());
        humidityText.setText("Humidity: "+currentCity.getHumidity()+" %");
        windSpeedText.setText("Wind Speed: "+currentCity.getWindSpeed().toString()+" km/h");

        if (descriptionText.getText().toString().equals("Drizzle")){
            Picasso.get().load(R.drawable.rain).into(weatherImage);
        } else if (descriptionText.getText().toString().equals("Rain")) {
            Picasso.get().load(R.drawable.heavy_rain).into(weatherImage);
        } else if (descriptionText.getText().toString().equals("Clouds")) {
            Picasso.get().load(R.drawable.cloudy).into(weatherImage);
        } else if (descriptionText.getText().toString().equals("Clear")) {
            Picasso.get().load(R.drawable.clear).into(weatherImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1 && resultCode==RESULT_OK){
            String city = data.getStringExtra("city");
            obtainDataFromAPI(URLbase+"q="+city+appID);
        }

    }
}
