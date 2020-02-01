package com.example.weatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchCityActivity extends AppCompatActivity {

    private EditText searchText;
    private WeatherReport searchedCity;
    private final String URLbase = "http://api.openweathermap.org/data/2.5/weather?";
    private final String appID = "&appid=4d26ff9720cf1c7f6334eb77b08c7bd8";
    JsonObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        setTitle(R.string.search_city);

        TextView suggestedCity1 = findViewById(R.id.suggestedCity1);
        TextView suggestedCity2 = findViewById(R.id.suggestedCity2);
        TextView suggestedCity3 = findViewById(R.id.suggestedCity3);

        Button btnSearch = findViewById(R.id.btnSearch);
        searchText = findViewById(R.id.searchCity);

        suggestedCity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textClick(getString(R.string.suggested_city_1));
            }
        });

        suggestedCity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textClick(getString(R.string.suggested_city_2));
            }
        });

        suggestedCity3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textClick(getString(R.string.suggested_city_3));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainDataFromAPI();
            }
        });
    }

    public void textClick (String text){
        searchText.setText(text);
    }


    private void obtainDataFromAPI(){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URLbase+"q="+searchText.getText().toString()+appID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String weatherCity = response.getString("name");
                            String weatherDescription=response.getJSONArray("weather").getJSONObject(0).getString("main");
                            String weatherHumidity=response.getJSONObject("main").getString("humidity");
                            Double temp=response.getJSONObject("main").getDouble("temp");
                            Double calculatedTemperature= temp-273.15;
                            Integer weatherTemperature=calculatedTemperature.intValue();
                            Double speed=response.getJSONObject("wind").getDouble("speed");
                            Double calculatedWindSpeed=speed*3.6;
                            Integer weatherWindSpeed=calculatedWindSpeed.intValue();

                            searchedCity=new WeatherReport(weatherTemperature,weatherDescription,weatherHumidity,weatherWindSpeed, weatherCity);
                            showDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Inform the user: it was impossible to retrieve the data
                        TextView textView=findViewById(R.id.ErrorText);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void showDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Add city")
                .setMessage("Add city to list?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToSearchResults();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"Operation canceled",Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    public void goToSearchResults(){
        Intent intent = new Intent();
        intent.putExtra("searchedCityName", searchedCity.getCity());
        intent.putExtra("searchedCityDescription", searchedCity.getDescription());
        intent.putExtra("searchedCityHumidity", searchedCity.getHumidity());
        intent.putExtra("searchedCityWindSpeed", searchedCity.getWindSpeed());
        intent.putExtra("searchedCityTemperature", searchedCity.getTemperature());

        setResult(RESULT_OK, intent);
        finish();
    }


}
