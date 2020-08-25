package com.example.sushigo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    String idGame = null;
    String username = null;
    int sala = 0;
    int numPlayers = 0;
    int numPlayer = 0;

    ArrayList<Card> listPlayerCards = new ArrayList<>();

    HashMap<Integer, ArrayList<Card>> mapManos = new HashMap<>();

    ArrayList<Card> listaPlayer3 = new ArrayList<Card>();

    Handler handler = new Handler();

    String url = "http://82.158.149.91:3000";

    ArrayList<Card> cartasPlayer = new ArrayList<>();

    HashMap<Integer, HashMap<Integer, String>> mapPos = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        HashMap<Integer, String> mapPos2 = new HashMap<Integer, String>();
        mapPos2.put(1, "S");
        mapPos2.put(2, "N");

        HashMap<Integer, String> mapPos3 = new HashMap<Integer, String>();
        mapPos3.put(1, "S");
        mapPos3.put(2, "W");
        mapPos3.put(3, "E");

        HashMap<Integer, String> mapPos4 = new HashMap<Integer, String>();
        mapPos4.put(1, "S");
        mapPos4.put(2, "W");
        mapPos4.put(3, "N");
        mapPos4.put(4, "E");

        //Falta 5 players, con N1 y N2.

        mapPos.put(2, mapPos2);
        mapPos.put(3, mapPos3);
        mapPos.put(4, mapPos4);

        mapManos.put(1, new ArrayList<Card>());
        mapManos.put(2, new ArrayList<Card>());
        mapManos.put(3, new ArrayList<Card>());
        mapManos.put(4, new ArrayList<Card>());
        mapManos.put(5, new ArrayList<Card>());

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
        widthCard = carta1.getWidth();
        heightCard = carta1.getHeight();
        username = getIntent().getStringExtra("username");
        sala = getIntent().getIntExtra("sala", 0);
        idGame = getIntent().getStringExtra("idgame");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.POST, url + "/initgame",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datajson = new JSONObject(response);
                            numPlayers = datajson.getInt("numplayers");
                            numPlayer = datajson.getInt("numplayer");
                            drawPlayers(datajson.getJSONArray("arrayplayers"));
                            listPlayerCards = JSONCardsToList(datajson.getJSONArray("cartas"));
                            drawPlayerCards();

                            ImageView baraja = findViewById(R.id.baraja);
                            for(int player = 1; player <= numPlayers; player++){
                                for(int i = 0; i < 10; i++){
                                    Card c1 = new Card((int)(Math.floor(Math.random()*108 + 1)), null, player != 1, false);
                                    genCardImage(c1, baraja);
                                    mapManos.get(player).add(c1);
                                }
                            }
                            moveManos(true);

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

    public void drawPlayers(JSONArray arrayPlayers) throws JSONException {

        findViewById(getResources().getIdentifier("cardN", "id", getPackageName())).setVisibility(View.INVISIBLE);
        findViewById(getResources().getIdentifier("cardW", "id", getPackageName())).setVisibility(View.INVISIBLE);
        findViewById(getResources().getIdentifier("cardE", "id", getPackageName())).setVisibility(View.INVISIBLE);
        //Añadir N1 y N2 cuando toque
        for(int i = 0; i < arrayPlayers.length(); i++){
            JSONObject jsonPlayer = arrayPlayers.getJSONObject(i);
            String username = jsonPlayer.getString("username");
            int num = jsonPlayer.getInt("num");
            if(num == numPlayer){
                continue;
            }
            int playerRelativeNum = (num - numPlayer + 1 + numPlayers) % numPlayers;
            if(playerRelativeNum == 0){
                playerRelativeNum = numPlayers;
            }
            findViewById(getResources().getIdentifier("card" + mapPos.get(numPlayers).get(playerRelativeNum), "id", getPackageName())).setVisibility(View.VISIBLE);
            TextView textView = findViewById(getResources().getIdentifier("username" + mapPos.get(numPlayers).get(playerRelativeNum), "id", getPackageName()));
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
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = null;
                    stringRequest = new StringRequest(Request.Method.POST, url + "/waitstart",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject datajson = new JSONObject(response);
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
        ImageView playerTemplate = findViewById(R.id.manoS);
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

    public void flipCard(Card card, final int duration){
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

    public void addCardPlayer3(View view){
        listaPlayer3.add(new Card(1, null, true, false));
        drawPlayer3(listaPlayer3);
    }

    public void buttonMoveCards(View view){

        for (int i = 0; i < 10; i++) {
            flipCard(mapManos.get(1).get(i), duration/2);
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
                moveManos(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            flipCard(mapManos.get(1).get(i), duration / 2);
                        }
                    }
                }, duration);
            }
        }, duration/2);

    }

    public void moveManos(boolean isReparto){
        for(int player = 1; player <= numPlayers; player++) {
            ArrayList<Card> listCards = mapManos.get(player);
            int prevPlayer = player - 1;
            if(player == 1){
                prevPlayer = numPlayers;
            }
            ImageView dest = findViewById(getResources().getIdentifier("mano" + mapPos.get(numPlayers).get(player), "id", getPackageName()));
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
                if(posDest.equals("E")){
                    return 1.5f;
                }else{
                    return 0.6f;
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
                rotacion = -180;
                escala = 1f;
                x = destino.getWidth()*(ncard-((totalcards-1)/2f))*0.1f;
                break;
            case "W":
                rotacion = -90;
                escala = 1.5f;
                y = destino.getHeight()*(ncard-((totalcards-1)/2f))*0.1f;
                break;
            case "E":
                rotacion = 90;
                escala = 1.5f;
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
                newCardImage.setImageResource(R.drawable.sushi_back_270);
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

    public void selectCard(View view){
        float escala = 1.5f;
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
                        ImageView oldImage = selCard.getImagen();
                        genCardImage(selCard, oldImage);
                        selCard.getImagen().setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                selectCard(v);
                            }
                        });
                        ((ConstraintLayout)findViewById(R.id.game_layout)).removeView(oldImage);

                        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(selCard.getImagen(), "scaleX", escala);
                        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(selCard.getImagen(), "scaleY", escala);
                        AnimatorSet animSetXY = new AnimatorSet();
                        animSetXY.playTogether(animScaleX, animScaleY);
                        animSetXY.setDuration(duration/4);
                        animSetXY.start();
                    }
                }else if(card.isSelected()){
                    card.setSelected(false);
                    ObjectAnimator animScaleX = ObjectAnimator.ofFloat(card.getImagen(), "scaleX", 1f);
                    ObjectAnimator animScaleY = ObjectAnimator.ofFloat(card.getImagen(), "scaleY", 1f);
                    AnimatorSet animSetXY = new AnimatorSet();
                    animSetXY.playTogether(animScaleX, animScaleY);
                    animSetXY.setDuration(duration/4);
                    animSetXY.start();
                }
            }
        }
    }
}