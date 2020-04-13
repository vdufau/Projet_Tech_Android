package com.example.myappimage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import java.util.ArrayList;
import java.util.Date;

/**
 * MainActivity Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private Context context = this;
    private Button buttonHisto;
    private Button buttonSave;
    private Button revert;
    private Button invert;
    private ArrayList<int[]> revertList;
    private ArrayList<int[]> invertList;
    private static ImageView im;
    private Bitmap bitmap;
    private int[] initialPixels;
    private JavaAlgorithm java;
    private RenderscriptAlgorithm rs;

    private Matrix matrix;
    private Matrix savedMatrix;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    /**
     * Initialization of the application.
     * Initialization of the main layout.
     * Initialization of the image which will be displayed in the application.
     * Initialization of buttons listener.
     *
     * @param savedInstanceState the data to initialize if there is a save thanks to onSaveInstanceState (it will not be the case in this application)
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        im = (ImageView) findViewById(R.id.imageView);
        revertList = new ArrayList<int[]>();
        invertList = new ArrayList<int[]>();

        initialization();

        java = new JavaAlgorithm(bitmap);
        rs = new RenderscriptAlgorithm(bitmap, context);

        buttonHisto = (Button) findViewById(R.id.buttonHisto);
        buttonHisto.setOnClickListener(this);
        buttonSave = (Button) findViewById(R.id.saveImage);
        buttonSave.setOnClickListener(this);
        revert = (Button) findViewById(R.id.revert);
        revert.setOnClickListener(this);
        invert = (Button) findViewById(R.id.invert);
        invert.setOnClickListener(this);
        refreshButton();

        im.setOnTouchListener(this);
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
                revertList.add(java.toGraySecondVersion());
//                toGrayThirdVersion();
                refreshAction();
                return true;
            case R.id.grayRS:
                revertList.add(rs.toGrayRS());
                refreshAction();
                return true;
            case R.id.colorize:
                final CustomColorDialog colorizeDialog = new CustomColorDialog("Choix de la couleur", null, this);
                ((ColorPickerDialog.Builder) colorizeDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = colorizeDialog.getValue();
                        if (value >= 0) {
                            revertList.add(java.colorize(value));
                            refreshAction();
                        }
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
                        if (value >= 0) {
                            revertList.add(rs.colorizeRS(value));
                            refreshAction();
                        }
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
                                        revertList.add(java.keepColor(h, secondH, inter));
                                        refreshAction();
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
                                        revertList.add(rs.keepColorRS(h, secondH, inter));
                                        refreshAction();
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
                            revertList.add(java.changeBitmapBrightness((float) value / 100f));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) brightnessDialog.getBuilder()).show();
                return true;
            case R.id.dynamicExpansion:
                revertList.add(java.dynamicExpansion());
                refreshAction();
                return true;
            case R.id.dynamicExpansionRS:
                revertList.add(rs.dynamicExpansionRS());
                refreshAction();
                return true;
            case R.id.contrastDiminution:
                final CustomRadioDialog contrastDialog = new CustomRadioDialog("Choix de la diminution", null, this, listContrast);
                ((AlertDialog.Builder) contrastDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int choice = contrastDialog.getValue();
                        if (choice >= 0) {
                            revertList.add(java.contrastDiminution(choice));
                            refreshAction();
                        }
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
                        if (choice >= 0) {
                            revertList.add(rs.contrastDiminutionRS(choice));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) constrastRSDialog.getBuilder()).show();
                return true;
            case R.id.histogramEqualization:
                revertList.add(java.histogramEqualization());
                refreshAction();
                return true;
            case R.id.histogramEqualizationRS:
                revertList.add(rs.histogramEqualizationRS());
                refreshAction();
                return true;
            case R.id.averageFilter:
                final CustomInputDialog averageDialog = new CustomInputDialog("Choix de la taille du noyau de convolution", "Le nombre rentré doit être impair", this);
                ((AlertDialog.Builder) averageDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = averageDialog.getValue();
                        if (value > 0 && value % 2 == 1) {
                            revertList.add(java.averageFilterConvolution(value));
                            refreshAction();
                        } else
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
                        if (value >= 0) {
                            revertList.add(java.gaussianFilterConvolution(Integer.parseInt(listGauss[value])));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) gaussDialog.getBuilder()).show();
                return true;
            case R.id.sobelConvolution:
                revertList.add(java.sobelFilterConvolution());
                refreshAction();
                return true;
            case R.id.sobelConvolutionRS:
//                sobelFilterConvolutionRS();
                return true;
            case R.id.laplacienConvolution:
                revertList.add(java.laplacienFilterConvolution());
                refreshAction();
                return true;
            case R.id.cartoonEffect:
                revertList.add(java.cartoonEffect());
                refreshAction();
                return true;
            case R.id.snowEffect:
                revertList.add(java.snowEffect());
                refreshAction();
                return true;
            case R.id.reinitialization:
                bitmap.setPixels(initialPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                revertList.clear();
                revertList.add(initialPixels);
                refreshAction();
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
            case R.id.revert:
                if (revertList.size() > 1) {
                    invertList.add(revertList.get(revertList.size() - 1));
                    revertList.remove(revertList.size() - 1);
                    bitmap.setPixels(revertList.get(revertList.size() - 1), 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                }
                refreshButton();
                break;
            case R.id.invert:
                if (invertList.size() > 0) {
                    revertList.add(invertList.get(invertList.size() - 1));
                    invertList.remove(invertList.size() - 1);
                    bitmap.setPixels(revertList.get(revertList.size() - 1), 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                }
                refreshButton();
                break;
        }
    }

    /**
     * Management of the touch event with the scroll and the zoom.
     *
     * @param v     the view on which the event is applied
     * @param event the event
     * @return true
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * Return the distance between the two fingers for a zoom event.
     *
     * @param event the event
     * @return the distance
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Set a point to the center of the line created by the two fingers for a zoom event.
     *
     * @param point the point to set
     * @param event the event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
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
        revertList.add(initialPixels);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) im.getLayoutParams();
        float width = (float) size.x - lp.rightMargin - lp.leftMargin;
        float height = (float) size.y - lp.bottomMargin - lp.topMargin;
        float scaleW = width / bitmap.getWidth();
        float scaleH = height / bitmap.getHeight();
        matrix = im.getImageMatrix();
        savedMatrix = im.getImageMatrix();
        float scale = scaleW > scaleH ? scaleH : scaleW;
        matrix.setScale(scale, scale);
        savedMatrix.setScale(scale, scale);

        Log.i("aya", "" + scaleW + " " + scaleH + " " + scale + " " + getBitmapPositionInsideImageView(im));
    }

    /**
     * Returns the bitmap position inside an imageView.
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;
        Log.i("aya", "" + top + " " + left + " " + actW + " " + actH);

        ret[0] = left;
        ret[1] = top;

        return ret;
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
     * Refresh the revert and invert buttons if they are clicked.
     */
    private void refreshButton() {
        revert.setEnabled(revertList.size() > 1 ? true : false);
        invert.setEnabled(invertList.size() > 0 ? true : false);
        revert.setVisibility(revertList.size() > 1 ? View.VISIBLE : View.INVISIBLE);
        invert.setVisibility(invertList.size() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Refresh the revert and invert buttons when the user use an algorithm on the image.
     */
    private void refreshAction() {
        revert.setEnabled(revertList.size() > 1 ? true : false);
        revert.setVisibility(revertList.size() > 1 ? View.VISIBLE : View.INVISIBLE);
        invertList.clear();
        invert.setEnabled(false);
        invert.setVisibility(View.INVISIBLE);
    }

}
