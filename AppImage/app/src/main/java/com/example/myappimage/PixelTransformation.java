package com.example.myappimage;

import android.graphics.Color;

/**
 * PixelTransformation Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class PixelTransformation {

    /**
     * Transform a pixel into a pixel which will be in gray scale.
     *
     * @param pixel the pixel to transform
     * @return the gray pixel
     */
    public static double pixelToGray(int pixel) {
        return (0.3 * Color.red(pixel) + 0.59 * Color.green(pixel) + 0.11 * Color.blue(pixel));
    }

    /**
     * Recoded function to transform rgb values into hsv values.
     *
     * @param red   the red value of the pixel [0-255]
     * @param green the green value of the pixel [0-255]
     * @param blue  the blue value of the pixel [0-255]
     * @return an array which contain the hue, the saturation and the value of the pixel
     */
    public static float[] myRgbToHsv(float red, float green, float blue) {
        float r = red / 255;
        float g = green / 255;
        float b = blue / 255;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float d = max - min;
        float h, s, v;

        if (max == min)
            h = 0;
        else if (max == r)
            h = (60 * ((g - b) / d) + 360) % 360;
        else if (max == g)
            h = (60 * ((b - r) / d) + 120) % 360;
        else
            h = (60 * ((r - g) / d) + 240) % 360;

        if (max == 0)
            s = 0;
        else
            s = (d / max) * 100;

        v = max * 100;

        return new float[]{h, s, v};
    }

    /**
     * Recoded function to transform hsv values into rgb values.
     *
     * @param h the hue of the pixel [0-360]
     * @param s the saturation of the pixel [0-1]
     * @param v the value of the pixel [0-1]
     * @return an array which contain the red value, the green value and the blue value of the pixel
     */
    public static float[] myHsvToRgb(float h, float s, float v) {
        float c = s * v;
        float newH = h / 60;
        float x = c * (1 - Math.abs((newH % 2) - 1));
        float r, g, b;

        if (0 <= newH && newH < 1) {
            r = c;
            g = x;
            b = 0;
        } else if (1 <= newH && newH < 2) {
            r = x;
            g = c;
            b = 0;
        } else if (2 <= newH && newH < 3) {
            r = 0;
            g = c;
            b = x;
        } else if (3 <= newH && newH < 4) {
            r = 0;
            g = x;
            b = c;
        } else if (4 <= newH && newH < 5) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        float m = v - c;
        return new float[]{(r + m) * 255, (g + m) * 255, (b + m) * 255};
    }
    
}
