package com.example.sushigo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameActivity extends AppCompatActivity {

    boolean activityRunning = false;
    //81dp y 126dp para una pantalla 534ppp (Pixel XL)
    int widthCard = 283;
    int heightCard = 441;

    int duration = 1000;

    String idGame = null;
    String username = null;
    int sala = 0;
    int numPlayers = 0;

    ArrayList<Card> listaPlayer3 = new ArrayList<Card>();

    Handler handler = new Handler();

    String url = "http://82.158.149.91:3000";

    ArrayList<Card> cartasPlayer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        activityRunning = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }, 500);
    }

    @Override
    protected void onStop() {
        activityRunning = false;
        super.onStop();
    }

    public void buttonPress(View view){
        if(idGame != null) {
            final ImageView baraja = findViewById(R.id.baraja);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://82.158.149.91:3000/playercards";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray cartasPlayerJSON = new JSONArray(response);
                                for(int i = 0; i < cartasPlayerJSON.length(); i++){
                                    Card c1 = new Card((int)cartasPlayerJSON.get(i),null, true, false);
                                    cartasPlayer.add(c1);
                                    genCardImage(c1, baraja);
                                }
                                redrawAll();

                            } catch (JSONException e) {
                                showErrorMessage("Error en el JSON");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    showErrorMessage(error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("idgame", idGame);

                    return params;
                }
            };

            queue.add(stringRequest);
        }
    }

    public void startGame(){
        ImageView carta1 = findViewById(R.id.cartaPlayer);
        widthCard = carta1.getWidth();
        heightCard = carta1.getHeight();
        username = getIntent().getStringExtra("username");
        SharedPreferences sp = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.apply();

        sala = getIntent().getIntExtra("sala", 0);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = null;
        String urlstart = url;
        if(sala == 0){
            urlstart += "/crearsala";
        }else{
            urlstart += "/unirsala";
        }
        stringRequest = new StringRequest(Request.Method.POST, urlstart,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datajson = new JSONObject(response);
                            idGame = datajson.getString("idgame");
                            sala = datajson.getInt("sala");
                            if(sala == 0){
                                showErrorMessage(datajson.getString("error"));
                                //Devolver al menu principal?
                                return;
                            }
                            numPlayers = datajson.getInt("numplayers");
                            //drawplayers
                            recursiveWaitForStart();
                        } catch (JSONException e) {
                            showErrorMessage("Error en el JSON, startGame()");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                if(sala != 0){
                    params.put("sala", String.valueOf(sala));
                }
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void recursiveWaitForStart(){
        if(activityRunning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = null;
                    stringRequest = new StringRequest(Request.Method.POST, url + "/waitstart",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject datajson = new JSONObject(response);
                                        Log.d("JSON:", response);
                                        if (datajson.getInt("numplayers") != numPlayers) {
                                            TextView textPlayers = findViewById(R.id.textPlayers);
                                            JSONArray playersJSON = new JSONArray(datajson.getString("arrayplayers"));
                                            String players = "";
                                            for(int i = 0; i < playersJSON.length(); i++){
                                                players += (String)playersJSON.get(i) + "\n";
                                            }
                                            textPlayers.setText(players);
                                            numPlayers = datajson.getInt("numplayers");
                                        }
                                        if (datajson.getString("start").equals("yes")) {
                                            //startgame de verdad
                                        } else {
                                            recursiveWaitForStart(); //volvemos a intentarlo dentro de duration
                                        }
                                    } catch (JSONException e) {
                                        showErrorMessage("Error en el JSON, recursiveWaitForStart()\n" + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showErrorMessage(error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("username", username);
                            params.put("sala", String.valueOf(sala));
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }
            }, duration);
        }
    }

    public void showErrorMessage(String error){
        View errorLayout = findViewById(R.id.errorLayout);
        TextView errorText = findViewById(R.id.errorText);

        errorLayout.setVisibility(View.VISIBLE);
        errorText.setText(error);
    }

    public void removeError(View view){
        findViewById(R.id.errorLayout).setVisibility(View.GONE);
    }

    public void redrawAll(){
        ImageView playerTemplate = findViewById(R.id.cartaPlayer);
        Card c = null;
        int x = 0;
        for(int i = 0; i < cartasPlayer.size(); i++){
            c = cartasPlayer.get(i);
            float mitad = (cartasPlayer.size()-1)/2f;
            x = (int)(playerTemplate.getX() + widthCard*(i-mitad));
            moveCard(c, x, playerTemplate.getY(), false, true, duration);
        }
    }

    public void genCardImage(Card card, ImageView slot){
        ImageView newCardImage = new ImageView(getApplicationContext());
        if(card.isFlip()){
            newCardImage.setImageResource(R.drawable.sushi_back);
        }else{
            newCardImage.setImageResource(card.getImageId());
        }

        newCardImage.setId(View.generateViewId());

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(widthCard,heightCard);
        newCardImage.setLayoutParams(lp);
        newCardImage.setX(slot.getX());
        newCardImage.setY(slot.getY());
        ((ConstraintLayout)findViewById(R.id.game_layout)).addView(newCardImage);
        card.setImagen(newCardImage);
    }

    public void moveCard(Card card, float x, float y, Boolean toPila, Boolean withFlip, int duration){

        ConstraintLayout layout = findViewById(R.id.game_layout);
        ImageView oldCardImage = card.getImagen();
        genCardImage(card, card.getImagen());
        layout.removeView(oldCardImage);
        card.getImagen().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        if(toPila){
            x +=  Math.random()*10;
            y +=  Math.random()*10;
        }
        ObjectAnimator animX = ObjectAnimator.ofFloat(card.getImagen(), "x", x);
        ObjectAnimator animY = ObjectAnimator.ofFloat(card.getImagen(), "y", y);
        ObjectAnimator animRot = ObjectAnimator.ofFloat(card.getImagen(), "rotation", 0, (float)(Math.random()-0.5)*10);
        AnimatorSet animSetXY = new AnimatorSet();
        if(toPila){
            animSetXY.playTogether(animX, animY, animRot);
        }else{
            animSetXY.playTogether(animX, animY);
        }
        animSetXY.setDuration(duration);
        animSetXY.start();
        if(withFlip) {
            flipCard(card, duration);
        }
    }

    public void flipCard(Card card, int duration){
        final Card cardCopy = card;
        int startRot = 0;
        int endRot = -180;

        if(card.isFlip()){
            startRot = -180;
            endRot = 0;
        }

        ObjectAnimator anim = ObjectAnimator.ofFloat(card.getImagen(), "rotationY", startRot, endRot);
        anim.setDuration(duration);
        anim.start();
        card.getImagen().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(cardCopy.isFlip()){
                    cardCopy.getImagen().setImageResource(cardCopy.getImageId());
                }else{
                    cardCopy.getImagen().setImageResource(R.drawable.sushi_back); //Cambiar esto cuando tengas todas las imagenes de cartas
                }
                cardCopy.setFlip(!cardCopy.isFlip());

            }
        }, duration/2);

    }

    public void addCardPlayer3(View view){
        listaPlayer3.add(new Card(1, null, true, false));
        drawPlayer3(listaPlayer3);
    }

    public void drawPlayer3(ArrayList<Card> listaCards){
        ImageView image1 = findViewById(R.id.cartaRivalP3);
        int width = image1.getWidth();
        int height = image1.getHeight();
        float x = image1.getX();
        float y = image1.getY();

        int numcards = listaCards.size();
        for(int i = 0; i < numcards; i++){
            float xcard;
            float ycard;
            int cardsinrow = 4;
            if( (i/4) == (numcards / 4)){
                cardsinrow = numcards % 4;
            }
            xcard = x + (i / 4)*width;
            ycard = y + height*((i % 4)*2 - (cardsinrow-1))/2f;
            Card card = listaCards.get(i);
            ImageView newCardImage;

            if(card.getImagen() == null){
                newCardImage = new ImageView(getApplicationContext());
            }else{
                newCardImage = card.getImagen();
            }

            if(card.isFlip()){
                newCardImage.setImageResource(R.drawable.sushi_back_ccw);
            }else{
                newCardImage.setImageResource(card.getImageId());
            }

            newCardImage.setId(View.generateViewId());

            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(width,height);
            newCardImage.setLayoutParams(lp);
            newCardImage.setX(xcard);
            newCardImage.setY(ycard);
            if(card.getImagen() == null) {
                ((ConstraintLayout) findViewById(R.id.game_layout)).addView(newCardImage);
                card.setImagen(newCardImage);
            }
        }
    }

}