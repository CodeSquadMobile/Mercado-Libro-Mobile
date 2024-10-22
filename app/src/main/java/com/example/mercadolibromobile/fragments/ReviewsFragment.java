package com.example.mercadolibromobile.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mercadolibromobile.R;
import com.example.mercadolibromobile.api.ApiClient;
import com.example.mercadolibromobile.api.ReviewService;
import com.example.mercadolibromobile.models.Review;
import com.example.mercadolibromobile.adapters.ReviewAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewResenas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargar las reseñas del usuario logueado
        loadUserReviews();

        return view;
    }

    private void loadUserReviews() {
        // Obtener el servicio de reseñas
        ReviewService reviewService = ApiClient.getClient().create(ReviewService.class);

        // Token de autenticación (por ahora, supongamos que lo tienes guardado en algún lugar)
        String authToken = "Bearer tu_token_aqui"; // Reemplaza con el token del usuario logueado

        // Realizar la llamada a la API para obtener las reseñas del usuario
        Call<List<Review>> call = reviewService.getUserReviews(authToken);
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    // Obtener las reseñas
                    List<Review> reviews = response.body();

                    // Configurar el adaptador con los datos
                    reviewAdapter = new ReviewAdapter(reviews);
                    recyclerView.setAdapter(reviewAdapter);
                } else {
                    Toast.makeText(getContext(), "Error al cargar las reseñas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
