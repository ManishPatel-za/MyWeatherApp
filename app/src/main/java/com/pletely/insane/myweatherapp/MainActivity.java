package com.pletely.insane.myweatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pletely.insane.myweatherapp.pojos.current.CurrentWeather;
import com.pletely.insane.myweatherapp.pojos.fiveday.FiveDayWeather;
import com.pletely.insane.myweatherapp.retrofit.RetrofitClientInstance;
import com.pletely.insane.myweatherapp.retrofit.WeatherService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String metric = "metric";

    private RecyclerView mForecastRecycler;
    private TextView mCurrentTemp, mMinTemp, mCurrentMiniTemp, mMaxTemp;
    private ImageView mWeatherBackground;
    private FusedLocationProviderClient mFusedLocationClient;

    //TODO: 2. Remove API KEY from code base before commiting!!!
    //TODO: 3. elevation for controls
    //TODO: 4. toolbar & actionbar
    //TODO: 5. unit(metric VS imperial)
    //TODO: 6. Onsave instance and restore
    //TODO: 7. Handle for tablet Mode
    //TODO: 8. if time:choose sea or forest theme/firebase login/unit test/control animations
    private static final String API_KEY = "760d62673766596fe58627fa0102e7ce";

    //Default to Rosebank if no location is found
    //TODO 1. set the lats and longs
    private double mLatitude = -26.147133;
    private double mLongitude = 28.052434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();

        checkLocationPermission();
    }

    private void initialiseViews() {

        mMinTemp = (TextView) findViewById(R.id.min_temp);
        mMaxTemp = (TextView) findViewById(R.id.max_temp);
        mCurrentMiniTemp = (TextView) findViewById(R.id.current_mini_temp);
        mCurrentTemp = (TextView) findViewById(R.id.current_temp);
        mWeatherBackground = (ImageView) findViewById(R.id.weather_background);
        mForecastRecycler = (RecyclerView) findViewById(R.id.forecast_recycler);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                mLatitude = location.getLatitude();
                                mLongitude = location.getLongitude();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });


            fetchWeatherData(String.valueOf(mLatitude), String.valueOf(mLongitude), metric);
        }
    }

    private void fetchWeatherData(String latitude, String longitude, String metric) {

        WeatherService weatherService = RetrofitClientInstance.getRetrofitInstance().create(WeatherService.class);

        //Get current weather for the specified location
        Call<CurrentWeather> currentCall = weatherService.getCurrentWeather(latitude, longitude, metric, API_KEY);
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
        Call<FiveDayWeather> forecastCall = weatherService.get5DayForecast(latitude, longitude, metric, API_KEY);
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
