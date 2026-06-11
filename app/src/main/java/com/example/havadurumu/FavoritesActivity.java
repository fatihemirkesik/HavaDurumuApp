package com.example.havadurumu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ListView listViewFavorites;
    private DatabaseHelper dbHelper;
    private List<String> favoriteCities;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        listViewFavorites = findViewById(R.id.listViewFavorites);
        dbHelper = new DatabaseHelper(this);

        loadFavorites();

        // Şehre tıklandığında ana ekrana dön ve hava durumunu göster
        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = favoriteCities.get(position);
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            intent.putExtra("CITY_NAME", selectedCity);
            // MainActivity zaten çalışıyor olabilir, üzerine yeni bir tane açmak yerine mevcudu kullanmak için flag eklenebilir
            // Ancak basitlik adına direkt başlatıyoruz. MainActivity'de kontrol edeceğiz.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Uzun basınca silme özelliği
        listViewFavorites.setOnItemLongClickListener((parent, view, position, id) -> {
            String cityToDelete = favoriteCities.get(position);
            dbHelper.deleteFavorite(cityToDelete);
            Toast.makeText(this, cityToDelete + " silindi", Toast.LENGTH_SHORT).show();
            loadFavorites(); // Listeyi yenile
            return true;
        });
    }

    private void loadFavorites() {
        favoriteCities = dbHelper.getAllFavorites();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteCities);
        listViewFavorites.setAdapter(adapter);
    }
}