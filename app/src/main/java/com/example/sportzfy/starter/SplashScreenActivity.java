package com.example.sportzfy.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

import com.example.sportzfy.R;

public class SplashScreenActivity extends AppCompatActivity {

    WebView webView;
    private static final long SPLASH_DELAY = 5000; // Delay of 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript if required

        // Load the HTML file from assets folder
        webView.loadUrl("file:///android_asset/index.html");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}