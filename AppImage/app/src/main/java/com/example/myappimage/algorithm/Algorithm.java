package com.example.myappimage.algorithm;

import android.graphics.Bitmap;

/**
 * Algorithm Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public abstract class Algorithm {
    private Bitmap bitmap;

    public Algorithm(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int[] getPixels() {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    public int[] getPixels(Bitmap bitmap) {
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
