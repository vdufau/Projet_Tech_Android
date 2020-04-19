package com.example.myappimage.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import android.util.SparseArray;
import android.widget.ImageView;

import com.example.myappimage.ConvolutionMatrix;
import com.example.myappimage.R;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.Random;

import static com.example.myappimage.PixelTransformation.*;

/**
 * JavaAlgorithm Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class JavaAlgorithm extends Algorithm {
    Bitmap nose;
    Bitmap right_eye;
    Bitmap left_eye;

    public JavaAlgorithm(Bitmap bitmap, Context context) {
        super(bitmap, context);
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
    public int[] toGray() {
        Bitmap bitmap = getBitmap();
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int pixelGray = (int) pixelToGray(pixels[i]);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
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
     * @return the new pixels
     */
    public int[] colorize(int color) {
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
     * Apply an average or a gaussian filter on the image.
     * It will blur the image.
     *
     * @param filterType the type of filter : average (0) or gaussian (1)
     * @param size       the size of the kernel
     * @return the new pixels
     */
    public int[] blurConvolution(int filterType, int size) {
        Bitmap bitmap = getBitmap();
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(size);
        if (filterType == 0) {
            double moy = 1.0 / (size * size);
            convolutionMatrix.setMatrix(moy);
        } else {
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

            convolutionMatrix.setMatrix(gauss);
        }
        int[] pixels = convolutionMatrix.applyConvolution(bitmap);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Apply a Sobel filter on the image.
     * It will mark the image outlines.
     *
     * @return the new pixels
     */
    public int[] sobelFilterConvolution() {
        toGray();
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
        toGray();
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
     * Apply a cartoon effect to the bitmap.
     *
     * @return the new pixels
     */
    public int[] cartoonEffect() {
        Bitmap bitmap = getBitmap();
        blurConvolution(0, 5);
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            red = (red - (red % 64));
            green = (green - (green % 64));
            blue = (blue - (blue % 64));
            if (red > 255) red = 255;
            if (red < 0) red = 0;
            if (green > 255) green = 255;
            if (green < 0) green = 0;
            if (blue > 255) blue = 255;
            if (blue < 0) blue = 0;
            pixels[i] = Color.argb(Color.alpha(pixels[i]), red, green, blue);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Apply a snow effect to the bitmap.
     *
     * @return the new pixels
     */
    public int[] snowEffect() {
        Bitmap bitmap = getBitmap();
        Random random = new Random();
        int[] pixels = getPixels();
        for (int i = 0; i < pixels.length; i++) {
            int r = random.nextInt(255);
            if (Color.red(pixels[i]) > r && Color.green(pixels[i]) > r && Color.blue(pixels[i]) > r)
                pixels[i] = Color.argb(Color.alpha(pixels[i]), 255, 255, 255);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

    /**
     * Add objects to the bitmap.
     *
     * @return the new pixels
     */
    public int[] objectIncrustation() {
        Bitmap bitmap = getBitmap();
        Context context = getContext();
        nose = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.clown_nose);
        right_eye = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.right_eye);
        left_eye = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.left_eye);

        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(bitmap, 0, 0, null);

        FaceDetector faceDetector =
                new FaceDetector.Builder(context.getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            nose = Bitmap.createScaledBitmap(nose, (int) thisFace.getWidth() / 4, (int) thisFace.getWidth() / 4, false);
            right_eye = Bitmap.createScaledBitmap(right_eye, (int) thisFace.getWidth() / 4, (int) thisFace.getWidth() / 4, false);
            left_eye = Bitmap.createScaledBitmap(left_eye, (int) thisFace.getWidth() / 4, (int) thisFace.getWidth() / 4, false);

            for (Landmark landmark : thisFace.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);

                if (landmark.getType() == Landmark.NOSE_BASE) {
                    int scaleWidth = nose.getScaledWidth(tempCanvas);
                    int scaleHeight = nose.getScaledHeight(tempCanvas);
                    tempCanvas.drawBitmap(nose, cx - (scaleWidth / 2), cy - scaleHeight + scaleHeight / 4, null);
                }
                if (landmark.getType() == Landmark.RIGHT_EYE) {
                    int scaleWidth = right_eye.getScaledWidth(tempCanvas);
                    int scaleHeight = right_eye.getScaledHeight(tempCanvas);
                    tempCanvas.drawBitmap(right_eye, cx - (scaleWidth / 2), cy - scaleHeight + scaleHeight / 4, null);
                }
                if (landmark.getType() == Landmark.LEFT_EYE) {
                    int scaleWidth = left_eye.getScaledWidth(tempCanvas);
                    int scaleHeight = left_eye.getScaledHeight(tempCanvas);
                    tempCanvas.drawBitmap(left_eye, cx - (scaleWidth / 2), cy - scaleHeight + scaleHeight / 4, null);
                }
            }
        }

        Bitmap b = new BitmapDrawable(context.getResources(), tempBitmap).getBitmap();
        bitmap.setPixels(getPixels(b), 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPixels();
    }

}
