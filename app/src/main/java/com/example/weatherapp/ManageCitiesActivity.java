package com.example.weatherapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ManageCitiesActivity extends AppCompatActivity {

    private ArrayList<WeatherReport> citiesList =new ArrayList<>();
    ArrayAdapter adapter;
    ListView listView;
    WeatherReport selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cities);

        setTitle(R.string.action_manage_cities);

        listView = findViewById(R.id.citiesListView);

        Intent intent = getIntent();

        String city = intent.getStringExtra("currentCityName");
        String humidity = intent.getStringExtra("currentCityHumidity");
        String description= intent.getStringExtra("currentCityHumidity") ;
        Integer temperature = intent.getIntExtra("currentCityTemperature",0);
        Integer windSpeed = intent.getIntExtra("currentCityWindSpeed", 0);

        WeatherReport currentCity = new WeatherReport(temperature, description, humidity, windSpeed, city);

        loadData();
        if (citiesList.isEmpty()) {
            addCityToList(currentCity);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                selectedCity = (WeatherReport)listView.getItemAtPosition(position);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("city", selectedCity.getCity());

                setResult(RESULT_OK, resultIntent);
                saveData();
                finish();
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = (WeatherReport)listView.getItemAtPosition(position);
                showDialog();
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.addCityButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });

    }

    public void addCityToList(WeatherReport report){
        if (!citiesList.contains(report)) {
            citiesList.add(report);
        }
    }

    public void removeCityFromList (){
        citiesList.remove(selectedCity);
        adapter.notifyDataSetChanged();
    }

    public void openSearchActivity(){
        Intent intent = new Intent(this, SearchCityActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1 && resultCode==RESULT_OK){
            String city = data.getStringExtra("searchedCityName");
            String humidity = data.getStringExtra("searchedCityHumidity");
            String description= data.getStringExtra("searchedCityHumidity") ;
            Integer temperature = data.getIntExtra("searchedCityTemperature",0);
            Integer windSpeed = data.getIntExtra("searchedCityWindSpeed", 0);

            WeatherReport searchedCity = new WeatherReport(temperature, description, humidity, windSpeed, city);
            addCityToList(searchedCity);

            adapter.notifyDataSetChanged();

        }

    }

    public void showDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Remove city")
                .setMessage("Add city to list?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeCityFromList();
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

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(citiesList);
        editor.putString("citiesList",json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences preferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("citiesList", null);
        Type type = new TypeToken<ArrayList<WeatherReport>>(){}.getType();
        citiesList=gson.fromJson(json, type);

        if (citiesList == null) {
            citiesList = new ArrayList<>();
        }
    }


}
