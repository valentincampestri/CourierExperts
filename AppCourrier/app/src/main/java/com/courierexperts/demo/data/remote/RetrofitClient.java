package com.courierexperts.demo.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Retrofit singleton.
 * Hoy usa MockInterceptor para desarrollar sin backend.
 * Cuando tengas tu API real, quitá el MockInterceptor y seteá la baseUrl real.
 */
public class RetrofitClient {

    private static volatile ApiService API;

    public static ApiService api(Context ctx) {
        if (API == null) {
            synchronized (RetrofitClient.class) {
                if (API == null) {
                    // Logs de red (útiles en desarrollo)
                    HttpLoggingInterceptor log = new HttpLoggingInterceptor();
                    log.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new MockInterceptor()) // <-- quitar cuando uses backend real
                            .addInterceptor(log)
                            .build();

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://mock.local/") // <-- reemplazar por tu base URL real
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(client)
                            .build();

                    API = retrofit.create(ApiService.class);
                }
            }
        }
        return API;
    }
}
