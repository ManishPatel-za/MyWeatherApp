package com.pletely.insane.myweatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pletely.insane.myweatherapp.pojos.current.CurrentWeather;
import com.pletely.insane.myweatherapp.pojos.fiveday.FiveDayWeather;
import com.pletely.insane.myweatherapp.retrofit.RetrofitClientInstance;
import com.pletely.insane.myweatherapp.retrofit.WeatherService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String API_ID = "";

    //Default to Rosebank if no location is found
    //TODO 1. set th lats and longs
    private double latitude = -26.147133;
    private double longitude = 28.052434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }

            fetchWeatherData(String.valueOf(latitude), String.valueOf(longitude), "metric");
        }
    }

    private void fetchWeatherData(String latitude, String longitude, String metric) {

        WeatherService weatherService = RetrofitClientInstance.getRetrofitInstance().create(WeatherService.class);

        //Get current weather for the specified location
        Call<CurrentWeather> currentCall = weatherService.getCurrentWeather(latitude, longitude, metric, API_ID);
        currentCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {

                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                CurrentWeather currentWeather = response.body();
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Log.v("http fail: ", t.getMessage());
            }
        });

        //Get 5 Day forecast for the specified location
        Call<FiveDayWeather> forecastCall = weatherService.get5DayForecast(latitude, longitude, metric, API_ID);
        forecastCall.enqueue(new Callback<FiveDayWeather>() {
            @Override
            public void onResponse(Call<FiveDayWeather> call, Response<FiveDayWeather> response) {

                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                FiveDayWeather forecastWeatherList = response.body();
            }

            @Override
            public void onFailure(Call<FiveDayWeather> call, Throwable t) {
                Log.v("http fail: ", t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, R.string.permissiondenied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
