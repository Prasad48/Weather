package com.prasad.weather;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prasad.weather.models.Forecast;
import com.prasad.weather.models.WeatherUpdate;
import com.prasad.weather.viewmodels.MainActivityViewModel;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    TextView location,timestamp,temperature,weathertype,mintemp,maxtemp,sunrise,sunset,wind;
    Double temperaturecelsius,mintemperature,maxtemperature;
    public MainActivityViewModel mainActivityViewModel;
    private String TAG = "Main Activity";
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private GestureDetectorCompat gestureDetectorCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location=findViewById(R.id.location);
        timestamp=findViewById(R.id.timestamp);
        temperature=findViewById(R.id.temperature);
        weathertype=findViewById(R.id.weatherType);
        mintemp=findViewById(R.id.mintemp);
        maxtemp=findViewById(R.id.maxtemp);
        sunrise=findViewById(R.id.sunrise);
        sunset=findViewById(R.id.sunset);
        bottom_sheet = findViewById(R.id.bottom_sheet);
//        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mainActivityViewModel  = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        gestureDetectorCompat = new GestureDetectorCompat(this,this);

        mainActivityViewModel.getWeatherUpdateMutableLiveData().observe(this, new Observer<WeatherUpdate>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(@Nullable WeatherUpdate weatherUpdate) {
                temperaturecelsius=weatherUpdate.getMain().getTemp().doubleValue();
                temperaturecelsius = (temperaturecelsius - 273.15);
                temperature.setText(temperaturecelsius.toString()+" \u2103");
                mintemperature=weatherUpdate.getMain().getTempMin().doubleValue();
                mintemperature = (mintemperature - 273.15);
                mintemp.setText(mintemperature.toString()+" \u2103");
                maxtemperature=weatherUpdate.getMain().getTempMax().doubleValue();
                maxtemperature= (maxtemperature - 273.15);
                maxtemp.setText(maxtemperature.toString());
                weathertype.setText(weatherUpdate.getWeather().get(0).getDescription());




                long dat=(long) weatherUpdate.getId().intValue();
                String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(weatherUpdate.getId().intValue() * 1000L));

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date datetime = new Date(weatherUpdate.getId().intValue());
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ZoneOffset offset = ZoneOffset.ofTotalSeconds(weatherUpdate.getTimezone());
                System.out.println("Offset is " + offset);
                String Gmt="GMT"+offset;
                sdf.setTimeZone(TimeZone.getTimeZone(Gmt));
                timestamp.setText("Updated at: "+sdf.format(datetime));


//                String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                        .format(new Date(weatherUpdate.getId().intValue() * 1000L));
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                LocalDateTime localNZ = LocalDateTime.parse(dateAsText,formatter);
//
//
//                ZonedDateTime zonedNZ = ZonedDateTime.of(localNZ, ZoneId.of(Gmt));
//                LocalDateTime localUTC = zonedNZ.withZoneSameInstant(ZoneId.of("GMT")).toLocalDateTime();
////                System.out.println("UTC Local Time: "+localUTC.format(formatter));
//                timestamp.setText("Updated at: "+localUTC.format(formatter));



//                weathertype.setText(weatherUpdate.getMain().);

                long sunrisev=(long) weatherUpdate.getId().intValue();
                String surisedt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(weatherUpdate.getId().intValue() * 1000L));

                SimpleDateFormat sunriseformat = new SimpleDateFormat("HH:mm:ss");
                Date sunrisedate = new Date();
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ZoneOffset sunriseoffset = ZoneOffset.ofTotalSeconds(weatherUpdate.getTimezone());
                String sunriseGmt="GMT"+sunriseoffset;
                sdf.setTimeZone(TimeZone.getTimeZone(sunriseGmt));
                sunrise.setText("Updated at: "+sdf.format(sunrisedate));


                long sunsetv=(long) weatherUpdate.getId().intValue();
                String sunsetdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(weatherUpdate.getId().intValue() * 1000L));

                SimpleDateFormat sunsetformat = new SimpleDateFormat("HH:mm:ss");
                Date sunsetdate = new Date();
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ZoneOffset sunsetoffset = ZoneOffset.ofTotalSeconds(weatherUpdate.getTimezone());
                String sunsetgmt="GMT"+sunsetoffset;
                sdf.setTimeZone(TimeZone.getTimeZone(sunsetgmt));
                sunrise.setText("Updated at: "+sunsetformat.format(sunsetdate));

//                windv=weatherUpdate.getWind().getDeg().doubleValue();
//                windv= (windv - 273.15);
//                wind.setText(""+windv);

//                        +" ,speed: "+weatherUpdate.getWind().getSpeed().toString());




                location.setText(weatherUpdate.getName().concat(",").concat(weatherUpdate.getSys().getCountry()));




                Log.d(TAG, "onChanged: ");
            }
        });

        mainActivityViewModel.getForecastMutableLiveData().observe(this, new Observer<Forecast>() {
            @Override
            public void onChanged(@Nullable Forecast forecast) {
                Log.d(TAG, "onChanged: ");
            }
        });
        gestureDetectorCompat.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("bub", "onFling: " + e1.toString() + e2.toString());
        return false;
    }



}





