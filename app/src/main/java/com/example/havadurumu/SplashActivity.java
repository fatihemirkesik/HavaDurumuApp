package com.example.havadurumu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

 public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //ekranının tasarım dosyası ayarlanır
        setContentView(R.layout.activity_splash);
        
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // splash ekranından ana ekrana geçiş yapmak için Intent oluşturulur
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                
                startActivity(intent);
                
                finish();
            }
        }, 2500);
    }
}
