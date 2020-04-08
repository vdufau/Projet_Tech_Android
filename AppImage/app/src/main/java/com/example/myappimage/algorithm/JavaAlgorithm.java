package com.example.myappimage.algorithm;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.example.myappimage.ConvolutionMatrix;
import com.example.myappimage.PixelTransformation;

import java.nio.IntBuffer;

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
     * @return the new pixels
     * @deprecated First version of image transformation to gray.
     * Worst version.
     */
    public int[] toGrayFirstVersion() {
        Bitmap bitmap = getBitmap();
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i, j);
                int pixelGray = (int) pixelToGray(pixel);
                int newPixel = Color.argb(Color.alpha(pixel), pixelGray, pixelGray, pixelGray);
                bitmap.setPixel(i, j, newPixel);
            }
        }
        return getPixels();
    }

    /**
     * Second version of image transformation to gray.
     * Better than the first.
     *
     * @return the new pixels
     */
    public int[] toGraySecondVersion() {
        Bitmap bitmap = getBitmap();
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            int pixelGray = (int) pixelToGray(pixels[i]);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Overload of the grayscale algorithm to match with an entry bitmap.
     *
     * @param bitmap the bitmap to transform
     * @return the transformed bitmap
     */
    public Bitmap toGraySecondVersion(Bitmap bitmap) {
        int[] pixels = getPixels(bitmap);
        for (int i = 0; i < pixels.length; i++) {
            int pixelGray = (int) pixelToGray(pixels[i]);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap;
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
     * Invert all bitmap pixels.
     *
     * @return the new pixels
     */
    public int[] invert() {
        Bitmap bitmap = getBitmap();
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.argb(Color.alpha(pixels[i]), 255 - Color.red(pixels[i]), 255 - Color.green(pixels[i]), 255 - Color.blue(pixels[i]));
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Overload of the invert algorithm to match with an entry bitmap.
     *
     * @param bitmap the bitmap to transform
     * @return the transformed bitmap
     */
    public Bitmap invert(Bitmap bitmap) {
        int[] pixels = getPixels(bitmap);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.argb(Color.alpha(pixels[i]), 255 - Color.red(pixels[i]), 255 - Color.green(pixels[i]), 255 - Color.blue(pixels[i]));
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap;
    }

    /**
     * Colorize the bitmap with a hue.
     *
     * @param color the hue chosen by the user
     * @return the new pixels
     */
    public int[] colorize(int color) {
        Bitmap bitmap = getBitmap();
        int[] pixels = getPixels();
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
        return getPixels();
    }

    /**
     * Keep an interval of color of the bitmap and colorize the pixels which are not in this interval in gray scale.
     *
     * @param h       the first hue chosen by the user
     * @param secondH the second hue chosen by the user
     * @param inter   the parameter which determine if the colors to keep are between the two hues or not
     * @return the new pixels
     */
    public int[] keepColor(int h, int secondH, boolean inter) {
        Bitmap bitmap = getBitmap();
        int[] interval = keepColorInteval(h, secondH);
        int[] pixels = getPixels();
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
        return getPixels();
    }

    /**
     * Modifies the bitmap's brightness.
     *
     * @param value the brightness value chosen by the user (0-200)
     * @return the new pixels
     */
    public int[] changeBitmapBrightness(float value) {
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
        return getPixels();
    }

    /**
     * Extend the pixels values if possible.
     *
     * @return the new pixels
     */
    public int[] dynamicExpansion() {
        Bitmap bitmap = getBitmap();
        int size = 101;
        int[] LUTValue = new int[size];
        int[] pixels = getPixels();
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
        return getPixels();
    }

    /**
     * Close the interval of pixels values.
     *
     * @param dimChoice the diminution asked by the user
     * @return the new pixels
     */
    public int[] contrastDiminution(int dimChoice) {
        Bitmap bitmap = getBitmap();
        int size = 101;
        int[] LUTValue = new int[size];
        int[] pixels = getPixels();
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

        int maxValue2 = -1;
        switch (dimChoice) {
            case 0:
                maxValue2 = maxValue - 1;
                break;
            case 1:
                maxValue2 = maxValue - (maxValue + minValue) / 4;
                break;
            case 2:
                maxValue2 = maxValue - (maxValue + minValue) / 2;
                break;
            case 3:
                maxValue2 = maxValue - (3 * (maxValue + minValue) / 4);
                break;
            case 4:
                maxValue2 = minValue + 1;
                break;
            default:
                break;
        }

        if (maxValue2 >= 0 && minValue >= 0 && maxValue2 > minValue && maxValue2 - minValue != 0) {
            for (int i = 0; i < size; i++) {
                LUTValue[i] = (i - minValue) * (maxValue2 - minValue) / (maxValue - minValue) + minValue;
            }

            for (int i = 0; i < pixels.length; i++) {
                float[] hsv = myRgbToHsv(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]));
                hsv[2] = LUTValue[(int) hsv[2]];
                float[] rgb = myHsvToRgb(hsv[0], hsv[1] / 100, hsv[2] / 100);
                pixels[i] = Color.argb(Color.alpha(pixels[i]), (int) rgb[0], (int) rgb[1], (int) rgb[2]);
            }

            bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        return getPixels();
    }

    /**
     * Equalize the pixels values.
     *
     * @return the new pixels
     */
    public int[] histogramEqualization() {
        Bitmap bitmap = getBitmap();
        int size = 101;
        int[] histV = new int[size];
        int[] pixels = getPixels();
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
        return getPixels();
    }

    /**
     * Apply an average filter on the image.
     * It will blur the image.
     *
     * @param size the size of the kernel
     * @return the new pixels
     */
    public int[] averageFilterConvolution(int size) {
        Bitmap bitmap = getBitmap();
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(size);
        double moy = 1.0 / (size * size);
        convolutionMatrix.setMatrix(moy);
        int[] pixels = convolutionMatrix.applyConvolution(bitmap);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Apply a Gaussian filter on the image.
     * It will blur the image.
     *
     * @return the new pixels
     */
    public int[] gaussianFilterConvolution(int size) {
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
        return getPixels();
    }

    /**
     * Overload of the gaussian blur to match with an entry bitmap.
     *
     * @param bitmap the bitmap to transform
     * @return the transformed bitmap
     */
    public Bitmap gaussianFilterConvolution(Bitmap bitmap) {
        double[][] gauss = new double[][]{
                {1.0, 2.0, 3.0, 2.0, 1.0},
                {2.0, 6.0, 8.0, 6.0, 2.0},
                {3.0, 8.0, 10.0, 8.0, 3.0},
                {2.0, 6.0, 8.0, 6.0, 2.0},
                {1.0, 2.0, 3.0, 2.0, 1.0}
        };

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

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(5);
        convolutionMatrix.setMatrix(gauss);
        int[] pixels = convolutionMatrix.applyConvolution(bitmap);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap;
    }

    /**
     * Apply a Sobel filter on the image.
     * It will mark the image outlines.
     *
     * @return the new pixels
     */
    public int[] sobelFilterConvolution() {
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

        int[] pixels = getPixels();

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
        return getPixels();
    }

    /**
     * Apply a Laplacien filter on the image.
     * It will mark the image outlines.
     *
     * @return the new pixels
     */
    public int[] laplacienFilterConvolution() {
        toGraySecondVersion();
        Bitmap bitmap = getBitmap();

        double[][] laplacien = new double[][]{
                {1, 1, 1},
                {1, -8, 1},
                {1, 1, 1}
        };

        int[] pixels = getPixels();

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
        return getPixels();
    }

    /**
     * Apply a sketch effect on the image.
     *
     * @param choice the user choice of the algorithm
     * @return the new pixels
     */
    public int[] sketchEffect(int choice) {
        Bitmap bitmap = getBitmap();
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        copy = toGraySecondVersion(copy);
        Bitmap invert = copy.copy(Bitmap.Config.ARGB_8888, true);
        invert = invert(invert);
        invert = gaussianFilterConvolution(invert);
        int[] pixelsCopy = getPixels(copy);
        int[] pixelsInvert = getPixels(invert);
        int[] results = new int[bitmap.getWidth() * bitmap.getHeight()];
        for (int i = 0; i < pixelsCopy.length; i++) {
            int pixelCopy = (int) pixelToGray(pixelsCopy[i]);
            int pixelInvert = (int) pixelToGray(pixelsInvert[i]);
            int newPixel = pixelInvert == 255 ? pixelInvert : Math.min(255, ((pixelCopy << 8) / (255 - pixelInvert)));
            for (int j = 0; j < choice * 2; j++)
                newPixel = newPixel * newPixel / 255;
            results[i] = Color.argb(Color.alpha(pixelsCopy[i]), newPixel, newPixel, newPixel);
        }
        bitmap.setPixels(results, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Apply a sketch effect in color on the image.
     *
     * @return the new pixels
     */
    public int[] sketchColorEffect() {
        Bitmap bitmap = getBitmap();
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        copy = toGraySecondVersion(copy);
        Bitmap invert = copy.copy(Bitmap.Config.ARGB_8888, true);
        invert = invert(invert);
        invert = gaussianFilterConvolution(invert);
        int[] pixelsBitmap = getPixels(bitmap);
        int[] pixelsCopy = getPixels(copy);
        int[] pixelsInvert = getPixels(invert);
        int[] results = new int[bitmap.getWidth() * bitmap.getHeight()];
        for (int i = 0; i < pixelsBitmap.length; i++) {
            int redB = Color.red(pixelsBitmap[i]);
            int redI = Color.red(pixelsInvert[i]);
            int blueB = Color.blue(pixelsBitmap[i]);
            int blueI = Color.blue(pixelsInvert[i]);
            int greenB = Color.green(pixelsBitmap[i]);
            int greenI = Color.green(pixelsInvert[i]);
            int newRed = redI == 255 ? redI : Math.min(255, ((redB << 8) / (255 - redI)));
            int newBlue = blueI == 255 ? blueI : Math.min(255, ((blueB << 8) / (255 - blueI)));
            int newGreen = greenI == 255 ? greenI : Math.min(255, ((greenB << 8) / (255 - greenI)));

//            int pixelInvert = (int) pixelToGray(pixelsInvert[i]);
//            int newRed = (Color.red(pixelsBitmap[i]) * pixelInvert) / 255;
//            int newGreen = (Color.green(pixelsBitmap[i]) * pixelInvert) / 255;
//            int newBlue = (Color.blue(pixelsBitmap[i]) * pixelInvert) / 255;

//            float[] hsvBitmap = myRgbToHsv(Color.red(pixelsBitmap[i]), Color.green(pixelsBitmap[i]), Color.blue(pixelsBitmap[i]));
//            float[] hsvInvert = myRgbToHsv(Color.red(pixelsInvert[i]), Color.green(pixelsInvert[i]), Color.blue(pixelsInvert[i]));
//            float[] newHsv = new float[]{hsvInvert[0], hsvBitmap[1], hsvInvert[2]};
//            float[] rgb = myHsvToRgb(newHsv[0], newHsv[1] / 100, newHsv[2] / 100);
//            int newRed = (int)rgb[0];
//            int newGreen = (int)rgb[1];
//            int newBlue = (int)rgb[2];

            results[i] = Color.argb(Color.alpha(pixelsBitmap[i]), newRed, newGreen, newBlue);
        }
        bitmap.setPixels(results, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }
}
