package com.example.myappimage;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * ConvolutionMatrix Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_L3
 */
public class ConvolutionMatrix {
    private int size;
    private double[][] matrix;

    public ConvolutionMatrix(int size) {
        this.size = size;
        this.matrix = new double[size][size];
    }

    /**
     * Fill the matrix with the same value.
     *
     * @param value the value to fill the matrix
     */
    public void setMatrix(double value) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = value;
            }
        }
    }

    /**
     * Fill the matrix with values.
     *
     * @param values the values to fill the matrix
     */
    public void setMatrix(double[][] values) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = values[i][j];
            }
        }
    }

    /**
     * Apply the matrix to each pixel of the bitmap.
     *
     * TODO Essayer de faire avec un tableau d'entier et pas une couleur pour le pixel et faire le traitement de transformation en pixel plus tard (ou un delire dans le style)
     *
     * @param bitmap the bitmap to transform
     * @return an array which contain the new pixels values
     */
    public int[] applyConvolution(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        int[] newPixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        bitmap.getPixels(newPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = size / 2; y < height - size / 2; y++) {
            for (int x = size / 2; x < width - size / 2; x++) {

                int red = 0, green = 0, blue = 0;
                for (int i = -(size / 2); i <= size / 2; i++) {
                    for (int j = -(size / 2); j <= size / 2; j++) {
                        int pixel = pixels[(y + i) * width + x + j];
                        red += Color.red(pixel) * matrix[i + size / 2][j + size / 2];
                        green += Color.green(pixel) * matrix[i + size / 2][j + size / 2];
                        blue += Color.blue(pixel) * matrix[i + size / 2][j + size / 2];
                    }
                }

                if (red < 0) red = 0;
                if (red > 255) red = 255;
                if (green < 0) green = 0;
                if (green > 255) green = 255;
                if (blue < 0) blue = 0;
                if (blue > 255) blue = 255;

                newPixels[y * width + x] = Color.argb(Color.alpha(pixels[y * width + x]), red, green, blue);
            }
        }

        return newPixels;
    }
}
