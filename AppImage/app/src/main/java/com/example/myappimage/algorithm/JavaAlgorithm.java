package com.example.myappimage.algorithm;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.example.myappimage.ConvolutionMatrix;

import static com.example.myappimage.PixelTransformation.*;

/**
 * JavaAlgorithm Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class JavaAlgorithm extends Algorithm {

    public JavaAlgorithm(Bitmap bitmap) {
        super(bitmap);
    }

    /**
     * @deprecated First version of image transformation to gray.
     * Worst version.
     */
    public void toGrayFirstVersion() {
        Bitmap bitmap = getBitmap();
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i, j);
                int pixelGray = (int) pixelToGray(pixel);
                int newPixel = Color.argb(Color.alpha(pixel), pixelGray, pixelGray, pixelGray);
                bitmap.setPixel(i, j, newPixel);
            }
        }
    }

    /**
     * Second version of image transformation to gray.
     * Better than the first.
     */
    public void toGraySecondVersion() {
        Bitmap bitmap = getBitmap();
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int pixelGray = (int) pixelToGray(pixels[i]);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Third version of image transformation to gray.
     * Better than the first.
     */
    public void toGrayThirdVersion() {
        ColorMatrix m = new ColorMatrix();
        m.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(m);
//        im.setColorFilter(filter);
    }

    /**
     * Colorize the bitmap with a hue.
     *
     * @param color the hue chosen by the user
     */
    public void colorize(int color) {
        Bitmap bitmap = getBitmap();
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            /**
             * Java functions
             */
//            float[] hsv = new float[3];
//            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
//            hsv[0] = color;
//            int newColor = Color.HSVToColor(hsv);
//            pixels[i] = newColor;

            /**
             * Recoded functions
             */
            float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
            hsv[0] = color;
            float[] rgb = myHsvToRgb(hsv[0], hsv[1] / 100, hsv[2] / 100);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), (int) rgb[0], (int) rgb[1], (int) rgb[2]);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Keep an interval of color of the bitmap and colorize the pixels which are not in this interval in gray scale.
     *
     * @param h       the first hue chosen by the user
     * @param secondH the second hue chosen by the user
     * @param inter   the parameter which determine if the colors to keep are between the two hues or not
     */
    public void keepColor(int h, int secondH, boolean inter) {
        Bitmap bitmap = getBitmap();
        int[] interval = keepColorInteval(h, secondH);

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            /**
             * Java functions
             */
//            float[] hsv = new float[3];
//            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);

            /**
             * Recoded functions
             */
            float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));

            if (inter) {
                if (hsv[0] < interval[0] || interval[1] < hsv[0]) {
                    int pixelGray = (int) pixelToGray(pixels[i]);
                    pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
                }
            } else {
                if (hsv[0] > interval[0] && interval[1] > hsv[0]) {
                    int pixelGray = (int) pixelToGray(pixels[i]);
                    pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
                }
            }
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Modifies the bitmap's brightness.
     *
     * @param value the brightness value chosen by the user (0-200)
     */
    public void changeBitmapBrightness(float value) {
        Bitmap bitmap = getBitmap();
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        value, 0, 0, 0, 1,
                        0, value, 0, 0, 1,
                        0, 0, value, 0, 1,
                        0, 0, 0, 1, 0
                });

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    /**
     * Extend the pixels values if possible.
     */
    public void dynamicExpansion() {
        Bitmap bitmap = getBitmap();
        int size = 101;
        int[] LUTValue = new int[size];
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int maxValue = 0, minValue = 100;

        for (int i = 0; i < size; i++) {
            LUTValue[i] = 0;
        }

        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
            if ((int) hsv[2] > maxValue)
                maxValue = (int) hsv[2];
            if ((int) hsv[2] < minValue)
                minValue = (int) hsv[2];
        }

        if (maxValue != 100 && minValue != 0 && maxValue - minValue != 0) {
            for (int i = 0; i < size; i++) {
                LUTValue[i] = 100 * (i - minValue) / (maxValue - minValue);
            }

            for (int i = 0; i < pixels.length; i++) {
                float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
                hsv[2] = LUTValue[(int) hsv[2]];
                float[] rgb = myHsvToRgb(hsv[0], hsv[1] / 100, hsv[2] / 100);
                pixels[i] = Color.argb(Color.alpha(pixels[i]), (int) rgb[0], (int) rgb[1], (int) rgb[2]);
            }

            bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    /**
     * Close the interval of pixels values.
     *
     * @param diminution the diminution asked by the user
     */
    public void contrastDiminution(int diminution) {
        Bitmap bitmap = getBitmap();
        int size = 101;
        int[] LUTValue = new int[size];
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int maxValue = 0, minValue = 100;

        for (int i = 0; i < size; i++) {
            LUTValue[i] = 0;
        }

        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
            if ((int) hsv[2] > maxValue)
                maxValue = (int) hsv[2];
            if ((int) hsv[2] < minValue)
                minValue = (int) hsv[2];
        }

        int maxValue2 = maxValue - diminution, minValue2 = minValue + diminution;

        if (maxValue2 > minValue2 && maxValue2 - minValue2 != 0) {
            for (int i = 0; i < size; i++) {
                LUTValue[i] = (i - minValue) * (maxValue2 - minValue2) / (maxValue - minValue) + minValue2;
            }

            for (int i = 0; i < pixels.length; i++) {
                float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
                hsv[2] = LUTValue[(int) hsv[2]];
                float[] rgb = myHsvToRgb(hsv[0], hsv[1] / 100, hsv[2] / 100);
                pixels[i] = Color.argb(Color.alpha(pixels[i]), (int) rgb[0], (int) rgb[1], (int) rgb[2]);
            }

            bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    /**
     * Equalize the pixels values.
     */
    public void histogramEqualization() {
        Bitmap bitmap = getBitmap();
        int size = 101;
        int[] histV = new int[size];
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < size; i++) {
            histV[i] = 0;
        }
        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
            histV[(int) hsv[2]]++;
        }

        int[] histVC = new int[size];
        histVC[0] = histV[0];
        for (int i = 1; i < size; i++) {
            histVC[i] = histVC[i - 1] + histV[i];
        }

        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
            hsv[2] = histVC[(int) hsv[2]] * 100 / pixels.length;
            float[] rgb = myHsvToRgb(hsv[0], hsv[1] / 100, hsv[2] / 100);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), (int) rgb[0], (int) rgb[1], (int) rgb[2]);
        }

        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Apply an average filter on the image.
     * It will blur the image.
     *
     * @param size the size of the kernel
     */
    public void averageFilterConvolution(int size) {
        Bitmap bitmap = getBitmap();
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(size);
        double moy = 1.0 / (size * size);
        convolutionMatrix.setMatrix(moy);
        int[] pixels = convolutionMatrix.applyConvolution(bitmap);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Apply a Gaussian filter on the image.
     * It will blur the image.
     */
    public void gaussianFilterConvolution(int size) {
        Bitmap bitmap = getBitmap();
        double[][] gauss;
        if (size == 3) {
            gauss = new double[][]{
                    {1.0, 2.0, 1.0},
                    {2.0, 4.0, 2.0},
                    {1.0, 2.0, 1.0}
            };
        } else {
            gauss = new double[][]{
                    {1.0, 2.0, 3.0, 2.0, 1.0},
                    {2.0, 6.0, 8.0, 6.0, 2.0},
                    {3.0, 8.0, 10.0, 8.0, 3.0},
                    {2.0, 6.0, 8.0, 6.0, 2.0},
                    {1.0, 2.0, 3.0, 2.0, 1.0}
            };
        }

        double total = 0.0;
        for (int i = 0; i < gauss.length; i++) {
            for (int j = 0; j < gauss[i].length; j++) {
                total += gauss[i][j];
            }
        }
        for (int i = 0; i < gauss.length; i++) {
            for (int j = 0; j < gauss[i].length; j++) {
                gauss[i][j] = gauss[i][j] / total;
            }
        }

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(size);
        convolutionMatrix.setMatrix(gauss);
        int[] pixels = convolutionMatrix.applyConvolution(bitmap);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Apply a Sobel filter on the image.
     * It will mark the image outlines.
     */
    public void sobelFilterConvolution() {
        toGraySecondVersion();
        Bitmap bitmap = getBitmap();

        double[][] sobelHorizontal = new double[][]{
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        double[][] sobelVertical = new double[][]{
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);

        convolutionMatrix.setMatrix(sobelHorizontal);
        int[] sobelPixelsHorizontal = convolutionMatrix.applyConvolutionOnGrayImage(bitmap);

        convolutionMatrix.setMatrix(sobelVertical);
        int[] sobelPixelsVertical = convolutionMatrix.applyConvolutionOnGrayImage(bitmap);

        int[] sobelPixels = new int[bitmap.getWidth() * bitmap.getHeight()];

        for (int i = 0; i < sobelPixels.length; i++) {
            int newPixel = (int) Math.sqrt(sobelPixelsHorizontal[i] * sobelPixelsHorizontal[i] + sobelPixelsVertical[i] * sobelPixelsVertical[i]);
            if (newPixel > 255) newPixel = 255;
            sobelPixels[i] = Color.argb(Color.alpha(pixels[i]), newPixel, newPixel, newPixel);
        }

        bitmap.setPixels(sobelPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Apply a Laplacien filter on the image.
     * It will mark the image outlines.
     */
    public void laplacienFilterConvolution() {
        toGraySecondVersion();
        Bitmap bitmap = getBitmap();

        double[][] laplacien = new double[][]{
                {1, 1, 1},
                {1, -8, 1},
                {1, 1, 1}
        };

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.setMatrix(laplacien);
        int[] results = convolutionMatrix.applyConvolutionOnGrayImage(bitmap);

        int[] laplacienPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        for (int i = 0; i < laplacienPixels.length; i++) {
            int newPixel = results[i];
            if (newPixel > 255) newPixel = 255;
            if (newPixel < 0) newPixel = 0;
            laplacienPixels[i] = Color.argb(Color.alpha(pixels[i]), newPixel, newPixel, newPixel);
        }
        bitmap.setPixels(laplacienPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }
}
