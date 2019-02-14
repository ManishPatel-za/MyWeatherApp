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
import com.pletely.insane.myweatherapp.pojos.current.Main;
import com.pletely.insane.myweatherapp.pojos.fiveday.FiveDayWeather;
import com.pletely.insane.myweatherapp.retrofit.RetrofitClientInstance;
import com.pletely.insane.myweatherapp.retrofit.WeatherService;

import java.util.Locale;

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
    private static final String API_KEY = "";

    //Default to Rosebank if no location is found
    //TODO 1. set the lats and longs
    private double mLatitude = -26.147133;
    private double mLongitude = 28.052434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();

        checkLocationPermissionAndStart();
    }

    private void initialiseViews() {

        mMinTemp = findViewById(R.id.min_temp);
        mMaxTemp = findViewById(R.id.max_temp);
        mCurrentMiniTemp = findViewById(R.id.current_mini_temp);
        mCurrentTemp = findViewById(R.id.current_temp);
        mWeatherBackground = findViewById(R.id.weather_background);
        mForecastRecycler = findViewById(R.id.forecast_recycler);
    }

    private void checkLocationPermissionAndStart() {
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

    //Fetch weather data from API using retrofit
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
                setCurrentWeather(currentWeather);
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

    private void setCurrentWeather(CurrentWeather currentWeather) {

        if (currentWeather != null) {
            Main main = currentWeather.getMain();

            if (mCurrentTemp != null) {
                mCurrentTemp.setText(String.format(Locale.getDefault(), "%.0f", main.getTemp()));
                mCurrentMiniTemp.setText(String.format(Locale.getDefault(), "%.0f", main.getTemp()));
            }

            if (mMinTemp != null) {
                mMinTemp.setText(String.format(Locale.getDefault(), "%.0f", main.getTempMin()));
            }

            if (mMaxTemp != null) {
                mMaxTemp.setText(String.format(Locale.getDefault(), "%.0f", main.getTempMax()));
            }

            if (mWeatherBackground != null) {
                switch ((currentWeather.getWeather().get(0).getMain()).toLowerCase()) {
                    case "rain":
                        mWeatherBackground.setImageResource(R.drawable.forest_rainy);
                        break;

                    case "clear sky":
                        mWeatherBackground.setImageResource(R.drawable.forest_sunny);
                        break;

                    case "few clouds":
                        mWeatherBackground.setImageResource(R.drawable.forest_cloudy);
                        break;

                    default:
                        mWeatherBackground.setImageResource(R.drawable.forest_sunny);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermissionAndStart();
                } else {

                    //create screen to handle denying of location permission.
                    Toast.makeText(MainActivity.this, R.string.permissiondenied, Toast.LENGTH_SHORT).show();
                    finishAndRemoveTask();
                }
            }
        }
    }
}
