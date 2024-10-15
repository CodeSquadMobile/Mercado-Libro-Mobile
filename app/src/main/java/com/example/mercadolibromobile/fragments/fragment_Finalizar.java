package com.example.mercadolibromobile.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.mercadolibromobile.R;
import com.example.mercadolibromobile.api.DirApi;
import com.example.mercadolibromobile.models.Direccion;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fragment_Finalizar extends Fragment {

    private LinearLayout contenedorLibros;
    private Button finalizarButton;
    private List<Libro> listaLibros;
    private TextView precioTotalTextView;

    public fragment_Finalizar() {
        // Constructor público vacío requerido
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaLibros = obtenerLibrosDelCarrito(); // Llamamos la función aquí
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__finalizar, container, false);

        contenedorLibros = view.findViewById(R.id.contenedorLibros);
        finalizarButton = view.findViewById(R.id.button4);
        precioTotalTextView = view.findViewById(R.id.precioTotal);

        // Inicializar precio total
        actualizarVistaLibros();

        // Acción del botón finalizar
        finalizarButton.setOnClickListener(v -> {
            obtenerDirecciones();  // Llamada a la API de direcciones
        });

        return view;
    }

    // Método para actualizar la vista de los libros y el precio total
    private void actualizarVistaLibros() {
        contenedorLibros.removeAllViews(); // Limpiar el contenedor antes de actualizarlo
        double precioTotal = 0.0;

        for (Libro libro : listaLibros) {
            precioTotal += agregarProducto(libro);
        }

        // Mostrar precio total
        precioTotalTextView.setText("Total: $" + String.format("%.2f", precioTotal));
    }

    // Función para agregar productos a la vista
    private double agregarProducto(Libro libro) {
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 8, 8);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(8);
        cardView.setCardElevation(4);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.beige_transparente));

        // Crear layout horizontal para los datos
        LinearLayout layoutHorizontal = new LinearLayout(getContext());
        layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        layoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layoutHorizontal.setPadding(16, 16, 16, 16);

        // Crear un layout para los detalles del libro (nombre, cantidad y precio)
        LinearLayout layoutDetalles = new LinearLayout(getContext());
        layoutDetalles.setOrientation(LinearLayout.VERTICAL);
        layoutDetalles.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // Esto permite que los detalles ocupen la mayor parte del espacio
        ));

        // Texto con información del libro
        TextView nombreView = new TextView(getContext());
        nombreView.setText("Libro: " + libro.getNombre());
        nombreView.setTextSize(16);
        nombreView.setTextColor(getResources().getColor(R.color.black));

        TextView cantidadView = new TextView(getContext());
        cantidadView.setText("Cantidad: " + libro.getCantidad());
        cantidadView.setTextSize(16);
        cantidadView.setTextColor(getResources().getColor(R.color.black));

        // Crear un layout horizontal para el precio y los iconos
        LinearLayout layoutPrecioIconos = new LinearLayout(getContext());
        layoutPrecioIconos.setOrientation(LinearLayout.HORIZONTAL);
        layoutPrecioIconos.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layoutPrecioIconos.setWeightSum(1); // Para usar pesos en la distribución

        TextView precioView = new TextView(getContext());
        precioView.setText("Precio: $" + String.format("%.2f", libro.getPrecio()));
        precioView.setTextSize(16);
        precioView.setTextColor(getResources().getColor(R.color.black));

        // Añadir precio al layoutPrecioIconos
        layoutPrecioIconos.addView(precioView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f)); // Peso 0.6

        // Crear un layout para los iconos y alinearlo a la derecha
        LinearLayout layoutIconos = new LinearLayout(getContext());
        layoutIconos.setOrientation(LinearLayout.HORIZONTAL);
        layoutIconos.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.4f
        ));
        layoutIconos.setGravity(Gravity.END); // Alinear los iconos al final

        // Icono de sumar
        ImageView sumarIcon = new ImageView(getContext());
        sumarIcon.setImageResource(R.drawable.ic_sumar); // icono vector
        sumarIcon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        sumarIcon.setPadding(16, 0, 0, 0); // Espacio entre el texto y el icono
        sumarIcon.setOnClickListener(v -> {
            libro.setCantidad(libro.getCantidad() + 1);
            cantidadView.setText("Cantidad: " + libro.getCantidad());
            actualizarPrecioTotal();
        });

        // Icono de restar
        ImageView restarIcon = new ImageView(getContext());
        restarIcon.setImageResource(R.drawable.ic_restar); // icono de resta
        restarIcon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        restarIcon.setPadding(16, 0, 0, 0); // Espacio entre los iconos
        restarIcon.setOnClickListener(v -> {
            if (libro.getCantidad() > 1) {
                libro.setCantidad(libro.getCantidad() - 1);
                cantidadView.setText("Cantidad: " + libro.getCantidad());
                actualizarPrecioTotal();
            } else {
                Toast.makeText(getContext(), "No se puede reducir la cantidad a menos de 1", Toast.LENGTH_SHORT).show();
            }
        });

        // Icono de eliminar
        ImageView eliminarIcon = new ImageView(getContext());
        eliminarIcon.setImageResource(R.drawable.ic_borrar); // icono de borrar
        eliminarIcon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        eliminarIcon.setPadding(16, 0, 0, 0); // Espacio entre los iconos
        eliminarIcon.setOnClickListener(v -> {
            contenedorLibros.removeView(cardView);
            listaLibros.remove(libro); // Asegúrate de eliminar el libro de la lista
            actualizarPrecioTotal();
            Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
        });

        // Añadir iconos al layoutIconos
        layoutIconos.addView(sumarIcon);
        layoutIconos.addView(restarIcon);
        layoutIconos.addView(eliminarIcon);

        // Añadir layoutIconos al layoutPrecioIconos
        layoutPrecioIconos.addView(layoutIconos); // Añadir layoutIconos a la derecha

        // Añadir nombre y cantidad al layoutDetalles
        layoutDetalles.addView(nombreView);
        layoutDetalles.addView(cantidadView);
        layoutDetalles.addView(layoutPrecioIconos); // Añadir layoutPrecioIconos al final de los detalles

        // Añadir detalles y los iconos al layout principal horizontal
        layoutHorizontal.addView(layoutDetalles);

        // Añadir el layout horizontal al CardView
        cardView.addView(layoutHorizontal);
        contenedorLibros.addView(cardView);

        return libro.getCantidad() * libro.getPrecio(); // Retorna el precio total del libro
    }

    private List<Libro> obtenerLibrosDelCarrito() {
        List<Libro> libros = new ArrayList<>();
        libros.add(new Libro("Harry Potter", 2, 20.99));
        libros.add(new Libro("The Lord of the Rings", 1, 15.99));
        libros.add(new Libro("Pride and Prejudice", 3, 12.99));
        return libros;
    }

    private void actualizarPrecioTotal() {
        double precioTotal = 0.0;
        for (Libro libro : listaLibros) {
            precioTotal += libro.getCantidad() * libro.getPrecio();
        }
        precioTotalTextView.setText("Total: $" + String.format("%.2f", precioTotal));
    }

    // Retrofit para obtener direcciones
    private void obtenerDirecciones() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.50:8000/api/direcciones/") // URL del backend
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirApi dirApi = retrofit.create(DirApi.class);

        Call<List<Direccion>> call = dirApi.getDirecciones();

        call.enqueue(new Callback<List<Direccion>>() {
            @Override
            public void onResponse(Call<List<Direccion>> call, Response<List<Direccion>> response) {
                if (response.isSuccessful()) {
                    List<Direccion> direcciones = response.body();
                    mostrarDirecciones(direcciones);
                } else {
                    Toast.makeText(getActivity(), "Error al obtener direcciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Direccion>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDirecciones(List<Direccion> direcciones) {
        if (direcciones != null && !direcciones.isEmpty()) {
            for (Direccion direccion : direcciones) {
                // código para mostrar las direcciones
                Toast.makeText(getActivity(), "Dirección: " + direccion.getCalle(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No hay direcciones disponibles", Toast.LENGTH_SHORT).show();
        }
    }


    public class Libro {
        private String nombre;
        private int cantidad;
        private double precio;

        public Libro(String nombre, int cantidad, double precio) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecio() {
            return precio;
        }

        public void setPrecio(double precio) {
            this.precio = precio;
        }
    }
}