package com.example.weatherapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class ManageCitiesActivity extends AppCompatActivity {

    private ArrayList<WeatherReport> citiesList = new ArrayList<>();
    ArrayAdapter adapter;
    ListView listView;


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
        addCityToList(currentCity);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                WeatherReport selectedCity = (WeatherReport)listView.getItemAtPosition(position);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedCityName", selectedCity.getCity());
                resultIntent.putExtra("selectedCityDescription", selectedCity.getDescription());
                resultIntent.putExtra("selectedCityHumidity", selectedCity.getHumidity());
                resultIntent.putExtra("selectedCityWindSpeed", selectedCity.getWindSpeed());
                resultIntent.putExtra("selectedCityTemperature", selectedCity.getTemperature());

                setResult(RESULT_OK, resultIntent);
                finish();

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

    public void removeCityFromList (WeatherReport report){
        if (citiesList.contains(report)) {
            citiesList.remove(report);
        }
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
            //listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

    }


}
