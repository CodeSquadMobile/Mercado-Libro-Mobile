package com.example.mercadolibromobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mercadolibromobile.api.BookApi;
import com.example.mercadolibromobile.api.CarritoApi;
import com.example.mercadolibromobile.api.RetrofitClient;
import com.example.mercadolibromobile.adapters.CarritoAdapter;
import com.example.mercadolibromobile.models.ItemCarrito;
import com.example.mercadolibromobile.models.Book;
import com.example.mercadolibromobile.R;
import com.example.mercadolibromobile.fragments.fragment_direccion; // Import añadido

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragment_Finalizar extends Fragment {

    private RecyclerView recyclerViewCarrito;
    private CarritoAdapter carritoAdapter;
    private Button btnFinalizarCompra;
    private List<ItemCarrito> itemsCarrito = new ArrayList<>();
    private List<Book> libros = new ArrayList<>();
    private static final String BASE_URL = "https://backend-mercado-libro-mobile.onrender.com/api/";
    private static final String TAG = "Fragment_Finalizar";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__finalizar, container, false);

        recyclerViewCarrito = view.findViewById(R.id.recyclerViewCarrito);
        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(getContext()));
        btnFinalizarCompra = view.findViewById(R.id.btnFinalizarCompra);

       
        obtenerCarrito();


        btnFinalizarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarCompra();
            }
        });

        return view;
    }

    private void obtenerCarrito() {
        String token = getAccessToken();

        if (token == null) {
            Toast.makeText(getContext(), "Token no encontrado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Token no encontrado.");
            return;
        }

        CarritoApi carritoApi = RetrofitClient.getInstance(BASE_URL).create(CarritoApi.class);
        Call<List<ItemCarrito>> call = carritoApi.obtenerCarrito("Bearer " + token);

        call.enqueue(new Callback<List<ItemCarrito>>() {
            @Override
            public void onResponse(Call<List<ItemCarrito>> call, Response<List<ItemCarrito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemsCarrito = response.body();
                    obtenerListaLibros(itemsCarrito);
                } else {
                    Log.e(TAG, "Error al obtener el carrito. Código de respuesta: " + response.code());
                    Toast.makeText(getContext(), "Error al obtener el carrito.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ItemCarrito>> call, Throwable t) {
                Log.e(TAG, "Fallo la conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Fallo la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerListaLibros(List<ItemCarrito> itemsCarrito) {
        Call<List<Book>> call = RetrofitClient.getInstance(BASE_URL).create(BookApi.class).getBooks();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    libros = response.body();
                    configurarAdapter(itemsCarrito, libros);
                } else {
                    Log.e(TAG, "Error al obtener los libros. Código de respuesta: " + response.code());
                    Toast.makeText(getContext(), "Error al obtener los libros.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e(TAG, "Fallo la conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Fallo la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarAdapter(List<ItemCarrito> itemsCarrito, List<Book> libros) {
        carritoAdapter = new CarritoAdapter(itemsCarrito, libros);
        recyclerViewCarrito.setAdapter(carritoAdapter);
    }

    private void finalizarCompra() {

        fragment_direccion direccionFragment = new fragment_direccion();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, direccionFragment) // Asegúrate de que el fragmento se pase aquí
                .addToBackStack(null)
                .commit();
    }

    private String getAccessToken() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("access_token", null);
    }
}
