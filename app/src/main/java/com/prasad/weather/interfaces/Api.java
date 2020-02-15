package com.prasad.weather.interfaces;

import com.prasad.weather.models.Forecast;
import com.prasad.weather.models.WeatherUpdate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @GET("weather")
    Call<WeatherUpdate> getWeatherUpdate(@Query("q") String cityname, @Query("APPID") String api_key);

    @GET("forecast")
    Call<Forecast> getForecast(@Query("q") String cityname, @Query("APPID") String api_key);
}
