package com.example.sushigo;

import android.widget.ImageView;


public class Card {
    private int id;
    private int tipo = -1;
    private String nombre = "????";
    private ImageView imagen = null;
    private boolean isFlip;
    private boolean isSelected;
    private int imageId = 0;

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

    public void genTipoYNombre(){
        if(id <= 14){
            this.tipo = 1;
            this.nombre = "Tempura";
            this.imageId = R.drawable.sushi_tempura;
        }else if(id <= 28){
            this.tipo = 2;
            this.nombre = "Sashimi";
            this.imageId = R.drawable.sushi_sashimi;
        }else if(id <= 42){
            this.tipo = 3;
            this.nombre = "Gyoza";
            this.imageId = R.drawable.sushi_gyoza;
        }else if(id <= 48){
            this.tipo = 4;
            this.nombre = "Maki1";
            this.imageId = R.drawable.sushi_maki1;
        }else if(id <= 60){
            this.tipo = 5;
            this.nombre = "Maki2";
            this.imageId = R.drawable.sushi_maki2;
        }else if(id <= 68){
            this.tipo = 6;
            this.nombre = "Maki3";
            this.imageId = R.drawable.sushi_maki3;
        }else if(id <= 73){
            this.tipo = 7;
            this.nombre = "Nigiri1";
            this.imageId = R.drawable.sushi_nigiri1;
        }else if(id <= 83){
            this.tipo = 8;
            this.nombre = "Nigiri2";
            this.imageId = R.drawable.sushi_nigiri2;
        }else if(id <= 88){
            this.tipo = 9;
            this.nombre = "Nigiri3";
            this.imageId = R.drawable.sushi_nigiri3;
        }else if(id <= 98){
            this.tipo = 10;
            this.nombre = "Pudin";
            this.imageId = R.drawable.sushi_pudin;
        }else if(id <= 104){
            this.tipo = 11;
            this.nombre = "Wasabi";
            this.imageId = R.drawable.sushi_wasabi;
        }else if(id <= 108){
            this.tipo = 12;
            this.nombre = "Palillos";
            this.imageId = R.drawable.sushi_palillos;
        }else{
            this.tipo = 0;
            this.nombre = "Error";
            this.imageId = R.drawable.sushi_go_template;
        }
    }
}
