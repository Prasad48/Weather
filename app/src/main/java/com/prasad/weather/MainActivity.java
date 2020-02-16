package com.prasad.weather;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.prasad.weather.adapters.ForecastRecycler;
import com.prasad.weather.models.Forecast;
import com.prasad.weather.models.Locationdetails;
import com.prasad.weather.models.WeatherUpdate;
import com.prasad.weather.viewmodels.MainActivityViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LocationListener{

    TextView location,timestampdt,temperature,weathertype,mintemp,maxtemp,sunrise,sunset,wind,pressure,humidity;
    LocationManager locationManager;
    RecyclerView recyclerView;
    ForecastRecycler forecastRecycler;
    private String city;
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
        layout_nonetwork = findViewById(R.id.no_network);
        mainActivityViewModel  = ViewModelProviders.of(MainActivity.this).get(MainActivityViewModel.class);

        getLocation();


        if(!isNetworkAvailable()){
            layout_nonetwork.setVisibility(View.VISIBLE);
            findViewById(R.id.bottom_sheet).setVisibility(View.INVISIBLE);
            findViewById(R.id.content_main).setVisibility(View.INVISIBLE);

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


        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(city!=null){
                    System.out.println("fgfd");
                    System.out.println("fgfd");
                    mainActivityViewModel.getWeatherUpdateMutableLiveData().observe(MainActivity.this, new Observer<WeatherUpdate>() {
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
                            timestampdt.setText("Updated at: "+sdf.format(d));
                            SimpleDateFormat sunrisedtformat = new SimpleDateFormat("HH:mm");
                            long sunrisev=weatherUpdate.getSys().getSunrise().longValue();
                            long sunrisetimestamp = sunrisev * 1000; // msec
                            java.util.Date sunrisedt = new java.util.Date(sunrisetimestamp);
                            sunrisedtformat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            sunrisedtformat.setTimeZone(TimeZone.getTimeZone(Gmt));
                            sunrise.setText(""+sunrisedtformat.format(sunrisedt));
                            long sunsetv=weatherUpdate.getSys().getSunset().longValue();
                            long sunsettimestamp = sunsetv * 1000; // msec
                            java.util.Date sunsetdt = new java.util.Date(sunsettimestamp);
                            sunrisedtformat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            sunrisedtformat.setTimeZone(TimeZone.getTimeZone(Gmt));
                            sunset.setText(""+sunrisedtformat.format(sunsetdt));
                            Double windv=weatherUpdate.getWind().getSpeed().doubleValue();
                            wind.setText(windv.toString());
                            location.setText(weatherUpdate.getName().concat(",").concat(weatherUpdate.getSys().getCountry()));
                            humidity.setText(""+weatherUpdate.getMain().getHumidity());
                            pressure.setText(""+weatherUpdate.getMain().getPressure());
                            iconName=weatherUpdate.getWeather().get(0).getIcon();
                            Picasso.with(MainActivity.this).load(IMG_URL +iconName +".png").into(weathertypeimg);
                            findViewById(R.id.mainActivity).setBackgroundResource(R.drawable.background);
                            layout_nonetwork.setVisibility(View.GONE);
                            findViewById(R.id.content_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);
                            Log.d(TAG, "onChanged: ");
                        }
                    });
                }
                else {
                    Log.d(TAG, "onChanged: city null ");
                }
            }
        },5000);


        mainActivityViewModel.getForecastMutableLiveData().observe(this, new Observer<Forecast>() {
            @Override
            public void onChanged(@Nullable Forecast forecast) {
                Log.d(TAG, "onChanged: forecast");
                recyclerView=findViewById(R.id.forecastrecyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                forecastRecycler = new ForecastRecycler(forecast,MainActivity.this);


                recyclerView.setAdapter(forecastRecycler);
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = MotionEventCompat.getActionMasked(event);
         int action = event.getActionMasked();

        switch(action) {
            case (MotionEvent.ACTION_UP) :
                mainActivityViewModel.forecastretry();
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


    void getLocation() {
        try {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);
        System.out.println("fesf");
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location locate) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(locate.getLatitude(), locate.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Locationdetails coords=Locationdetails.getInstance();
            coords.city=city;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("entered");
                } else {
                    System.out.println("else");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}





