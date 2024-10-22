package com.example.mercadolibromobile.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.mercadolibromobile.models.Review;

import java.util.List;

public interface ReviewService {

    @GET("api/resenas/")
    Call<List<Review>> getUserReviews(
            @Header("Authorization") String token // Esto será el token del usuario logueado
    );
}
