package com.prasad.weather.repo;

import android.util.Log;

//import com.prasad.weather.NetworkDetection;
//import com.prasad.weather.interfaces.Api;

import com.prasad.weather.interfaces.Api;

import org.jetbrains.annotations.NotNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitGenerator {

    private static final String TAG = "RetrofitGenerator";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String HEADER_PRAGMA = "Pragma";

    private static RetrofitGenerator instance;

    public static RetrofitGenerator getInstance(){
        if(instance == null){
            instance = new RetrofitGenerator();
        }
        return instance;
    }


    private static Retrofit retrofit(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();
    }


    private static HttpLoggingInterceptor httpLoggingInterceptor ()
    {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor( new HttpLoggingInterceptor.Logger()
                {
                    @Override
                    public void log (@NotNull String message)
                    {
                        Log.d(TAG, "log: http log: " + message);
                    }
                } );
        httpLoggingInterceptor.setLevel( HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    public static Api getApi(){
        return retrofit().create(Api.class);
    }

}

