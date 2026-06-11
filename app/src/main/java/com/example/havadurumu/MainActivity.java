package com.example.havadurumu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private TextView textViewCityName, textViewTemp, textViewDescription;
    private ImageView imageViewWeatherIcon;
    private Button buttonSearch, buttonAddToFavorites, buttonViewFavorites;
    private DatabaseHelper dbHelper;

    // API Anahtarı
    private final String API_KEY = "5ce14b15a8e045de1eff5321d564a19c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // boşluk ayarlama
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        editTextCity = findViewById(R.id.editTextCity);
        textViewCityName = findViewById(R.id.textViewCityName);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewDescription = findViewById(R.id.textViewDescription);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonAddToFavorites = findViewById(R.id.buttonAddToFavorites);
        buttonViewFavorites = findViewById(R.id.buttonViewFavorites);

        // favorilerde şehir kontrolü
        handleIntent(getIntent());

        // arama yapma
        buttonSearch.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                if (isNetworkAvailable()) {
                    getWeatherData(city);
                } else {
                    Toast.makeText(this, "Lütfen internet bağlantınızı kontrol edin!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Lütfen bir şehir adı girin", Toast.LENGTH_SHORT).show();
            }
        });

        // favorilere ekleme
        buttonAddToFavorites.setOnClickListener(v -> {
            String city = textViewCityName.getText().toString();
            if (!city.equals("Şehir") && !city.isEmpty()) {
                boolean isInserted = dbHelper.addFavorite(city);
                if (isInserted) {
                    Toast.makeText(MainActivity.this, city + " favorilere eklendi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Bu şehir zaten favorilerde.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Önce bir şehir aratın.", Toast.LENGTH_SHORT).show();
            }
        });

        //activity geçiş
        buttonViewFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("CITY_NAME")) {
            String city = intent.getStringExtra("CITY_NAME");
            if (city != null && !city.isEmpty()) {
                getWeatherData(city);
            }
        }
    }

    // internet bağlantısı kontrol ediliyor
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getWeatherData(String city) {
        // API URL
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric&lang=tr";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        String temp = Math.round(main.getDouble("temp")) + "°C";
                        String cityName = response.getString("name");
                        
                        JSONObject weatherArray = response.getJSONArray("weather").getJSONObject(0);
                        String description = weatherArray.getString("description");
                        String iconCode = weatherArray.getString("icon");

                        textViewCityName.setText(cityName);
                        textViewTemp.setText(temp);
                        textViewDescription.setText(description.substring(0, 1).toUpperCase() + description.substring(1));

                        // ikon ekleme
                        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                        Picasso.get().load(iconUrl).placeholder(R.mipmap.ic_launcher).into(imageViewWeatherIcon);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Veri işlenirken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Şehir bulunamadı veya bağlantı hatası", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }
}
