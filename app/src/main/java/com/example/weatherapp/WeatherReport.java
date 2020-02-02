package com.example.weatherapp;


import android.os.Parcel;
import android.os.Parcelable;

public class WeatherReport {
    private Integer temperature;
    private String description;
    private String humidity;
    private Integer windSpeed;
    private String city;

    public WeatherReport(Integer temperature, String description, String humidity, Integer windSpeed, String city) {
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.city=city;
    }

    public WeatherReport(Parcel in) {
        this.temperature = in.readInt();
        this.description = in.readString();
        this.humidity = in.readString();
        this.windSpeed = in.readInt();
        this.city=in.readString();
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public Integer getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Integer windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString(){
        return this.city+"\n"+ this.temperature.toString()+" ºC";
    }

}

