package com.courierexperts.demo.data.remote;

import com.courierexperts.demo.domain.model.Purchase;
import com.courierexperts.demo.domain.model.Shipment;
import com.courierexperts.demo.domain.model.UserPackage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/purchases")
    Call<List<Purchase>> getPurchases();

    @GET("/packages")
    Call<List<UserPackage>> getPackages();

    @GET("/shipments")
    Call<List<Shipment>> getShipments();
}
