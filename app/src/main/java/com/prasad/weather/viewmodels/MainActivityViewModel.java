package com.prasad.weather.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.prasad.weather.interfaces.Api;
import com.prasad.weather.models.Forecast;
import com.prasad.weather.models.WeatherUpdate;
import com.prasad.weather.repo.RetrofitGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {

    public MutableLiveData<WeatherUpdate> weatherUpdateMutableLiveData ;
    public MutableLiveData<Forecast> forecastMutableLiveData;
    public MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();
    public MutableLiveData<Boolean> show_networkError = new MutableLiveData<>();
    private Call<WeatherUpdate> apicall;
    private Call<Forecast> apicall_forecast;

    public LiveData<WeatherUpdate> getWeatherUpdateMutableLiveData() {
        if(weatherUpdateMutableLiveData == null){
            weatherUpdateMutableLiveData = new MutableLiveData<>();
            Api api = RetrofitGenerator.getApi();
            apicall =  api.getWeatherUpdate("visakhapatnam","4b5bb0a2f34b060e2c5704f8e52d68bb");
            apicall.enqueue(new Callback<WeatherUpdate>() {
                @Override
                public void onResponse(Call<WeatherUpdate> call, Response<WeatherUpdate> response) {
                    Log.d("success", "onResponse: "+response.raw().toString());
                    weatherUpdateMutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<WeatherUpdate> call, Throwable t) {
                    String error_message= t.getMessage();
                    Log.d("Error loading data", error_message);
                }
            });
        }

        return weatherUpdateMutableLiveData;
    }

    public LiveData<Forecast> getForecastMutableLiveData() {
        if(forecastMutableLiveData == null){
            forecastMutableLiveData = new MutableLiveData<>();
            Api api = RetrofitGenerator.getApi();
            apicall_forecast = api.getForecast("visakhapatnam","4b5bb0a2f34b060e2c5704f8e52d68bb");
            apicall_forecast.enqueue(new Callback<Forecast>() {
                @Override
                public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                    Log.d("success", "onResponse: "+response.raw().toString());
                    forecastMutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<Forecast> call, Throwable t) {
                    String error_message= t.getMessage();
                    Log.d("Error loading data", error_message);
                }
            });

        }
        return forecastMutableLiveData;
    }
}
