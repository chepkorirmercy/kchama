package com.sharon.sample.mpesa;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionDatabase {
//    private static final String BASE_URL="http://192.168.0.1/";
        private static final String BASE_URL = "http://chamafinalyear.atwebpages.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}

