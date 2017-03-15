package com.sanislo.lostandfound.model.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 16.03.17.
 */

public class ApiModule {
    private static Retrofit RETROFIT_CLIENT;
    private static ApiInterface API_INTERFACE;

    private static Retrofit createApiInterface(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient);
        return builder.build();
    }

    public static ApiInterface getApiInterface() {
        if (RETROFIT_CLIENT == null){
            RETROFIT_CLIENT = createApiInterface();
            API_INTERFACE = RETROFIT_CLIENT.create(ApiInterface.class);
        }

        return API_INTERFACE;
    }

    public static Retrofit getRetrofitClient(){
        if (RETROFIT_CLIENT == null){
            RETROFIT_CLIENT = createApiInterface();
        }
        return RETROFIT_CLIENT;
    }
}
