package com.example.sushigo;

import android.widget.ImageView;

import androidx.annotation.NonNull;


public class Card {
    private int id;
    private int tipo = -1;
    private String nombre = "????";
    private ImageView imagen = null;
    private boolean isFlip;
    private boolean isSelected;
    private int imageId = 0;
    private int imageFoodId = 0;
    private String color;
    private int clase = 0;

    public Card(int id, ImageView imagen, boolean isFlip, boolean isSelected) {
        this.id = id;
        this.imagen = imagen;
        this.isFlip = isFlip;
        this.isSelected = isSelected;
        genTipoYNombre();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ImageView getImagen() {
        return imagen;
    }

    public void setImagen(ImageView imagen) {
        this.imagen = imagen;
    }

    public boolean isFlip() {
        return isFlip;
    }

    public void setFlip(boolean flip) {
        isFlip = flip;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getImageFoodId() {
        return imageFoodId;
    }

    public void setImageFoodId(int imageFoodId) {
        this.imageFoodId = imageFoodId;
    }

    public int getClase() {
        return clase;
    }

    public void setClase(int clase) {
        this.clase = clase;
    }

    public void genTipoYNombre(){
        if(id == 0){
            this.tipo = 0;
            this.clase = 0;
            this.nombre = "Desconocido";
            this.imageId = R.drawable.sushi_go_template;
            this.imageFoodId = R.drawable.sushi_food_maki1;
            this.color = "#FFFFFF";
        }else if(id >= 1 && id <= 14){
            this.tipo = 1;
            this.clase = 1;
            this.nombre = "Tempura";
            this.imageId = R.drawable.sushi_tempura;
            this.imageFoodId = R.drawable.sushi_food_tempura;
            this.color = "#ca9eb8";
        }else if(id <= 28){
            this.tipo = 2;
            this.clase = 2;
            this.nombre = "Sashimi";
            this.imageId = R.drawable.sushi_sashimi;
            this.imageFoodId = R.drawable.sushi_food_sashimi;
            this.color = "#cfcd03";
        }else if(id <= 42){
            this.tipo = 3;
            this.clase = 3;
            this.nombre = "Gyoza";
            this.imageId = R.drawable.sushi_gyoza;
            this.imageFoodId = R.drawable.sushi_food_gyoza;
            this.color = "#2672bb";
        }else if(id <= 48){
            this.tipo = 4;
            this.clase = 4;
            this.nombre = "Maki";
            this.imageId = R.drawable.sushi_maki1;
            this.imageFoodId = R.drawable.sushi_food_maki1;
            this.color = "#ba130a";
        }else if(id <= 60){
            this.tipo = 5;
            this.clase = 4;
            this.nombre = "Maki";
            this.imageId = R.drawable.sushi_maki2;
            this.imageFoodId = R.drawable.sushi_food_maki2;
            this.color = "#ba130a";
        }else if(id <= 68){
            this.tipo = 6;
            this.clase = 4;
            this.nombre = "Maki";
            this.imageId = R.drawable.sushi_maki3;
            this.imageFoodId = R.drawable.sushi_food_maki3;
            this.color = "#ba130a";
        }else if(id <= 73){
            this.tipo = 7;
            this.clase = 5;
            this.nombre = "Nigiri Tortilla";
            this.imageId = R.drawable.sushi_nigiri1;
            this.imageFoodId = R.drawable.sushi_food_nigiri1;
            this.color = "#f2c007";
        }else if(id <= 83){
            this.tipo = 8;
            this.clase = 5;
            this.nombre = "Nigiri Salmón";
            this.imageId = R.drawable.sushi_nigiri2;
            this.imageFoodId = R.drawable.sushi_food_nigiri2;
            this.color = "#f2c007";
        }else if(id <= 88){
            this.tipo = 9;
            this.clase = 5;
            this.nombre = "Nigiri Calamar";
            this.imageId = R.drawable.sushi_nigiri3;
            this.imageFoodId = R.drawable.sushi_food_nigiri3;
            this.color = "#f2c007";
        }else if(id <= 98){
            this.tipo = 10;
            this.clase = 6;
            this.nombre = "Pudin";
            this.imageId = R.drawable.sushi_pudin;
            this.imageFoodId = R.drawable.sushi_food_pudin;
            this.color = "#e0aba2";
        }else if(id <= 104){
            this.tipo = 11;
            this.clase = 7;
            this.nombre = "Wasabi";
            this.imageId = R.drawable.sushi_wasabi;
            this.imageFoodId = R.drawable.sushi_food_wasabi;
            this.color = "#f2c007";
        }else if(id <= 108){
            this.tipo = 12;
            this.clase = 8;
            this.nombre = "Palillos";
            this.imageId = R.drawable.sushi_palillos;
            this.imageFoodId = R.drawable.sushi_food_palillos;
            this.color = "#94cab9";
        }else{
            this.tipo = -1;
            this.clase = -1;
            this.nombre = "Error";
            this.imageId = R.drawable.sushi_go_template;
            this.imageFoodId = R.drawable.sushi_food_maki1;
            this.color = "#FFFFFF";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return nombre + " (" + id + ")";
    }
}
