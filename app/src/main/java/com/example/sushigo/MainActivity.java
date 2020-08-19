package com.example.sushigo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    int duration = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText usernameBox =  findViewById(R.id.usernameBox);
        String username = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE).getString("username", "");
        usernameBox.setText(username);
        if(usernameBox.getText().length() == 0){
            Button crearSala = findViewById(R.id.buttonCrearSala);
            crearSala.setEnabled(false);
        }
        Button unirseSala = findViewById(R.id.buttonUnirse);
        unirseSala.setEnabled(false);

        usernameBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.salaBox)).getText().length() != 0){
                    Button unirseSala = findViewById(R.id.buttonUnirse);
                    unirseSala.setEnabled(count != 0);
                }
                Button crearSala = findViewById(R.id.buttonCrearSala);
                crearSala.setEnabled(count != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText salaBox = findViewById(R.id.salaBox);
        salaBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.usernameBox)).getText().length() != 0){
                    Button unirseSala = findViewById(R.id.buttonUnirse);
                    unirseSala.setEnabled(count != 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        final View usernameLayout = findViewById(R.id.usernameLayout);
        final View salaLayout = findViewById(R.id.salaLayout);
        final View crearSalaLayout = findViewById(R.id.crearSalaLayout);
        usernameLayout.setAlpha(0);
        salaLayout.setAlpha(0);
        crearSalaLayout.setAlpha(0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation(usernameLayout, duration);
                startAnimation(salaLayout, duration);
                startAnimation(crearSalaLayout, duration);
            }
        }, duration);


    }

    public void startAnimation(View view, int duration){
        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(view, "alpha", 1);
        animAlpha.setDuration(duration);
        animAlpha.start();
    }

    public void crearSala(View view){
        EditText usernameBox =  findViewById(R.id.usernameBox);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("username", usernameBox.getText().toString());
        startActivity(intent);
    }

    public void unirseASala(View view){
        EditText usernameBox =  findViewById(R.id.usernameBox);
        EditText salaBox =  findViewById(R.id.salaBox);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("username", usernameBox.getText().toString());
        intent.putExtra("sala", Integer.parseInt(salaBox.getText().toString()));
        startActivity(intent);
    }
}