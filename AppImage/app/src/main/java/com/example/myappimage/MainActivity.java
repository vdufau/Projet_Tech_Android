package com.example.myappimage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.example.myappimage.algorithm.JavaAlgorithm;
import com.example.myappimage.algorithm.RenderscriptAlgorithm;
import com.example.myappimage.dialog.*;
import com.skydoves.colorpickerview.ColorPickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private JavaAlgorithm java;
    private RenderscriptAlgorithm rs;

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

        java = new JavaAlgorithm(bitmap);
        rs = new RenderscriptAlgorithm(bitmap, context);

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
        final String[] listInterval = getResources().getStringArray(R.array.keepColorInterval);
        final String[] listContrast = getResources().getStringArray(R.array.contrastDim);
        final String[] listGauss = getResources().getStringArray(R.array.gaussChoices);
        switch (item.getItemId()) {
            case R.id.gray:
//                toGrayFirstVersion();
                java.toGraySecondVersion();
//                toGrayThirdVersion();
                return true;
            case R.id.grayRS:
                rs.toGrayRS();
                return true;
            case R.id.colorize:
                final CustomColorDialog colorizeDialog = new CustomColorDialog("Choix de la couleur", null, this);
                ((ColorPickerDialog.Builder) colorizeDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = colorizeDialog.getValue();
                        if (value >= 0)
                            java.colorize(value);
                    }
                });
                ((ColorPickerDialog.Builder) colorizeDialog.getBuilder()).show();
                return true;
            case R.id.colorizeRS:
                final CustomColorDialog colorizeRSDialog = new CustomColorDialog("Choix de la couleur (RS)", null, this);
                ((ColorPickerDialog.Builder) colorizeRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = colorizeRSDialog.getValue();
                        if (value >= 0)
                            rs.colorizeRS(value);
                    }
                });
                ((ColorPickerDialog.Builder) colorizeRSDialog.getBuilder()).show();
                return true;
            case R.id.keepColor:
                final CustomColorDialog keepColorDialog = new CustomColorDialog("Choix des couleurs", "Première couleur de l'intervalle", this);
                ((ColorPickerDialog.Builder) keepColorDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        final int h = keepColorDialog.getValue();
                        final CustomColorDialog keepSecondColorDialog = new CustomColorDialog("Choix des couleurs", "Seconde couleur de l'intervalle", context);
                        ((ColorPickerDialog.Builder) keepSecondColorDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                final int secondH = keepSecondColorDialog.getValue();
                                final CustomRadioDialog intervalDialog = new CustomRadioDialog("Couleurs à garder \nValeurs choisies : " + h + " et " + secondH, null, context, listInterval);
                                ((AlertDialog.Builder) intervalDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        boolean inter = intervalDialog.getValue() == 1;
                                        java.keepColor(h, secondH, inter);
                                    }
                                });
                                ((AlertDialog.Builder) intervalDialog.getBuilder()).show();
                            }
                        });
                        ((ColorPickerDialog.Builder) keepSecondColorDialog.getBuilder()).show();
                    }
                });
                ((ColorPickerDialog.Builder) keepColorDialog.getBuilder()).show();
                return true;
            case R.id.keepColorRS:
                final CustomColorDialog keepColorRSDialog = new CustomColorDialog("Choix des couleurs (RS)", "Première couleur de l'intervalle", this);
                ((ColorPickerDialog.Builder) keepColorRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        final int h = keepColorRSDialog.getValue();
                        final CustomColorDialog keepSecondColorRSDialog = new CustomColorDialog("Choix des couleurs (RS)", "Seconde couleur de l'intervalle", context);
                        ((ColorPickerDialog.Builder) keepSecondColorRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                final int secondH = keepSecondColorRSDialog.getValue();
                                final CustomRadioDialog intervalRSDialog = new CustomRadioDialog("Couleurs à garder \nValeurs choisies : " + h + " et " + secondH, null, context, listInterval);
                                ((AlertDialog.Builder) intervalRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        int inter = intervalRSDialog.getValue();
                                        rs.keepColorRS(h, secondH, inter);
                                    }
                                });
                                ((AlertDialog.Builder) intervalRSDialog.getBuilder()).show();
                            }
                        });
                        ((ColorPickerDialog.Builder) keepSecondColorRSDialog.getBuilder()).show();
                    }
                });
                ((ColorPickerDialog.Builder) keepColorRSDialog.getBuilder()).show();
                return true;
            case R.id.brightness:
                final CustomInputDialog brightnessDialog = new CustomInputDialog("Choix du niveau de luminosité de l'image (0-200)", null, this);
                ((AlertDialog.Builder) brightnessDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = brightnessDialog.getValue();
                        if (value <= 200 && value >= 0) {
                            bitmap.setPixels(initialPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                            java.changeBitmapBrightness((float) value / 100f);
                        }
                    }
                });
                ((AlertDialog.Builder) brightnessDialog.getBuilder()).show();
                return true;
            case R.id.dynamicExpansion:
                java.dynamicExpansion();
                return true;
            case R.id.dynamicExpansionRS:
                rs.dynamicExpansionRS();
                return true;
            case R.id.contrastDiminution:
                final CustomRadioDialog contrastDialog = new CustomRadioDialog("Choix de la diminution", null, this, listContrast);
                ((AlertDialog.Builder) contrastDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int choice = contrastDialog.getValue();
                        if (choice >= 0)
                            java.contrastDiminution(choice);
                    }
                });
                ((AlertDialog.Builder) contrastDialog.getBuilder()).show();
                return true;
            case R.id.contrastDiminutionRS:
                final CustomRadioDialog constrastRSDialog = new CustomRadioDialog("Choix de la diminution (RS)", null, this, listContrast);
                ((AlertDialog.Builder) constrastRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int choice = constrastRSDialog.getValue();
                        if (choice >= 0)
                            rs.contrastDiminutionRS(choice);
                    }
                });
                ((AlertDialog.Builder) constrastRSDialog.getBuilder()).show();
                return true;
            case R.id.histogramEqualization:
                java.histogramEqualization();
                return true;
            case R.id.histogramEqualizationRS:
                rs.histogramEqualizationRS();
                return true;
            case R.id.averageFilter:
                final CustomInputDialog averageDialog = new CustomInputDialog("Choix de la taille du noyau de convolution", "Le nombre rentré doit être impair", this);
                ((AlertDialog.Builder) averageDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = averageDialog.getValue();
                        if (value > 0 && value % 2 == 1)
                            java.averageFilterConvolution(value);
                        else
                            Toast.makeText(context, "Nombre invalide", Toast.LENGTH_LONG).show();
                    }
                });
                ((AlertDialog.Builder) averageDialog.getBuilder()).show();
                return true;
            case R.id.averageFilterRS:
//                inputDialog(AlgorithmVersion.RENDERSCRIPT, AlgorithmType.AVERAGE_CONVOLUTION);
                return true;
            case R.id.gaussConvolution:
                final CustomRadioDialog gaussDialog = new CustomRadioDialog("Choix de la taille du filtre de Gauss", null, this, listGauss);
                ((AlertDialog.Builder) gaussDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = gaussDialog.getValue();
                        if (value >= 0)
                            java.gaussianFilterConvolution(Integer.parseInt(listGauss[value]));
                    }
                });
                ((AlertDialog.Builder) gaussDialog.getBuilder()).show();
                return true;
            case R.id.sobelConvolution:
                java.sobelFilterConvolution();
                return true;
            case R.id.sobelConvolutionRS:
//                sobelFilterConvolutionRS();
                return true;
            case R.id.laplacienConvolution:
                java.laplacienFilterConvolution();
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

}
