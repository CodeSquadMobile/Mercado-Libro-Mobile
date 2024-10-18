package com.example.mercadolibromobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mercadolibromobile.R;
import com.example.mercadolibromobile.api.ContactoApi;
import com.example.mercadolibromobile.api.RetrofitClient;
import com.example.mercadolibromobile.models.Contacto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContactFragment extends Fragment {

    private ContactoApi contactoApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout personalizado (fragment_contact.xml)
        View view = inflater.inflate(R.layout.fragment_contact, container, false);


        // Definir las direcciones IP
        String[] ipAddresses = {
                "http://192.168.0.50:8000/api/", //Leo
                "http://10.0.2.2:8000/api/", //Marce
                "http://192.168.100.26:8000/api/", //Nahir
                "http://192.168.0.244:8000/api/", //Ivette
                "http://192.168.0.53:8000/api/" //Invitado

        };

        // Seleccionar la IP que deseas usar
        String selectedIp = ipAddresses[0]; // Cambia el índice para seleccionar otra IP

        // Inicializar Retrofit con la IP seleccionada
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(selectedIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        contactApi = retrofit.create(ContactApi.class);

        // Referencias a los elementos del layout
        nombreEditText = view.findViewById(R.id.etNombre);
        asuntoEditText = view.findViewById(R.id.etAsunto);
        emailEditText = view.findViewById(R.id.etEmail);
        consultaEditText = view.findViewById(R.id.etConsulta);

        Button enviarConsultaButton = view.findViewById(R.id.btnEnviarConsulta);

        // Establecer el comportamiento del botón "Enviar consulta"
        enviarConsultaButton.setOnClickListener(v -> {
            String nombre = nombreEditText.getText().toString().trim();
            String asunto = asuntoEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String consulta = consultaEditText.getText().toString().trim();

            // Validación: los campos no deben estar vacíos
            if (asunto.isEmpty()) {
                asuntoEditText.setError("Por favor, escribe el asunto.");
                asuntoEditText.requestFocus();
            } else if (nombre.isEmpty()) {
                nombreEditText.setError("Por favor, escribe tu nombre.");
                nombreEditText.requestFocus();
            } else if (email.isEmpty() || !email.contains("@")) {
                emailEditText.setError("Por favor, escribe un email válido.");
                emailEditText.requestFocus();
            } else if (consulta.isEmpty()) {
                consultaEditText.setError("Por favor, escribe tu consulta.");
                consultaEditText.requestFocus();
            } else if (consulta.length() < 10) {
                consultaEditText.setError("La consulta debe tener al menos 10 caracteres.");
                consultaEditText.requestFocus();
            } else {
                // Crear el objeto Contacto con los datos ingresados
                Contacto contacto = new Contacto(nombre, email, asunto, consulta);

                // Enviar la consulta al servidor utilizando contactoApi
                enviarConsulta(contacto);
            }
        });

        return view;
    }

    private void enviarConsulta(Contacto contacto) {
        Call<Void> call = contactoApi.enviarConsulta(contacto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Consulta enviada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error al enviar la consulta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
