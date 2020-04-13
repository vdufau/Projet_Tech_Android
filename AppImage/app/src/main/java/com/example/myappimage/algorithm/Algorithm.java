package com.example.myappimage.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Algorithm Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public abstract class Algorithm {
    private Bitmap bitmap;
    private Context context;

    public Algorithm(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Algorithm(Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public Context getContext() {
        return context;
    }

    public int[] getPixels() {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    /**
     * Calculate the correct interval for the keepColor algorithm.
     *
     * @param h       the first hue chosen by the user
     * @param secondH the second hue chosen by the user
     * @return an array with the left and right intervals
     */
    public int[] keepColorInteval(int h, int secondH) {
        if (h < secondH)
            return new int[]{h, secondH};
        else
            return new int[]{secondH, h};
    }
}
