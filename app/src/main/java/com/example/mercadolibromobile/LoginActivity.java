package com.example.mercadolibromobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log; // log para testear login
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import com.example.mercadolibromobile.api.LoginApi;
import com.example.mercadolibromobile.models.AuthModels;
import com.example.mercadolibromobile.api.RetrofitClient;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout usernameLayout, passwordLayout, nameLayout;
    private TextInputEditText usernameEditText, passwordEditText, nameEditText;
    private Button loginButton, toggleModeButton;
    private ProgressBar progressBar;
    private boolean isLoginMode = true;
//<<<<<<< marcelolunadallalasta

    private final String BASE_URL = "https://backend-mercado-libro-mobile.onrender.com/api/";
//=======
    //  NAHIR IP
//    private final String BASE_URL = "http://192.168.100.26:8000/api/";
    //Ivette URL
    //private final String BASE_URL = "http://192.168.0.244:8000/api/";
    //URL MARCELO EMULADOR
    //private final String BASE_URL = "http://10.0.2.2:8000/api/";
//>>>>>>> develop
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        usernameLayout = findViewById(R.id.textInputLayout2);
        passwordLayout = findViewById(R.id.textInputLayout);
        nameLayout = findViewById(R.id.textInputLayoutName);
        usernameEditText = findViewById(R.id.textInputEditTextUsername);
        passwordEditText = findViewById(R.id.textInputEditTextPassword);
        nameEditText = findViewById(R.id.textInputEditTextName);
        loginButton = findViewById(R.id.buttonMainAction);
        toggleModeButton = findViewById(R.id.buttonToggleMode);
        progressBar = findViewById(R.id.progressBar);

        final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        Button buttonPoliticas = findViewById(R.id.buttonpoli); // Asegúrate de que este ID coincida con el del layout

        buttonPoliticas.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Politicas.class);
            startActivity(intent);
        });

        toggleModeButton.setOnClickListener(v -> {
            toggleLoginMode(fadeIn, fadeOut);


        });


        loginButton.setOnClickListener(v -> {
            if (isLoginMode) {
                loginUser();
            } else {
                registerUser();
            }
        });


        usernameEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateButtonState();
            }
        });

        passwordEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateButtonState();
            }
        });

        nameEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateButtonState();
            }
        });
    }

    private void toggleLoginMode(Animation fadeIn, Animation fadeOut) {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            loginButton.setText(R.string.ingresar);
            toggleModeButton.setText(R.string.registrarse);
            nameLayout.setVisibility(View.GONE);
            findViewById(R.id.textViewName).setVisibility(View.GONE);
        } else {
            loginButton.setText(R.string.registrarse);
            toggleModeButton.setText(R.string.volver);
            nameLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.textViewName).setVisibility(View.VISIBLE);
            findViewById(R.id.textViewName).startAnimation(fadeIn);
        }
        // Limpia errores previos al cambiar de modo y valida el botón
        nameLayout.setError(null);
        usernameLayout.setError(null);
        passwordLayout.setError(null);
        validateButtonState();
    }

    private void validateButtonState() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        boolean isEnabled = isLoginMode ?
                !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) :
                !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name);
        loginButton.setEnabled(isEnabled);
    }

    private void loginUser() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        LoginApi api = RetrofitClient.getInstance(BASE_URL).create(LoginApi.class);
        Call<AuthModels.LoginResponse> call = api.login(email, password);

        call.enqueue(new Callback<AuthModels.LoginResponse>() {
            @Override
            public void onResponse(Call<AuthModels.LoginResponse> call, Response<AuthModels.LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    // Guarda los tokens y el correo electrónico
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access_token", response.body().getAccess());
                    editor.putString("refresh_token", response.body().getRefresh());
                    editor.putString("user_email", email); // Guarda el correo electrónico
                    editor.apply();

                    // Log para verificar los tokens
                    Log.d("LoginActivity", "Access Token: " + response.body().getAccess());
                    Log.d("LoginActivity", "Refresh Token: " + response.body().getRefresh());
                    Log.d("LoginActivity", "User Email: " + email); // Log del email

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    usernameLayout.setError("Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthModels.LoginResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                usernameLayout.setError("Error de conexión");
            }
        });
    }

    private void registerUser() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = nameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            if (TextUtils.isEmpty(username)) {
                nameLayout.setError("El nombre es requerido");
            }
            return;
        }

        AuthModels.SignupRequest signupRequest = new AuthModels.SignupRequest(email, password, username);
        LoginApi api = RetrofitClient.getInstance(BASE_URL).create(LoginApi.class);
        Call<AuthModels.SignupResponse> call = api.register(signupRequest);
        call.enqueue(new Callback<AuthModels.SignupResponse>() {
            @Override
            public void onResponse(Call<AuthModels.SignupResponse> call, Response<AuthModels.SignupResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access_token", response.body().getAccess());
                    editor.putString("refresh_token", response.body().getRefresh());
                    editor.apply();
                    Log.d("LoginActivity", "Access Token: " + response.body().getAccess());
                    Log.d("LoginActivity", "Refresh Token: " + response.body().getRefresh());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    nameLayout.setError("Error en el registro");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthModels.SignupResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                nameLayout.setError("Error de conexión");
            }
        });
    }

    // Clase auxiliar para simplificar el TextWatcher
    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }


}
