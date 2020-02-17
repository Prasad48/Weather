package com.prasad.weather.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasad.weather.MainActivity;
import com.prasad.weather.R;
import com.prasad.weather.models.Forecast;
import com.prasad.weather.models.WeatherUpdate;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

public class ForecastRecycler extends RecyclerView.Adapter<ForecastRecycler.RecyclerViewHolder> {
    Forecast forecast;
    public static final String IMG_URL = "https://openweathermap.org/img/w/";
    Context context;
    LayoutInflater inflater;
    private Double temperaturecelsius,mintemperature,maxtemperature;


    public ForecastRecycler(Forecast forecast, Context context) {
        this.forecast = forecast;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    @NonNull
    @Override
    public ForecastRecycler.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.forecast_list_row,viewGroup,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ForecastRecycler.RecyclerViewHolder recyclerViewHolder, int i) {

        temperaturecelsius=forecast.getList().get(i).getMain().getTemp().doubleValue();
        temperaturecelsius = (temperaturecelsius - 273.15);
        recyclerViewHolder.temperature.setText(Math.round(temperaturecelsius)+" \u2103");
        mintemperature=forecast.getList().get(i).getMain().getTempMin().doubleValue();
        mintemperature =  (mintemperature - 273.15);
        recyclerViewHolder.mintemp.setText("Minimum: "+Math.round(mintemperature)+" \u2103");
        maxtemperature=forecast.getList().get(i).getMain().getTempMax().doubleValue();
        maxtemperature= (maxtemperature - 273.15);
        recyclerViewHolder.maxtemp.setText("Maximum :"+Math.round(maxtemperature)+" \u2103");

        recyclerViewHolder.timestampdt.setText(forecast.getList().get(i).getDtTxt());
        String iconName=forecast.getList().get(i).getWeather().get(0).getIcon();
        Picasso.with(recyclerViewHolder.weathertypeimg.getContext()).load(IMG_URL +iconName +".png").into(recyclerViewHolder.weathertypeimg);
        recyclerViewHolder.weathertype.setText(forecast.getList().get(i).getWeather().get(0).getDescription());


        ZoneOffset offset = ZoneOffset.ofTotalSeconds(forecast.getCity().getTimezone());
        String Gmt="GMT"+offset;
//        SimpleDateFormat sunrisedtformat = new SimpleDateFormat("HH:mm");
//        long sunrisev=forecast.getList().get(0).getSys().getSunrise().longValue();
//        long sunrisetimestamp = sunrisev * 1000; // msec
//        java.util.Date sunrisedt = new java.util.Date(sunrisetimestamp);
//        sunrisedtformat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        sunrisedtformat.setTimeZone(TimeZone.getTimeZone(Gmt));
//
//
//        long sunsetv=forecast.getList().get(0).getSys().getSunrise().longValue();
//        long sunsettimestamp = sunsetv * 1000; // msec
//        java.util.Date sunsetdt = new java.util.Date(sunsettimestamp);
//        sunrisedtformat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        sunrisedtformat.setTimeZone(TimeZone.getTimeZone(Gmt));
//
//        recyclerViewHolder.sunrise.setText(""+sunrisedtformat.format(sunrisedt));
//        recyclerViewHolder.sunset.setText(""+sunrisedtformat.format(sunsetdt));
        int humidity=forecast.getList().get(0).getMain().getHumidity().intValue();
        int pressure=forecast.getList().get(0).getMain().getPressure().intValue();
        Double w=forecast.getList().get(0).getWind().getSpeed().doubleValue();
        recyclerViewHolder.humidity.setText(""+humidity);
        recyclerViewHolder.pressure.setText(""+pressure);
        recyclerViewHolder.wind.setText(w.toString());
        recyclerViewHolder.feelike.setText(Math.round(forecast.getList().get(i).getMain().getFeelsLike().doubleValue()-273.15)+" \u2103");
    }

    @Override
    public int getItemCount() {
        return forecast.getList().size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView feelike,location,timestampdt,temperature,weathertype,mintemp,maxtemp,sunrise,sunset,wind,pressure,humidity;
        ImageView weathertypeimg;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            location=itemView.findViewById(R.id.location);
            timestampdt=itemView.findViewById(R.id.timestamp);
            temperature=itemView.findViewById(R.id.temperature);
            weathertype=itemView.findViewById(R.id.weatherType);
            mintemp=itemView.findViewById(R.id.mintemp);
            maxtemp=itemView.findViewById(R.id.maxtemp);
            sunrise=itemView.findViewById(R.id.sunrise);
            sunset=itemView.findViewById(R.id.sunset);
            wind=itemView.findViewById(R.id.wind);
            weathertypeimg=itemView.findViewById(R.id.weathertypeimg);
            humidity=itemView.findViewById(R.id.humidity);
            pressure=itemView.findViewById(R.id.pressure);
            wind=itemView.findViewById(R.id.wind);
            feelike=itemView.findViewById(R.id.feelslike);
        }

    }
}
