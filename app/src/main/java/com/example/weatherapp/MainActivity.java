package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    WeatherReport currentCity=null;
    ArrayList<WeatherReport> citiesList = new ArrayList<>();
    JSONObject responseJSON=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "http://api.openweathermap.org/data/2.5/weather?lat=39.7436200&lon=-8.8070500&appid=4d26ff9720cf1c7f6334eb77b08c7bd8",
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
                            System.out.println(currentCity.toString());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_manage_cities) {
            onManageCitiesClick();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onManageCitiesClick(){
        //new activity
    }
}
