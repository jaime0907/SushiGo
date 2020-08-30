package com.example.sushigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

public class AjustesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE).getString("url", "https://sushigo-backend-jaime.herokuapp.com");
                ((EditText)findViewById(R.id.urlBox)).setText(url);
            }
        }, 50);
    }

    public void guardarAjustes(View view){
        EditText urlBox = findViewById(R.id.urlBox);
        String url = urlBox.getText().toString();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("url", url);
        editor.apply();
    }
}