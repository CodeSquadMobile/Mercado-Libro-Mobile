package com.example.mercadolibromobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mercadolibromobile.activities.MisResenasActivity;

import com.example.mercadolibromobile.R;
import com.example.mercadolibromobile.adapters.ResenaAdapter;
import com.example.mercadolibromobile.api.ApiService;
import com.example.mercadolibromobile.models.Resena;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MisResenasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResenaAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_resenas);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el adaptador con una lista vacía
        adapter = new ResenaAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Inicializar Retrofit y apiService antes de llamar a getResenas()
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://backend-mercado-libro-mobile.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(ApiService.class);

        // Realizar la llamada a la API
        getResenas();

        // Configurar el botón para abrir la actividad de agregar reseñas
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MisResenasActivity.this, AddResenasActivity.class);
            startActivity(intent);
        });
    }

    private void getResenas() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("access_token", null);
        String emailUsuario = sharedPreferences.getString("user_email", null);

        if (token != null && emailUsuario != null) {
            Call<List<Resena>> call = apiService.getResenas("Bearer " + token);
            call.enqueue(new Callback<List<Resena>>() {
                @Override
                public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Resena> allResenas = response.body();
                        List<Resena> userResenas = new ArrayList<>();

                        Log.d("MisResenasActivity", "Total reseñas recuperadas: " + allResenas.size());
                        Log.d("MisResenasActivity", "Email del usuario: " + emailUsuario);

                        for (Resena resena : allResenas) {
                            Log.d("MisResenasActivity", "Reseña: " + resena.getComentario() + ", Usuario: " + resena.getEmailUsuario());
                            if (resena.getEmailUsuario() != null && resena.getEmailUsuario().equals(emailUsuario)) {
                                userResenas.add(resena);
                            }
                        }

                        // Actualizar el adaptador con las reseñas del usuario
                        adapter.updateResenas(userResenas);
                        if (userResenas.isEmpty()) {
                            Toast.makeText(MisResenasActivity.this, "No se encontraron reseñas para el usuario.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MisResenasActivity.this, "No se encontraron reseñas.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Resena>> call, Throwable t) {
                    Log.e("MisResenasActivity", "Error al obtener reseñas", t);
                    Toast.makeText(MisResenasActivity.this, "Error al obtener reseñas.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No se encontró el token o el correo del usuario. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            // Puedes agregar un intent para redirigir al usuario al LoginActivity
        }
    }
}
