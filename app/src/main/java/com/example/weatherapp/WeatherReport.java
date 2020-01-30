package com.example.weatherapp;

public class WeatherReport {
    private String temperature;
    private String description;
    private int humidity;
    private int precision;

    public WeatherReport(String temperature, String description, int humidity, int precision) {
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.precision = precision;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
