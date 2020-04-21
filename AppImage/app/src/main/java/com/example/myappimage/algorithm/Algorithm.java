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

    /**
     * Replace one pixel color with a transparent color.
     *
     * @param bitmap the bitmap to transform
     * @param color  the color to delete
     * @return the new bitmap
     */
    public Bitmap createTransparentBitmapFromBitmap(Bitmap bitmap, int color) {
        if (bitmap != null) {
            int[] pixels = getPixels(bitmap);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] == color)
                    pixels[i] = Color.TRANSPARENT;
            }

            bitmap = Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            return bitmap;
        }
        return null;
    }

    public abstract int[] toGray();

    public abstract int[] colorize(int color);

    public abstract int[] keepColor(int firstColor, int secondColor, boolean interval);

    public abstract int[] brightnessModification(int brightness);

    public abstract int[] contrastModification(double multiplier);

    public abstract int[] dynamicExpansion();

    public abstract int[] histogramEqualization();

    public abstract int[] blurConvolution(int filterType, int size);

    public abstract int[] sobelFilterConvolution();

    public abstract int[] laplacienFilterConvolution();

    public abstract int[] cartoonEffect();

    public abstract int[] snowEffect();
}
