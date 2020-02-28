package com.example.myappimage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.widget.Toast;

import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.myappimage.PixelTransformation.*;

/**
 * MainActivity Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;
    private static TextView tv;
    private Button buttonHisto;
    private Button buttonSave;
    private static ImageView im;
    private Bitmap bitmap;
    private int[] initialPixels;
    private ScaleGestureDetector SGD;
    private float mx, my, curX, curY;
    private int gaussChoice;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        return super.onCreateOptionsMenu(menu);
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
            case R.id.gaussConvolution:
                radioDialog();
                return true;
            case R.id.sobelConvolution:
                sobelFilterConvolution();
                return true;
            case R.id.sobelConvolutionRS:
//                sobelFilterConvolutionRS();
                return true;
            case R.id.laplacienConvolution:
                laplacienFilterConvolution();
                return true;
            case R.id.reinitialization:
                bitmap.setPixels(initialPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Associate functions to the activity's buttons.
     *
     * @param v the button clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHisto:
                Intent intent = new Intent(MainActivity.this, HistogramActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.saveImage:
                saveImage();
                break;
        }
    }

    /**
     * Management of the touch event with the scroll and the zoom.
     *
     * @param event the event
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        SGD.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                im.scrollBy((int) (mx - curX), (int) (my - curY));
                mx = curX;
                my = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
                im.scrollBy((int) (mx - curX), (int) (my - curY));
                break;
        }

        return true;
    }

    /**
     * Initialize the bitmap with a rotation if needed.
     */
    private void initialization() {
        Intent intent = getIntent();
        int orientation = intent.getIntExtra("orientation", 0);

        bitmap = BitmapSingleton.getInstance().getBitmap();

        Bitmap rotatedBitmap = null;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        bitmap = rotatedBitmap;
        im.setImageBitmap(bitmap);

        initialPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(initialPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Rotate a bitmap.
     *
     * @param source the bitmap to rotate
     * @param angle  the rotation's angle
     * @return the new bitmap after the rotation
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Save the image with all the modifications the user has done.
     */
    private void saveImage() {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream out = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(path, timeStamp + ".png");
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            Toast.makeText(this, "Image sauvegardée", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Sauvegarde impossible", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Sauvegarde impossible", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * ---------------
     * |  JAVA PART  |
     * ---------------
     */


    /**
     * @deprecated First version of image transformation to gray.
     * Worst version.
     */
    private void toGrayFirstVersion() {
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
    private void toGraySecondVersion() {
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
     * Show a dialog with a radio choice.
     */
    private void radioDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] list = getResources().getStringArray(R.array.gauss_choices);
        gaussChoice = Integer.parseInt(list[0]);
        builder.setTitle("Choix de la taille du filtre de Gauss")
                .setSingleChoiceItems(list, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gaussChoice = Integer.parseInt(list[which]);
                    }
                })
                .setPositiveButton(getString(R.string.validate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gaussianFilterConvolution();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        int[] intervals = keepColorInteval(color, interval);

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

            if (intervals[0] == 0) {
                if (hsv[0] < intervals[1] || intervals[2] < hsv[0]) {
                    int pixelGray = (int) pixelToGray(pixels[i]);
                    pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
                }
            } else {
                if (hsv[0] > intervals[1] && intervals[2] > hsv[0]) {
                    int pixelGray = (int) pixelToGray(pixels[i]);
                    pixels[i] = Color.argb(Color.alpha(pixels[i]), pixelGray, pixelGray, pixelGray);
                }
            }
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Calculate the correct interval for the keepColor algorithm.
     *
     * @param color    the hue chosen by the user
     * @param interval the interval to keep
     * @return an array with the left and right intervals and a boolean to know if the pixels to change
     * into grayscale are between the intervals or not
     */
    private int[] keepColorInteval(int color, int interval) {
        int inter = 0;
        int interLeft = color - interval / 2;
        int interRight = color + interval / 2;
        if (interLeft < 0) {
            inter = 1;
            int tmp = interLeft;
            interLeft = interRight;
            interRight = 360 + (tmp % 360);
        }
        if (interRight > 360) {
            inter = 1;
            int tmp = interRight;
            interRight = interLeft;
            interLeft = tmp % 360;
        }

        return new int[]{inter, interLeft, interRight};
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
     * Apply a Gaussian filter on the image.
     * It will blur the image.
     */
    public void gaussianFilterConvolution() {
        double[][] gauss;
        if (gaussChoice == 3) {
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

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(gaussChoice);
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
     *
     * @param color    the color to keep
     * @param interval the interval
     */
    private void keepColorRS(int color, int interval) {
        int[] intervals = keepColorInteval(color, interval);

        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_keepColor keepColorScript = new ScriptC_keepColor(rs);

        keepColorScript.set_color(color);
        keepColorScript.set_inter(intervals[0]);
        keepColorScript.set_intervalLeft(intervals[1]);
        keepColorScript.set_intervalRight(intervals[2]);

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
