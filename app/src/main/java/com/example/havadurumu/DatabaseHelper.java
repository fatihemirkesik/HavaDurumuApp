package com.example.havadurumu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeatherDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CITY_NAME = "city_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tablo oluşturma sorgusu (City name benzersiz olacak şekilde ayarlandı)
        String createTable = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY_NAME + " TEXT UNIQUE)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    // Şehir Ekleme Metodu
    public boolean addFavorite(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Şehri her zaman düzgün formatta kaydetmek için trim yapıyoruz
        values.put(COLUMN_CITY_NAME, cityName.trim());
        
        // insert metodu eğer UNIQUE kısıtlaması ihlal edilirse -1 döner
        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    // Tüm Favorileri Getirme Metodu
    public List<String> getAllFavorites() {
        List<String> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);

        if (cursor.moveToFirst()) {
            do {
                favorites.add(cursor.getString(1)); // 1. kolon city_name
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favorites;
    }

    // Şehir Silme Metodu
    public void deleteFavorite(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_CITY_NAME + "=?", new String[]{cityName});
    }
}