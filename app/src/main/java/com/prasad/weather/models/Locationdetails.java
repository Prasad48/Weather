package com.prasad.weather.models;

public class Locationdetails {

    private static Locationdetails single_instance = null;
    public String city;



    public static Locationdetails getInstance()
    {
        if (single_instance == null)
            single_instance = new Locationdetails();

        return single_instance;
    }

}
