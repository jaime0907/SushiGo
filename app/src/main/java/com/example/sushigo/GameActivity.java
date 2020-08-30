package com.example.sushigo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    boolean activityRunning = false;
    //81dp y 126dp para una pantalla 534ppp (Pixel XL)
    int widthCard = 283;
    int heightCard = 441;

    int duration = 1000;
    int durationRecursive = 1000;

    String idGame = null;
    String username = null;
    int sala = 0;
    int numPlayers = 0;
    int numPlayer = 0;
    int turno = 0;

    float escalaSel = 1.5f;

    HashMap<Integer, ArrayList<Card>> mapManos = new HashMap<>();

    Handler handler = new Handler();
    RequestQueue queue;

    String url = "https://sushigo-backend-jaime.herokuapp.com";

    HashMap<Integer, HashMap<Integer, String>> mapPos = new HashMap<>();

    boolean hayPalillos = false;
    boolean hasAlreadyPlayed = false;

    int countdownDuration = 30;

    HashMap<String, CountDownTimer> mapTimer = new HashMap<>();

    HashMap<Integer, ArrayList<ArrayList<Card>>> mapTablero = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> mapSlotsTablero = new HashMap<>();
    HashMap<Integer, Boolean> mapHasPlayed = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> mapWasabisLibres = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        url = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE).getString("url", "https://sushigo-backend-jaime.herokuapp.com");

        HashMap<Integer, String> mapPos2 = new HashMap<Integer, String>();
        mapPos2.put(1, "S");
        mapPos2.put(2, "N");

        HashMap<Integer, String> mapPos3 = new HashMap<Integer, String>();
        mapPos3.put(1, "S");
        mapPos3.put(2, "N1");
        mapPos3.put(3, "N2");

        HashMap<Integer, String> mapPos4 = new HashMap<Integer, String>();
        mapPos4.put(1, "S");
        mapPos4.put(2, "W");
        mapPos4.put(3, "N");
        mapPos4.put(4, "E");

        HashMap<Integer, String> mapPos5 = new HashMap<Integer, String>();
        mapPos5.put(1, "S");
        mapPos5.put(2, "W");
        mapPos5.put(3, "N1");
        mapPos5.put(4, "N2");
        mapPos5.put(5, "E");

        mapPos.put(2, mapPos2);
        mapPos.put(3, mapPos3);
        mapPos.put(4, mapPos4);
        mapPos.put(5, mapPos5);

        mapManos.put(1, new ArrayList<Card>());
        mapManos.put(2, new ArrayList<Card>());
        mapManos.put(3, new ArrayList<Card>());
        mapManos.put(4, new ArrayList<Card>());
        mapManos.put(5, new ArrayList<Card>());

        mapTimer.put("N", new CountDownTimer(countdownDuration*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ProgressBar pb = findViewById(R.id.timerN);
                pb.setProgress(pb.getProgress() - 1);
            }

            @Override
            public void onFinish() {

            }
        });
        mapTimer.put("N1", new CountDownTimer(countdownDuration*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ProgressBar pb = findViewById(R.id.timerN1);
                pb.setProgress(pb.getProgress() - 1);
            }

            @Override
            public void onFinish() {

            }
        });
        mapTimer.put("N2", new CountDownTimer(countdownDuration*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ProgressBar pb = findViewById(R.id.timerN2);
                pb.setProgress(pb.getProgress() - 1);
            }

            @Override
            public void onFinish() {

            }
        });
        mapTimer.put("S", new CountDownTimer(countdownDuration*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ProgressBar pb = findViewById(R.id.timerS);
                pb.setProgress(pb.getProgress() - 1);
            }

            @Override
            public void onFinish() {

            }
        });
        mapTimer.put("E", new CountDownTimer(countdownDuration*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ProgressBar pb = findViewById(R.id.timerE);
                pb.setProgress(pb.getProgress() - 1);
            }

            @Override
            public void onFinish() {

            }
        });
        mapTimer.put("W", new CountDownTimer(countdownDuration*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ProgressBar pb = findViewById(R.id.timerW);
                pb.setProgress(pb.getProgress() - 1);
            }

            @Override
            public void onFinish() {

            }
        });

        mapTablero.put(1, new ArrayList<ArrayList<Card>>());
        mapTablero.put(2, new ArrayList<ArrayList<Card>>());
        mapTablero.put(3, new ArrayList<ArrayList<Card>>());
        mapTablero.put(4, new ArrayList<ArrayList<Card>>());
        mapTablero.put(5, new ArrayList<ArrayList<Card>>());

        mapSlotsTablero.put(1, new HashMap<Integer, Integer>());
        mapSlotsTablero.put(2, new HashMap<Integer, Integer>());
        mapSlotsTablero.put(3, new HashMap<Integer, Integer>());
        mapSlotsTablero.put(4, new HashMap<Integer, Integer>());
        mapSlotsTablero.put(5, new HashMap<Integer, Integer>());

        mapWasabisLibres.put(1, new ArrayList<Integer>());
        mapWasabisLibres.put(2, new ArrayList<Integer>());
        mapWasabisLibres.put(3, new ArrayList<Integer>());
        mapWasabisLibres.put(4, new ArrayList<Integer>());
        mapWasabisLibres.put(5, new ArrayList<Integer>());

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

    public void startGame(){
        ImageView carta1 = findViewById(R.id.manoS);
        queue = Volley.newRequestQueue(getApplicationContext());
        widthCard = carta1.getWidth();
        heightCard = carta1.getHeight();
        username = getIntent().getStringExtra("username");
        sala = getIntent().getIntExtra("sala", 0);
        idGame = getIntent().getStringExtra("idgame");


        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.POST, url + "/initgame",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datajson = new JSONObject(response);
                            numPlayers = datajson.getInt("numplayers");
                            numPlayer = datajson.getInt("numplayer");
                            turno = datajson.getInt("turno");
                            drawPlayers(datajson.getJSONArray("arrayplayers"));
                            mapManos.put(1,JSONCardsToList(datajson.getJSONArray("cartas")));

                            ImageView baraja = findViewById(R.id.baraja);
                            for(int player = 1; player <= numPlayers; player++){
                                for(int i = 0; i < mapManos.get(1).size(); i++){
                                    if(player != 1){
                                        Card c1 = new Card(0, null, true, false);
                                        genCardImage(c1, baraja);
                                        mapManos.get(player).add(c1);
                                    }else{
                                        genCardImage(mapManos.get(1).get(i), baraja);
                                    }
                                }
                                startTimer(mapPos.get(numPlayers).get(player), player==1);
                                mapHasPlayed.put(player, false);
                            }
                            redrawManos(true);
                            recursiveWaitForTurno();
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
                params.put("sala", String.valueOf(sala));
                params.put("idgame", idGame);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public int getRelativePlayerNum(int player){
        int playerRelativeNum = (player - numPlayer + 1 + numPlayers) % numPlayers;
        if(playerRelativeNum == 0){
            playerRelativeNum = numPlayers;
        }
        return playerRelativeNum;
    }

    public void drawPlayers(JSONArray arrayPlayers) throws JSONException {
        findViewById(getIdView("cardN") ).setVisibility(View.INVISIBLE);
        findViewById(getIdView("cardN1") ).setVisibility(View.INVISIBLE);
        findViewById(getIdView("cardN2") ).setVisibility(View.INVISIBLE);
        findViewById(getIdView("cardW")).setVisibility(View.INVISIBLE);
        findViewById(getIdView("cardE")).setVisibility(View.INVISIBLE);
        for(int i = 0; i < arrayPlayers.length(); i++){
            JSONObject jsonPlayer = arrayPlayers.getJSONObject(i);
            String username = jsonPlayer.getString("username");
            int num = jsonPlayer.getInt("num");
            if(num == numPlayer){
                continue;
            }
            int playerRelativeNum = getRelativePlayerNum(num);
            Log.d("numPlayers", String.valueOf(numPlayers));
            Log.d("num", String.valueOf(num));
            Log.d("relativeNum", String.valueOf(playerRelativeNum));
            Log.d("mapPos", mapPos.get(numPlayers).get(playerRelativeNum));
            findViewById(getIdView("card" + mapPos.get(numPlayers).get(playerRelativeNum))).setVisibility(View.VISIBLE);
            TextView textView = findViewById(getIdView("username" + mapPos.get(numPlayers).get(playerRelativeNum)));
            textView.setText(username);
        }
    }

    public void drawPlayerCards(){
        ImageView playerTemplate = findViewById(R.id.manoS);
        ArrayList<Card> listCards = mapManos.get(1);
        for(int i = 0; i < listCards.size(); i++){
            moveMano(listCards.get(i), playerTemplate, "S", "S", i, listCards.size());
        }
    }

    public ArrayList<Card> JSONCardsToList(JSONArray jsonarray) throws JSONException {
        ArrayList<Card> listCard = new ArrayList<>();
        for(int i = 0; i < jsonarray.length(); i++){
            int idcard = jsonarray.getInt(i);
            listCard.add(new Card(idcard, null, false, false));
        }
        return listCard;
    }

    public void recursiveWaitForStart(){
        if(activityRunning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StringRequest stringRequest = null;
                    stringRequest = new StringRequest(Request.Method.POST, url + "/waitstart",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject datajson = new JSONObject(response);
                                        if (datajson.getInt("numplayers") != numPlayers) {
                                            JSONArray playersJSON = new JSONArray(datajson.getString("arrayplayers"));
                                            String players = "";
                                            for(int i = 0; i < playersJSON.length(); i++){
                                                players += (String)playersJSON.get(i) + "\n";
                                            }
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

    public void regenCardImage(Card card){
        ImageView newCardImage = new ImageView(getApplicationContext());
        ImageView oldCardImage = card.getImagen();

        newCardImage.setId(View.generateViewId());
        if(card.isFlip()){
            newCardImage.setImageResource(R.drawable.sushi_back);
        }else{
            newCardImage.setImageResource(card.getImageId());
        }
        newCardImage.setLayoutParams(oldCardImage.getLayoutParams());
        newCardImage.setPivotX(oldCardImage.getPivotX());
        newCardImage.setPivotY(oldCardImage.getPivotY());
        newCardImage.setScaleX(oldCardImage.getScaleX());
        newCardImage.setScaleY(oldCardImage.getScaleY());
        newCardImage.setRotation(oldCardImage.getRotation());
        newCardImage.setX(oldCardImage.getX());
        newCardImage.setY(oldCardImage.getY());
        ConstraintLayout layout = findViewById(R.id.game_layout);
        layout.addView(newCardImage);
        card.setImagen(newCardImage);
        layout.removeView(oldCardImage);
    }

    public void moveCard(Card card, float x, float y, Boolean withFlip, int duration, boolean toN, boolean withRegen){
        if(withRegen) {
            regenCardImage(card);
        }
        card.getImagen().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        ObjectAnimator animX = ObjectAnimator.ofFloat(card.getImagen(), "x", x);
        ObjectAnimator animY = ObjectAnimator.ofFloat(card.getImagen(), "y", y);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.setDuration(duration);
        animSetXY.start();
        if(withFlip) {
            flipCard(card, duration, toN);
        }
    }

    public void flipCard(Card card, final int duration, final boolean toN){
        final Card cardCopy = card;
        int startRot = 0;
        int endRot = -90;

        if(card.isFlip()){
            startRot = 0;
            endRot = 90;
        }

        ObjectAnimator anim = ObjectAnimator.ofFloat(card.getImagen(), "rotationY", startRot, endRot);
        anim.setDuration(duration/2);
        anim.start();
        card.getImagen().postDelayed(new Runnable(){
            @Override
            public void run() {

                int startRot = 90;
                int endRot = 0;

                if(cardCopy.isFlip()){
                    startRot = -90;
                    endRot = 0;
                }
                if(toN){
                    cardCopy.getImagen().setRotation(0);
                }

                if(cardCopy.isFlip()){
                    cardCopy.getImagen().setImageResource(cardCopy.getImageId());
                }else{
                    cardCopy.getImagen().setImageResource(R.drawable.sushi_back);
                }
                cardCopy.setFlip(!cardCopy.isFlip());


                ObjectAnimator anim = ObjectAnimator.ofFloat(cardCopy.getImagen(), "rotationY", startRot, endRot);
                anim.setDuration(duration/2);
                anim.start();

            }
        }, duration/2);

    }

    public void regenCardPlayerForTablero(Card card){
        Card cardOrigen = card;
        ImageView origen = cardOrigen.getImagen();
        ImageView newCardImage = new ImageView(getApplicationContext());
        if (card.isFlip()) {
            newCardImage.setImageResource(R.drawable.sushi_back);
        } else {
            newCardImage.setImageResource(cardOrigen.getImageId());
        }

        newCardImage.setId(View.generateViewId());


        ImageView destino = findViewById(R.id.tableroS);

        float multiplicador = 1f;



        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(destino.getWidth(), destino.getHeight());
        newCardImage.setLayoutParams(lp);
        //newCardImage.setX(origen.getX() + origen.getWidth() / 2f - destino.getWidth() / 2f);
        //newCardImage.setY(origen.getY() + origen.getHeight() / 2f - destino.getHeight() / 2f);

        //newCardImage.setX(origen.getX() + ((origen.getWidth()/2f)*(1 - escalaSel)) + origen.getWidth()/2f);
        //newCardImage.setY(origen.getY() + (origen.getHeight()*(1f - escalaSel)) + origen.getHeight()/2f);

        newCardImage.setX(origen.getX() + origen.getWidth()/2f - destino.getWidth()/2f);
        newCardImage.setY(origen.getY() + (origen.getHeight()/2f)*(2f - escalaSel) - destino.getHeight()/2f);

        //newCardImage.setPivotX(0);
        //newCardImage.setPivotY(0);

        newCardImage.setScaleX(scaleCard("S", "N")*escalaSel);
        newCardImage.setScaleY(scaleCard("S", "N")*escalaSel);

        ConstraintLayout layout = findViewById(R.id.game_layout);
        layout.addView(newCardImage);



        layout.removeView(origen);
        cardOrigen.setImagen(newCardImage);
    }

    public void addCardPlayer3(View view){
        for(int player = 1; player <= numPlayers; player++) {
            int ncard = 0;
            final Card card = mapManos.get(player).get(ncard);
            mapManos.get(player).remove(ncard);
            if (player == 1) {
                regenCardPlayerForTablero(card);
            }
            addCardToTablero(player, card);
            drawTableroPlayer(player);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("Card X", String.valueOf(card.getImagen().getX()));
                    Log.d("Slot X", String.valueOf(((ImageView) findViewById(R.id.tableroS)).getX()));
                    Log.d("Card Y", String.valueOf(card.getImagen().getY()));
                    Log.d("Slot Y", String.valueOf(((ImageView) findViewById(R.id.tableroS)).getY()));
                    Log.d("Card Width", String.valueOf(card.getImagen().getWidth()));
                    Log.d("Slot Width", String.valueOf(((ImageView) findViewById(R.id.tableroS)).getWidth()));
                    Log.d("Card Height", String.valueOf(card.getImagen().getHeight()));
                    Log.d("Slot Height", String.valueOf(((ImageView) findViewById(R.id.tableroS)).getHeight()));
                }
            }, 2 * duration);
        }
    }

    public void moveManos(ArrayList<Card> playerCards){
        for (int i = 0; i < mapManos.get(1).size(); i++) {
            flipCard(mapManos.get(1).get(i), duration/2, false);
        }
        if(playerCards.size() == mapManos.get(numPlayers).size()){
            for(int i = 0; i < playerCards.size(); i++) {
                mapManos.get(numPlayers).get(i).setId(playerCards.get(i).getId());
                mapManos.get(numPlayers).get(i).genTipoYNombre();
            }
        }else{
            showErrorMessage("El número de cartas en la mano del ultimo jugador y las del JSON no coinciden");
        }
        ArrayList<Card> aux;
        aux = (ArrayList<Card>) mapManos.get(1).clone();
        for(int player = numPlayers; player >= 2; player--){
            int nextPlayer = player + 1;
            if(player == numPlayers){
                nextPlayer = 1;
            }
            mapManos.put(nextPlayer, (ArrayList<Card>) mapManos.get(player).clone());
        }
        mapManos.put(2, (ArrayList<Card>) aux.clone());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redrawManos(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < mapManos.get(1).size(); i++) {
                            flipCard(mapManos.get(1).get(i), duration / 2, false);
                        }
                    }
                }, duration);
            }
        }, duration/2);

    }

    public void redrawManos(boolean isReparto){
        for(int player = 1; player <= numPlayers; player++) {
            ArrayList<Card> listCards = mapManos.get(player);
            int prevPlayer = player - 1;
            if(player == 1){
                prevPlayer = numPlayers;
            }
            ImageView dest = findViewById(getIdView("mano" + mapPos.get(numPlayers).get(player)));
            for (int i = 0; i < listCards.size(); i++) {
                Card card = listCards.get(i);
                String posOri = mapPos.get(numPlayers).get(prevPlayer);
                if(isReparto){
                    posOri = "S";
                }
                moveMano(card, dest, posOri, mapPos.get(numPlayers).get(player), i, listCards.size());
            }
        }
    }

    public float scaleCard(String posOri, String posDest){
        switch(posOri){
            case "S":
                if(posDest.equals("N")){
                    return 5f/3f; //1.666
                }else if(posDest.equals("S")){
                    return 1f;
                }else{
                    return 2.5f;
                }
            case "N":
            case "N1":
            case "N2":
                if(posDest.equals("E")){
                    return escalaSel;
                }else if(posDest.equals("S")){
                    return 0.6f;
                }else{
                    return 1f;
                }
            case "W":
                return 1f;
            case "E":
                return 0.6f;
        }
        return 1f;
    }

    public int rotateCard(String posOri){
        switch(posOri){
            case "S":
                return 0;
            case "N":
            case "N1":
            case "N2":
                return 180;
            case "W":
                return -90;
            case "E":
                return 90;
        }
        return 0;
    }

    public void moveMano(Card cardOrigen, ImageView destino, String posOri, String posDest, int ncard, int totalcards){
        ImageView origen = cardOrigen.getImagen();
        ImageView newCardImage = new ImageView(getApplicationContext());
        if(cardOrigen.isFlip()){
            newCardImage.setImageResource(R.drawable.sushi_back);
        }else{
            newCardImage.setImageResource(cardOrigen.getImageId());
        }

        newCardImage.setId(View.generateViewId());

        newCardImage.setScaleX(scaleCard(posOri, posDest));
        newCardImage.setScaleY(scaleCard(posOri, posDest));
        newCardImage.setRotation(rotateCard(posOri));
        float rotacion = 0;
        float escala = 1;

        float x = 0;
        float y = 0;
        switch(posDest){
            case "S":
                rotacion = 0;
                if(cardOrigen.isSelected()){
                    escala = 1.2f;
                }else{
                    escala = 1f;
                }
                x = destino.getWidth()*(ncard-((totalcards-1)/2f));
                newCardImage.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        selectCard(v);
                    }
                });
                break;
            case "N":
            case "N1":
            case "N2":
                rotacion = -180;
                escala = 1f;
                x = destino.getWidth()*(ncard-((totalcards-1)/2f))*0.1f;
                break;
            case "W":
                rotacion = -90;
                escala = escalaSel;
                y = destino.getHeight()*(ncard-((totalcards-1)/2f))*0.1f;
                break;
            case "E":
                rotacion = 90;
                escala = escalaSel;
                y = destino.getHeight()*(ncard-((totalcards-1)/2f))*0.1f;
                break;
        }


        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(destino.getWidth(),destino.getHeight());
        newCardImage.setLayoutParams(lp);
        newCardImage.setX(origen.getX() + origen.getWidth()/2f - destino.getWidth()/2f);
        newCardImage.setY(origen.getY() + origen.getHeight()/2f - destino.getHeight()/2f);

        ConstraintLayout layout = findViewById(R.id.game_layout);
        layout.removeView(origen);
        cardOrigen.setImagen(newCardImage);

        ((ConstraintLayout)findViewById(R.id.game_layout)).addView(newCardImage);
        ObjectAnimator animX = ObjectAnimator.ofFloat(newCardImage, "x", destino.getX() + x);
        ObjectAnimator animY = ObjectAnimator.ofFloat(newCardImage, "y", destino.getY() + y);
        ObjectAnimator animRot = ObjectAnimator.ofFloat(newCardImage, "rotation", rotacion);
        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(newCardImage, "scaleX", escala);
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(newCardImage, "scaleY", escala);
        AnimatorSet animSetXY = new AnimatorSet();

        animSetXY.playTogether(animX, animY, animRot, animScaleX, animScaleY);
        animSetXY.setDuration(duration);
        animSetXY.start();
    }

    public boolean pilaNotFull(ArrayList<Card> pila, int clase, boolean nigiriAWasabi){
        int ncards = pila.size();
        switch(clase){
            case 1:
                return ncards < 2;
            case 2:
                return ncards < 3;
            case 3: case 6:
                return true;
            case 7:
                if(nigiriAWasabi){
                    return ncards < 2;
                }else{
                    return ncards < 1;
            }
            default:
                return ncards < 4;

        }
    }

    public void addNigiriConWasabi(int player, Card card){
        if(mapWasabisLibres.get(player).size() > 0){
            mapTablero.get(player).get(mapWasabisLibres.get(player).get(0)).add(card);
            mapWasabisLibres.get(player).remove(0);
        }else{
            showErrorMessage("No hay wasabis libres");
        }
    }

    public void addCardToTablero(int player, Card card){
        String pos = mapPos.get(numPlayers).get(player);
        ArrayList<ArrayList<Card>> tablero = mapTablero.get(player);
        HashMap<Integer, Integer> slotsTablero = mapSlotsTablero.get(player);
        if(slotsTablero.containsKey(card.getClase())){
            ArrayList<Card> pila = tablero.get(slotsTablero.get(card.getClase()));
            if(pilaNotFull(pila, card.getClase(), false)){
                pila.add(card);
            }else{
                ArrayList<Card> newLista = new ArrayList<Card>();
                newLista.add(card);
                tablero.add(newLista);
                slotsTablero.put(card.getClase(), tablero.size()-1);
            }
        }else{
            ArrayList<Card> newLista = new ArrayList<Card>();
            newLista.add(card);
            tablero.add(newLista);
            slotsTablero.put(card.getClase(), tablero.size()-1);
        }
        if(card.getClase() == 7){
            mapWasabisLibres.get(player).add(tablero.size()-1);
        }
    }

    public void drawTableroPlayer(int player){
        Log.d("drawTableroPlayer()", "yes");
        ArrayList<ArrayList<Card>> listaPilas = mapTablero.get(player);
        String posDest = mapPos.get(numPlayers).get(player);
        ImageView destino = findViewById(getIdView("tablero" + posDest));
        int width = destino.getWidth();
        int height = destino.getHeight();
        float xslot = destino.getX();
        float yslot = destino.getY();
        int maxPilasInRow = 4;
        float porcentajeApliado = 0.165f;

        switch (posDest) {
            case "S":
            case "N":
                maxPilasInRow = 10;
                break;
            case "N1":
            case "N2":
                maxPilasInRow = 7;
                break;
            case "W":
            case "E":
                maxPilasInRow = 4;
                break;
        }

        int totalpilas = listaPilas.size();
        for(int i = 0; i < totalpilas; i++){
            int nrow = (i / maxPilasInRow);
            for(int j = 0; j < listaPilas.get(i).size(); j++) {
                float xcard = 0;
                float ycard = 0;

                float desplazamientoLastRow = 0;

                float apilado = j * porcentajeApliado;

                if(nrow != 0){
                    int max = 1;
                    for(int a = (nrow-1)*maxPilasInRow; a < nrow*maxPilasInRow; a++){
                        if(listaPilas.get(a).size() > max){
                            max = listaPilas.get(a).size();
                        }
                    }
                    desplazamientoLastRow = porcentajeApliado * (max - 1);
                }
                float desplazamientoCurrentRow = 0;
                if(posDest.equals("S")){
                    int max = 1;
                    for(int a = 0; a < listaPilas.size(); a++){
                        if(listaPilas.get(a).size() > max){
                            max = listaPilas.get(a).size();
                        }
                    }
                    desplazamientoCurrentRow = porcentajeApliado * (max - 1);
                }

                int pilasInRow = maxPilasInRow;

                if (nrow == (totalpilas / maxPilasInRow)) {
                    pilasInRow = totalpilas % maxPilasInRow;
                }

                switch (posDest) {
                    case "S":
                        ycard = yslot + (nrow - desplazamientoCurrentRow + apilado) * height;
                        xcard = xslot + width * ((i % maxPilasInRow) * 2 - (pilasInRow - 1)) / 2f;
                        break;
                    case "N":
                    case "N1":
                    case "N2":
                        ycard = yslot + (nrow + desplazamientoLastRow + apilado) * height;
                        xcard = xslot + width * ((i % maxPilasInRow) * 2 - (pilasInRow - 1)) / 2f;
                        break;
                    case "W":
                        xcard = xslot + (nrow + desplazamientoLastRow + apilado) * width;
                        ycard = yslot + height * ((i % maxPilasInRow) * 2 - (pilasInRow - 1)) / 2f;
                        break;
                    case "E":
                        xcard = xslot - (nrow + desplazamientoLastRow + apilado) * width;
                        ycard = yslot + height * ((i % maxPilasInRow) * 2 - (pilasInRow - 1)) / 2f;
                        break;
                }

                Card card = listaPilas.get(i).get(j);

                if (card.getImagen() == null) {
                    ImageView newCardImage;
                    newCardImage = new ImageView(getApplicationContext());
                    if (card.isFlip()) {
                        newCardImage.setImageResource(R.drawable.sushi_back);
                    } else {
                        newCardImage.setImageResource(card.getImageId());
                    }

                    newCardImage.setId(View.generateViewId());

                    ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(width, height);
                    newCardImage.setLayoutParams(lp);
                    newCardImage.setX(xcard);
                    newCardImage.setY(ycard);
                    ((ConstraintLayout) findViewById(R.id.game_layout)).addView(newCardImage);
                    card.setImagen(newCardImage);
                }

                boolean toN = false;
                toN = posDest.equals("N") || posDest.equals("N1") || posDest.equals("N2");
                boolean withFlip = card.isFlip();
                if(card.getClase() == 0){
                    withFlip = false;
                }
                //Log.d("i", String.valueOf(i));
                //Log.d("j", String.valueOf(j));
                //Log.d("withFlip", String.valueOf(withFlip));
                //Log.d("card", String.valueOf(card.getId()));
                moveCard(card, xcard, ycard, withFlip, duration, toN, player != 1);

                if (posDest.equals("S")) {
                    ObjectAnimator animScaleX = ObjectAnimator.ofFloat(card.getImagen(), "scaleX", 1f);
                    ObjectAnimator animScaleY = ObjectAnimator.ofFloat(card.getImagen(), "scaleY", 1f);
                    AnimatorSet animSetXY = new AnimatorSet();

                    animSetXY.playTogether(animScaleX, animScaleY);
                    animSetXY.setDuration(duration);
                    animSetXY.start();
                }
            }
        }
    }

    public void selectCard(View view){
        if(hasAlreadyPlayed){
            return; //si ya ha jugado carta, que el selectCard no tenga efecto para mantener la selCard hasta que termine el turno
        }
        float escala = escalaSel;
        boolean noButtons = false;
        Card selCard = null;
        for(Card card : mapManos.get(1)){
            if(card.getImagen() == view){
                selCard = card;
                break;
            }
        }

        if(selCard != null){
            for(Card card : mapManos.get(1)){ //excepto en caso de palillos, hacer en el futuro
                if(card == selCard){
                    if(!card.isSelected()) {
                        selCard.setSelected(true);
                        card.getImagen().setElevation(1);
                        escala = escalaSel;
                    }else{
                        selCard.setSelected(false);
                        escala = 1f;
                        noButtons = true;
                        card.getImagen().setElevation(0);
                    }
                    card.getImagen().setPivotY(card.getImagen().getHeight());
                    card.getImagen().setPivotX(card.getImagen().getWidth()/2f);
                    ObjectAnimator animScaleX = ObjectAnimator.ofFloat(card.getImagen(), "scaleX", escala);
                    ObjectAnimator animScaleY = ObjectAnimator.ofFloat(card.getImagen(), "scaleY", escala);
                    AnimatorSet animSetXY = new AnimatorSet();
                    animSetXY.playTogether(animScaleX, animScaleY);
                    animSetXY.setDuration(duration/4);
                    animSetXY.start();
                }else if(card.isSelected()){
                    card.getImagen().setElevation(0);
                    card.getImagen().setPivotY(card.getImagen().getHeight());
                    card.getImagen().setPivotX(card.getImagen().getWidth()/2f);
                    card.setSelected(false);
                    escala = 1f;
                    ObjectAnimator animScaleX = ObjectAnimator.ofFloat(card.getImagen(), "scaleX", escala);
                    ObjectAnimator animScaleY = ObjectAnimator.ofFloat(card.getImagen(), "scaleY", escala);
                    AnimatorSet animSetXY = new AnimatorSet();
                    animSetXY.playTogether(animScaleX, animScaleY);
                    animSetXY.setDuration(duration/4);
                    animSetXY.start();
                }
            }
            makeButtons(selCard, noButtons);
        }
    }

    public boolean hayWasabiLibre(){
        return mapWasabisLibres.get(1).size() > 0;
    }

    public void makeButtons(final Card selCard, boolean noButtons){
        clearButtons();
        if(!noButtons) {
            if (hayPalillos) {
                //something
            } else {
                switch (selCard.getTipo()) {
                    case 7:
                    case 8:
                    case 9:
                        if (hayWasabiLibre()) {
                            CardView conWasabiCard = newButton("Jugar con Wasabi", selCard.getImageFoodId(), selCard.getColor(), false);
                            conWasabiCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playCard(selCard, true);
                                }
                            });
                            CardView sinWasabiCard = newButton("Jugar sin Wasabi", selCard.getImageFoodId(), selCard.getColor(), false);
                            sinWasabiCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playCard(selCard, false);
                                }
                            });
                            break;
                        }
                    default:
                        String texto = "Jugar " + selCard.getNombre();
                        int idImagen = selCard.getImageFoodId(); //Cambiar cuando estén todos los renders
                        CardView playCard = newButton(texto, idImagen, selCard.getColor(), true);
                        playCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                playCard(selCard, false);
                            }
                        });
                        break;
                }
            }
        }
    }

    public int dptopx(int dp){
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    public void clearButtons(){
        LinearLayout layoutButtons = findViewById(R.id.layoutButtons);
        layoutButtons.removeAllViews();
    }

    public CardView newButton(String texto, int imageId, String color, boolean isOnlyOneButton){
        LinearLayout layoutButtons = findViewById(R.id.layoutButtons);

        CardView cardView = new CardView(getApplicationContext());
        cardView.setId(View.generateViewId());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        int dp = 4;

        lp.setMargins(dptopx(dp), 0, dptopx(dp), 0);
        cardView.setLayoutParams(lp);
        cardView.setCardBackgroundColor(Color.parseColor(color));
        cardView.setRadius(dptopx(8));
        cardView.setElevation(dptopx(8));

        LinearLayout linearLayoutCardView = new LinearLayout(getApplicationContext());
        linearLayoutCardView.setId(View.generateViewId());
        LinearLayout.LayoutParams lpLayoutCard = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        TextView newText = new TextView(getApplicationContext());
        newText.setId(View.generateViewId());
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView newImage = new ImageView(getApplicationContext());
        newImage.setId(View.generateViewId());
        LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(isOnlyOneButton){
            linearLayoutCardView.setOrientation(LinearLayout.HORIZONTAL);
            lpText = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            lpImage = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        }else{
            linearLayoutCardView.setOrientation(LinearLayout.VERTICAL);
        }
        linearLayoutCardView.setLayoutParams(lpLayoutCard);

        lpText.gravity = Gravity.CENTER;
        lpText.setMargins(dptopx(8), dptopx(4), dptopx(8), dptopx(4));
        newText.setLayoutParams(lpText);
        newText.setText(texto);
        newText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        lpImage.gravity = Gravity.CENTER;
        newImage.setLayoutParams(lpImage);
        newImage.setImageResource(imageId);

        linearLayoutCardView.addView(newText);
        linearLayoutCardView.addView(newImage);

        cardView.addView(linearLayoutCardView);

        layoutButtons.addView(cardView);
        return cardView;
    }

    public void playCard(final Card card, final boolean withWasabi){
        hasAlreadyPlayed = true;

        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.POST, url + "/playcard",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datajson = new JSONObject(response);
                            String status = datajson.getString("status");
                            if(status.equals("ok")){
                                card.setClase(0);
                                regenCardPlayerForTablero(card);
                                addCardToTablero(1, card);
                                mapManos.get(1).remove(mapManos.get(1).indexOf(card));
                                drawTableroPlayer(1);
                                flipCard(card, duration, false);

                                clearButtons();
                                stopTimer("S");
                                for(int i = 2; i <= numPlayers; i++){
                                    if(!mapHasPlayed.get(i)) {
                                        findViewById(getIdView("timer" + mapPos.get(numPlayers).get(i))).setVisibility(View.VISIBLE);
                                    }
                                }
                            }else{
                                hasAlreadyPlayed = false;
                                showErrorMessage(status);
                            }

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
                params.put("sala", String.valueOf(sala));
                params.put("idgame", idGame);
                params.put("card", String.valueOf(card.getId()));
                params.put("turno", String.valueOf(turno));
                if(withWasabi) {
                    params.put("withWasabi", "yes");
                }else{
                    params.put("withWasabi", "no");
                }
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void recursiveWaitForTurno(){
        if(activityRunning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    StringRequest stringRequest = null;
                    stringRequest = new StringRequest(Request.Method.POST, url + "/waitturno",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject datajson = new JSONObject(response);
                                        JSONArray playersFin = datajson.getJSONArray("playersFin");
                                        for(int i = 0; i < playersFin.length(); i++){
                                            int playerRel = getRelativePlayerNum(playersFin.getInt(i));
                                            if(playerRel == 1){
                                                continue;
                                            }
                                            if(!mapHasPlayed.get(playerRel)){
                                                mapHasPlayed.put(playerRel, true);
                                                mapManos.get(playerRel).get(0).setClase(0);
                                                addCardToTablero(playerRel, mapManos.get(playerRel).get(0));
                                                mapManos.get(playerRel).remove(0);

                                                drawTableroPlayer(playerRel);
                                                findViewById(getIdView("timer" + mapPos.get(numPlayers).get(playerRel))).setVisibility(View.INVISIBLE);
                                            }
                                        }
                                        if (datajson.getString("endTurn").equals("yes")) {
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    nextTurn();
                                                }
                                            }, duration);

                                        } else {
                                            recursiveWaitForTurno(); //volvemos a intentarlo dentro de durationRecursive
                                        }
                                    } catch (JSONException e) {
                                        showErrorMessage("Error en el JSON, recursiveWaitForTurno()\n" + e.getMessage());
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
                            params.put("idgame", idGame);
                            params.put("turno", String.valueOf(turno));
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }
            }, durationRecursive);
        }
    }

    public void startTimer(String pos, boolean isVisible){

        ProgressBar pb = findViewById(getIdView("timer" + pos));
        if(isVisible) {
            pb.setVisibility(View.VISIBLE);
        }else{
            pb.setVisibility(View.INVISIBLE);
        }
        pb.setMax(countdownDuration);
        pb.setProgress(countdownDuration);
        mapTimer.get(pos).start();
    }
    public void stopTimer(String pos){
        findViewById(getIdView("timer" + pos)).setVisibility(View.INVISIBLE);
        mapTimer.get(pos).cancel();
    }

    public int getIdView(String name){
        return getResources().getIdentifier(name, "id", getPackageName());
    }

    public void nextTurn(){
        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.POST, url + "/nextturno",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datajson = new JSONObject(response);
                            turno = datajson.getInt("turno");
                            JSONArray infoPlayers = datajson.getJSONArray("infoPlayers");
                            for(int i = 0; i < infoPlayers.length(); i++){
                                JSONObject info = infoPlayers.getJSONObject(i);
                                int player = info.getInt("player");
                                int relativePlayer = getRelativePlayerNum(player);
                                int cardPlayed = info.getInt("cardPlayed");
                                String withWasabiString = info.getString("withWasabi");
                                boolean withWasabi = withWasabiString.equals("yes");
                                JSONArray tablero = info.getJSONArray("tablero");
                                descubrirCarta(relativePlayer, cardPlayed, withWasabi);
                            }
                            JSONArray cartasjson = datajson.getJSONArray("cartas");
                            final ArrayList<Card> cartasPlayer = JSONCardsToList(cartasjson);
                            for(Card c : cartasPlayer){
                                c.setFlip(true);
                            }

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i = 1; i <= numPlayers; i++){
                                        startTimer(mapPos.get(numPlayers).get(i), i == 1);
                                        mapHasPlayed.put(i, false);
                                    }
                                    hasAlreadyPlayed = false;
                                    moveManos(cartasPlayer);
                                    recursiveWaitForTurno();
                                }
                            }, duration);
                        } catch (JSONException e) {
                            showErrorMessage("Error en el JSON, nextTurn()\n" + e.getStackTrace());
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
                params.put("sala", String.valueOf(sala));
                params.put("idgame", idGame);
                params.put("turno", String.valueOf(turno));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void descubrirCarta(int player, int cardPlayedId, boolean withWasabi) {
        Card card = new Card(cardPlayedId, null, true, false);

        ArrayList<ArrayList<Card>> tablero = mapTablero.get(player);
        HashMap<Integer, Integer> slotsTablero = mapSlotsTablero.get(player);
        if (slotsTablero.containsKey(0)) {
            ArrayList<Card> pila = tablero.get(slotsTablero.get(0));
            card = pila.get(0);
            card.setId(cardPlayedId);
            card.genTipoYNombre();
            int index = slotsTablero.get(0);
            mapTablero.get(player).remove(index);
            mapSlotsTablero.get(player).remove(0);
        } else {
            ImageView slot = findViewById(getIdView("tablero" + mapPos.get(numPlayers).get(player)));
            genCardImage(card, slot);
        }
        if (withWasabi) {
            addNigiriConWasabi(player, card);
        } else {
            addCardToTablero(player, card);
        }
        drawTableroPlayer(player);
    }
}