package com.example.myappimage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO fragment a la place d'activité
/**
 * MainActivity Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_L3
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;
    private static TextView tv;
    private Button buttonHisto;
    private Button buttonSave;
    private static ImageView im;
    private Bitmap bitmap;
    private int[] initialPixels;
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector SGD;

    /**
     * Initialization of the application.
     * Initialization of the main layout.
     * Initialization of the image which will be displayed in the application.
     * Initialization of buttons listener.
     *
     * @param savedInstanceState the data to initialize if there is a save thanks to onSaveInstanceState (it will not be the case in this application)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv = (TextView) findViewById(R.id.sizeImage);
        im = (ImageView) findViewById(R.id.imageView);

        initialization();

        buttonHisto = (Button) findViewById(R.id.buttonHisto);
        buttonHisto.setOnClickListener(this);
        buttonSave = (Button) findViewById(R.id.saveImage);
        buttonSave.setOnClickListener(this);

        SGD = new ScaleGestureDetector(this, new ScaleListener());
    }

    public static ImageView getIm() {
        return im;
    }

    /**
     * Initialization of the menu.
     *
     * @param menu the menu which will receive the items of our menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Call the comportment associate to the user's click in the menu.
     *
     * @param item the comportment asked by the user
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gray:
//                toGrayFirstVersion();
                toGraySecondVersion();
//                toGrayThirdVersion();
                return true;
            case R.id.grayRS:
                toGrayRS();
                return true;
            case R.id.colorize:
                colorDialog(AlgorithmVersion.JAVA, AlgorithmType.COLORIZE);
                return true;
            case R.id.colorizeRS:
                colorDialog(AlgorithmVersion.RENDERSCRIPT, AlgorithmType.COLORIZE);
                return true;
            case R.id.keepColor:
                colorDialog(AlgorithmVersion.JAVA, AlgorithmType.KEEP_COLOR);
                return true;
            case R.id.keepColorRS:
                colorDialog(AlgorithmVersion.RENDERSCRIPT, AlgorithmType.KEEP_COLOR);
                return true;
            case R.id.dynamicExpansion:
                dynamicExpansion();
                return true;
            case R.id.dynamicExpansionRS:
                dynamicExpansionRS();
                return true;
            case R.id.contrastDiminution:
                inputDialog(AlgorithmVersion.JAVA, AlgorithmType.CONTRAST_DIMINUTION);
                return true;
            case R.id.contrastDiminutionRS:
                inputDialog(AlgorithmVersion.RENDERSCRIPT, AlgorithmType.CONTRAST_DIMINUTION);
                return true;
            case R.id.histogramEqualization:
                histogramEqualization();
                return true;
            case R.id.histogramEqualizationRS:
                histogramEqualizationRS();
                return true;
            case R.id.averageFilter:
                inputDialog(AlgorithmVersion.JAVA, AlgorithmType.AVERAGE_CONVOLUTION);
                return true;
            case R.id.averageFilterRS:
                inputDialog(AlgorithmVersion.RENDERSCRIPT, AlgorithmType.AVERAGE_CONVOLUTION);
                return true;
            case R.id.sobelConvolution:
                sobelFilterConvolution();
                return true;
            case R.id.sobelConvolutionRS:
//                sobelFilterConvolutionRS();
                return true;
            case R.id.reinitialization:
                bitmap.setPixels(initialPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHisto:
                Intent intent = new Intent(MainActivity.this, HistogramActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.saveImage:
                if (isExternalStorageWritable()) {
                    Log.i("info", "oui");
                    saveImage();
                } else {
                    Log.i("info", "non");
                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        SGD.onTouchEvent(event);
        return true;
    }

    /**
     * Initialize the bitmap.
     */
    private void initialization() {
        bitmap = BitmapSingleton.getInstance().getBitmap();
        initialPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(initialPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        im.setImageBitmap(bitmap);
//        bitmap = decodeSampledBitmapFromResource(getResources(), img, 512, 512);
//        tv.setText(tv.getText() + "\nTaille de la bitmap : " + bitmap.getWidth() + " x " + bitmap.getHeight());
//        im.setImageBitmap(bitmap);
//        BitmapSingleton.getInstance().setBitmap(bitmap);
    }

    /**
     * Create the bitmap.
     *
     * @param res       the resources
     * @param resId     the id's image
     * @param reqWidth  the width required
     * @param reqHeight the height required
     * @return the bitmap created built on the image
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inMutable = true;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Calculate the value to rescale the image.
     *
     * @param options   the bitmap's options
     * @param reqWidth  the width required
     * @param reqHeight the height required
     * @return the size
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        tv.setText("Taille de base de l'image : " + width + " x " + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // TODO marche pas -> probleme internal / external storage
    private void saveImage() {
//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        Log.i("info", timeStamp);
//        String fname = "image_" + timeStamp + ".jpg";
//        Log.i("info", fname);
//
//        File file = new File(root, fname);
//        if (file.exists()) file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * ---------------
     * |  JAVA PART  |
     * ---------------
     */

    /**
     * Transform a pixel into a pixel which will be in gray scale.
     *
     * @param pixel the pixel to transform
     * @return the gray pixel
     */
    public int pixelToGray(int pixel) {
        return (int) (0.3 * Color.red(pixel) + 0.59 * Color.green(pixel) + 0.11 * Color.blue(pixel));
    }

    /**
     * @deprecated First version of image transformation to gray.
     * Worst version.
     */
    private void toGrayFirstVersion() {
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i, j);
                int pixelGray = pixelToGray(pixel);
                int newPixel = Color.argb(Color.alpha(pixel), pixelGray, pixelGray, pixelGray);
                bitmap.setPixel(i, j, newPixel);
            }
        }
    }

    /**
     * Second version of image transformation to gray.
     * Better than the first.
     */
    private void toGraySecondVersion() {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int pixelGray = pixelToGray(pixels[i]);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Third version of image transformation to gray.
     * Better than the first.
     */
    private void toGrayThirdVersion() {
        ColorMatrix m = new ColorMatrix();
        m.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(m);
        im.setColorFilter(filter);
    }

    /**
     * Show a color dialog to choose one color which will be useful to an image transformation algorithm.
     * Use of an external widget : ColorPickerView
     * Link : https://github.com/skydoves/ColorPickerView
     *
     * @param version the version of the algorithm to execute
     * @param type    the type of algorithm
     */
    private void colorDialog(final AlgorithmVersion version, final AlgorithmType type) {
        new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Choix de la couleur")
                .setPositiveButton(getString(R.string.validate), new ColorListener() {
                    @Override
                    public void onColorSelected(int color, boolean fromUser) {
                        final int h = (int) myRgbToHsv(Color.red(color), Color.green(color), Color.blue(color))[0];
                        switch (type) {
                            case COLORIZE:
                                switch (version) {
                                    case JAVA:
                                        colorize(h);
                                        break;
                                    case RENDERSCRIPT:
                                        colorizeRS(h);
                                        break;
                                }
                                break;
                            case KEEP_COLOR:
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Choix de l'intervalle");
                                builder.setMessage("Choississez la valeur de l'intervalle total à garder");
                                final EditText input = new EditText(context);
                                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                                builder.setView(input);

                                builder.setPositiveButton(getString(R.string.validate), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        int interval = Integer.parseInt(input.getText().toString());
                                        switch (version) {
                                            case JAVA:
                                                keepColor(h, interval);
                                                break;
                                            case RENDERSCRIPT:
                                                keepColorRS(h, interval);
                                                break;
                                        }
                                    }
                                });
                                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                                break;
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(false)
                .show();
    }

    /**
     * Show an input dialog to choose a value.
     */
    private void inputDialog(final AlgorithmVersion version, final AlgorithmType type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (type) {
            case CONTRAST_DIMINUTION:
                builder.setTitle("Choix de la diminution");
                break;
            case AVERAGE_CONVOLUTION:
                builder.setTitle("Choix de la taille du noyau de convolution");
                builder.setMessage("Le nombre rentré doit être impair");
                break;
        }
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.validate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().matches("")) {
                int value = Integer.parseInt(input.getText().toString());
                    switch (type) {
                        case CONTRAST_DIMINUTION:
                            switch (version) {
                                case JAVA:
                                    Log.i("aya", String.valueOf(value));
                                    contrastDiminution(value);
                                    break;
                                case RENDERSCRIPT:
                                    contrastDiminutionRS(value);
                                    break;
                            }
                            break;
                        case AVERAGE_CONVOLUTION:
                            switch (version) {
                                case JAVA:
                                    if (value % 2 == 1) {
                                        averageFilterConvolution(value);
                                    } else {
                                        inputDialog(AlgorithmVersion.JAVA, AlgorithmType.AVERAGE_CONVOLUTION);
                                    }
                                    break;
                                case RENDERSCRIPT:
                                    if (value % 2 == 1) {
//                                    averageFilterConvolutionRS(value);
                                    } else {
                                        inputDialog(AlgorithmVersion.RENDERSCRIPT, AlgorithmType.AVERAGE_CONVOLUTION);
                                    }
                                    break;
                            }
                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Colorize the bitmap with a hue.
     *
     * @param color the hue chosen by the user
     */
    private void colorize(int color) {
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
     * @param color    the hue chosen by the user
     * @param interval the interval to keep
     */
    private void keepColor(int color, int interval) {
        int interLeft = color - interval / 2;
        int interRight = color + interval / 2;
        if (interLeft < 0) {
            int tmp = interLeft;
            interLeft = interRight;
            interRight = 360 + (tmp % 360);
        }
        if (interRight > 360) {
            int tmp = interRight;
            interRight = interLeft;
            interLeft = tmp % 360;
        }
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

            if (interLeft < hsv[0] && interRight > hsv[0]) {
                int pixelGray = pixelToGray(pixels[i]);
                pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
            }
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Extend the pixels values if possible.
     */
    private void dynamicExpansion() {
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
    private void contrastDiminution(int diminution) {
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
    private void histogramEqualization() {
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
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(size);
        double moy = 1.0 / (size * size);
        convolutionMatrix.setMatrix(moy);
        int[] pixels = convolutionMatrix.applyConvolution(bitmap);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Apply a Sobel filter on the image.
     * It will mark the image outlines.
     */
    public void sobelFilterConvolution() {
        double[][] sobelHorizontal = new double[][]{
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        double[][] sobelVertical = new double[][]{
                {1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}
        };

        int size = 3;
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(size);
        convolutionMatrix.setMatrix(sobelHorizontal);
        int[] sobelPixelsHorizontal = convolutionMatrix.applyConvolution(bitmap);
        convolutionMatrix.setMatrix(sobelVertical);
        int[] sobelPixelsVertical = convolutionMatrix.applyConvolution(bitmap);
        int[] sobelPixels = new int[bitmap.getWidth() * bitmap.getHeight()];

        for (int i = 0; i < sobelPixels.length; i++) {
            int pixelRed = (int) Math.sqrt(Math.pow(Color.red(sobelPixelsHorizontal[i]), 2) + Math.pow(Color.red(sobelPixelsVertical[i]), 2));
            int pixelGreen = (int) Math.sqrt(Math.pow(Color.green(sobelPixelsHorizontal[i]), 2) + Math.pow(Color.green(sobelPixelsVertical[i]), 2));
            int pixelBlue = (int) Math.sqrt(Math.pow(Color.blue(sobelPixelsHorizontal[i]), 2) + Math.pow(Color.blue(sobelPixelsVertical[i]), 2));
            sobelPixels[i] = Color.argb(Color.alpha(sobelPixelsHorizontal[i]),
                    pixelRed > 255 ? 255 : pixelRed,
                    pixelGreen > 255 ? 255 : pixelGreen,
                    pixelBlue > 255 ? 255 : pixelBlue);
        }

        bitmap.setPixels(sobelPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Recoded function to transform rgb values into hsv values.
     *
     * @param red   the red value of the pixel [0-255]
     * @param green the green value of the pixel [0-255]
     * @param blue  the blue value of the pixel [0-255]
     * @return an array which contain the hue, the saturation and the value of the pixel
     */
    private float[] myRgbToHsv(float red, float green, float blue) {
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
    private float[] myHsvToRgb(float h, float s, float v) {
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

    /**
     * -----------------------
     * |  RENDERSCRIPT PART  |
     * -----------------------
     */

    /**
     * Transform the image in gray scale using renderscript.
     */
    private void toGrayRS() {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_gray grayScript = new ScriptC_gray(rs);

        grayScript.forEach_toGray(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        grayScript.destroy();
        rs.destroy();
    }

    /**
     * Colorize the image using renderscript.
     *
     * @param color the color to apply to the image
     */
    private void colorizeRS(int color) {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_colorize colorizeScript = new ScriptC_colorize(rs);

        colorizeScript.set_color(color);

        colorizeScript.forEach_colorize(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        colorizeScript.destroy();
        rs.destroy();
    }

    /**
     * Keep an interval of colors using renderscript.
     * // TODO adapter comme version java
     *
     * @param color    the color to keep
     * @param interval the interval
     */
    private void keepColorRS(int color, int interval) {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_keepColor keepColorScript = new ScriptC_keepColor(rs);

        keepColorScript.set_color(color);
        keepColorScript.set_interval(interval);

        keepColorScript.forEach_keepColor(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        keepColorScript.destroy();
        rs.destroy();
    }

    /**
     * Extend the pixels values using renderscript.
     */
    private void dynamicExpansionRS() {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_dynamicExpansion dynamicExpansionScript = new ScriptC_dynamicExpansion(rs);

        dynamicExpansionScript.forEach_minMax(input);
        dynamicExpansionScript.invoke_createLUTExpanded();
        dynamicExpansionScript.forEach_expansion(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        dynamicExpansionScript.destroy();
        rs.destroy();
    }

    /**
     * Close the interval of pixels values using renderscript.
     *
     * @param diminution the diminution asked by the user
     */
    private void contrastDiminutionRS(int diminution) {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_contrastDiminution contrastDiminutionScript = new ScriptC_contrastDiminution(rs);

        contrastDiminutionScript.set_diminution(diminution);

        contrastDiminutionScript.forEach_minMax(input);
        contrastDiminutionScript.invoke_initNewMinMaxValues();
        contrastDiminutionScript.invoke_createLUTExpanded();
        contrastDiminutionScript.forEach_applyDiminution(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        contrastDiminutionScript.destroy();
        rs.destroy();
    }

    /**
     * Equalize the pixels values using renderscript.
     */
    private void histogramEqualizationRS() {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_histogramEqualization histEqScript = new ScriptC_histogramEqualization(rs);

        histEqScript.set_size(bitmap.getWidth() * bitmap.getHeight());

        histEqScript.forEach_incHisto(input);
        histEqScript.invoke_createHistoCumul();
        histEqScript.forEach_equalization(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        histEqScript.destroy();
        rs.destroy();
    }
}
