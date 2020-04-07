package com.example.myappimage;

import android.graphics.Bitmap;

/**
 * BitmapSingleton Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class BitmapSingleton {
    private Bitmap bitmap = null;
    private static final BitmapSingleton instance = new BitmapSingleton();

    public BitmapSingleton() {
    }

    public static BitmapSingleton getInstance() {
        return instance;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
