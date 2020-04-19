package com.example.myappimage.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Algorithm Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public abstract class Algorithm {
    private Bitmap bitmap;
    private Context context;

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

    public int[] getPixels(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    public int[] getRed() {
        int[] red = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            red[i] = Color.red(pixels[i]);
        }
        return red;
    }

    public int[] getGreen() {
        int[] green = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            green[i] = Color.green(pixels[i]);
        }
        return green;
    }

    public int[] getBlue() {
        int[] blue = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            blue[i] = Color.blue(pixels[i]);
        }
        return blue;
    }

    public int[] getAlpha() {
        int[] alpha = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            alpha[i] = Color.alpha(pixels[i]);
        }
        return alpha;
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
