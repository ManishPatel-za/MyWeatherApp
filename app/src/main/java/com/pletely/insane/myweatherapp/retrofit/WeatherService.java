package com.pletely.insane.myweatherapp.retrofit;

import com.pletely.insane.myweatherapp.pojos.current.CurrentWeather;
import com.pletely.insane.myweatherapp.pojos.fiveday.FiveDayWeather;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    //curent weather call:
    //http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&APPID=
    @GET("weather")
    Call<CurrentWeather> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String metric, @Query("APPID") String apiKey);

    //5 day forecast:
    //http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&&metric=metric&APPID=
    @GET("forecast")
    Call<FiveDayWeather> get5DayForecast(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String metric, @Query("APPID") String apiKey);
}
