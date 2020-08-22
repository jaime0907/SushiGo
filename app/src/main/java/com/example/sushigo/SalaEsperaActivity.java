package com.example.sushigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SalaEsperaActivity extends AppCompatActivity {

    Handler handler = new Handler();

    String url = "http://82.158.149.91:3000";

    int duration = 1000;

    String idGame = null;
    String username = null;
    int sala = 0;
    int numPlayers = 0;

    boolean activityRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_espera);

        activityRunning = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                joinSala();
            }
        }, 500);
    }

    @Override
    protected void onStop() {
        activityRunning = false;
        super.onStop();
    }

    public void joinSala(){
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
                            ((TextView)findViewById(R.id.textSala)).setText("Sala nº " + sala);
                            numPlayers = datajson.getInt("numplayers");
                            JSONArray playersJSON = new JSONArray(datajson.getString("arrayplayers"));
                            drawPlayers(playersJSON);
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
                                            JSONArray playersJSON = new JSONArray(datajson.getString("arrayplayers"));
                                            drawPlayers(playersJSON);
                                            numPlayers = datajson.getInt("numplayers");
                                            Button button = findViewById(R.id.buttonStartGame);
                                            if(datajson.getInt("numplayers") > 1 && datajson.getInt("isLeader") == 1){
                                                button.setVisibility(View.VISIBLE);
                                            }else{
                                                button.setVisibility(View.GONE);
                                            }
                                        }
                                        if (datajson.getString("start").equals("yes")) {
                                            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                                            intent.putExtra("username", username);
                                            intent.putExtra("sala", sala);
                                            intent.putExtra("idgame", idGame);
                                            startActivity(intent);
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

    public void drawPlayers(JSONArray playersJSON) throws JSONException {
        for(int i = 1; i <= playersJSON.length(); i++){
            TextView textView = findViewById(getResources().getIdentifier("textP" + i, "id", getPackageName()));
            textView.setText((String)playersJSON.get(i-1));
        }
    }

    public void sendStartGame(View view){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.POST, url + "/startgame",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Nada xd
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
                params.put("idgame", idGame);
                return params;
            }
        };
        queue.add(stringRequest);
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

}