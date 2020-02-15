package com.prasad.weather;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prasad.weather.models.Forecast;
import com.prasad.weather.models.WeatherUpdate;
import com.prasad.weather.viewmodels.MainActivityViewModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    TextView location,timestampdt,temperature,weathertype,mintemp,maxtemp,sunrise,sunset,wind,pressure,humidity;
    ImageView weathertypeimg;
    public static final String IMG_URL = "https://openweathermap.org/img/w/";
    public String iconName;
    Button retry;
    Double temperaturecelsius,mintemperature,maxtemperature;
    public MainActivityViewModel mainActivityViewModel;
    private String TAG = "Main Activity";
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private ProgressBar progressBar;
    private RelativeLayout layout_nonetwork;
    public MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();
    public MutableLiveData<Boolean> show_networkError = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location=findViewById(R.id.location);
        timestampdt=findViewById(R.id.timestamp);
        weathertypeimg = findViewById(R.id.weathertypeimg);
        temperature=findViewById(R.id.temperature);
        weathertype=findViewById(R.id.weatherType);
        mintemp=findViewById(R.id.mintemp);
        maxtemp=findViewById(R.id.maxtemp);
        sunrise=findViewById(R.id.sunrise);
        sunset=findViewById(R.id.sunset);
        wind=findViewById(R.id.wind);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        mainActivityViewModel  = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        layout_nonetwork = findViewById(R.id.no_network);

        if(!isNetworkAvailable()){
            layout_nonetwork.setVisibility(View.VISIBLE);
            findViewById(R.id.bottom_sheet).setVisibility(View.INVISIBLE);
//            findViewById(R.id.mainActivity).setVisibility(View.INVISIBLE);
        }
        else {
            layout_nonetwork.setVisibility(View.GONE);
            findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);
            findViewById(R.id.mainActivity).setBackgroundResource(R.drawable.background);
        }
        progressBar = findViewById(R.id.progress_bar);
        retry=findViewById(R.id.retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivityViewModel.retry();
//                System.out.println("retry clicked");
//                mainActivityViewModel.getWeatherUpdateMutableLiveData().observe(MainActivity.this, new Observer<WeatherUpdate>() {
//                    @Override
//                    public void onChanged(@Nullable WeatherUpdate weatherUpdate) {
//                        layout_nonetwork.setVisibility(View.GONE);
//                        findViewById(R.id.mainActivity).setVisibility(View.VISIBLE);
//                        findViewById(R.id.mainActivity).setBackgroundResource(R.drawable.background);
//                    }
//                });

            }
        });

        mainActivityViewModel.getShowProgressBar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        mainActivityViewModel.getShow_networkError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    layout_nonetwork.setVisibility(View.VISIBLE);
                } else {
                    layout_nonetwork.setVisibility(View.GONE);
                }
            }
        });



        mainActivityViewModel.getWeatherUpdateMutableLiveData().observe(this, new Observer<WeatherUpdate>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(@Nullable WeatherUpdate weatherUpdate) {
                pressure=findViewById(R.id.pressure);
                humidity=findViewById(R.id.humidity);
                temperaturecelsius=weatherUpdate.getMain().getTemp().doubleValue();
                temperaturecelsius = (temperaturecelsius - 273.15);
                temperature.setText(temperaturecelsius.toString()+" \u2103");
                mintemperature=weatherUpdate.getMain().getTempMin().doubleValue();
                mintemperature = (mintemperature - 273.15);
                mintemp.setText(mintemperature.toString()+" \u2103");
                maxtemperature=weatherUpdate.getMain().getTempMax().doubleValue();
                maxtemperature= (maxtemperature - 273.15);
                maxtemp.setText(maxtemperature.toString()+" \u2103");
                weathertype.setText(weatherUpdate.getWeather().get(0).getDescription());


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long unixtime=weatherUpdate.getDt().longValue();
                long timestamp = unixtime * 1000; // msec
                java.util.Date d = new java.util.Date(timestamp);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ZoneOffset offset = ZoneOffset.ofTotalSeconds(weatherUpdate.getTimezone());
                String Gmt="GMT"+offset;
                sdf.setTimeZone(TimeZone.getTimeZone(Gmt));
                System.out.println(("Updated at: "+sdf.format(d)));
                timestampdt.setText("Updated at: "+sdf.format(d));



                SimpleDateFormat sunrisedtformat = new SimpleDateFormat("HH:mm");
                long sunrisev=weatherUpdate.getSys().getSunrise().longValue();
                long sunrisetimestamp = sunrisev * 1000; // msec
                java.util.Date sunrisedt = new java.util.Date(sunrisetimestamp);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                sdf.setTimeZone(TimeZone.getTimeZone(Gmt));
//                System.out.println(("Updated at: "+sunrisedtformat.format(sunrisedt)));
                sunrise.setText(""+sunrisedtformat.format(sunrisedt));

                long sunsetv=weatherUpdate.getSys().getSunset().longValue();
                long sunsettimestamp = sunsetv * 1000; // msec
                java.util.Date sunsetdt = new java.util.Date(sunsettimestamp);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                sdf.setTimeZone(TimeZone.getTimeZone(Gmt));
                System.out.println(("Updated at: "+sunrisedtformat.format(sunsetdt)));
                sunset.setText(""+sunrisedtformat.format(sunsetdt));

                Double windv=weatherUpdate.getWind().getSpeed().doubleValue();
                wind.setText(windv.toString());

                location.setText(weatherUpdate.getName().concat(",").concat(weatherUpdate.getSys().getCountry()));

                humidity.setText(""+weatherUpdate.getMain().getHumidity());
                pressure.setText(""+weatherUpdate.getMain().getPressure());
//                weathertypeimg.se
                iconName=weatherUpdate.getWeather().get(0).getIcon();
                Picasso.with(MainActivity.this).load(IMG_URL +iconName +".png").into(weathertypeimg);
                findViewById(R.id.mainActivity).setBackgroundResource(R.drawable.background);
                layout_nonetwork.setVisibility(View.GONE);
                findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);



                Log.d(TAG, "onChanged: ");
            }
        });

        mainActivityViewModel.getForecastMutableLiveData().observe(this, new Observer<Forecast>() {
            @Override
            public void onChanged(@Nullable Forecast forecast) {
                Log.d(TAG, "onChanged: ");
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_UP) :
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }







}





