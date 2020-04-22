package com.example.myappimage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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

    private static final int PERMISSION_CODE = 300;

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

        java = new JavaAlgorithm(bitmap, context);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(this, "Permissions pour la caméra et la sauvegarde refusées", Toast.LENGTH_LONG).show();
            }
        }
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
        final String[] listGauss = getResources().getStringArray(R.array.gaussChoices);
        final String[] listSketch = getResources().getStringArray(R.array.graySketch);
        final int rangeValuesBrightness = 510;
        switch (item.getItemId()) {
            case R.id.gray:
//                toGrayFirstVersion();
                revertList.add(java.toGray());
                refreshAction();
                return true;
            case R.id.grayRS:
                revertList.add(rs.toGray());
                refreshAction();
                return true;
            case R.id.invertColor:
                revertList.add(java.invert());
                refreshAction();
                return true;
            case R.id.invertColorRS:
                revertList.add(rs.invert());
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
                            revertList.add(rs.colorize(value));
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
                        if (h != -1) {
                            final CustomColorDialog keepSecondColorDialog = new CustomColorDialog("Choix des couleurs", "Seconde couleur de l'intervalle", context);
                            ((ColorPickerDialog.Builder) keepSecondColorDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    final int secondH = keepSecondColorDialog.getValue();
                                    if (secondH != -1) {
                                        final CustomRadioDialog intervalDialog = new CustomRadioDialog("Couleurs à garder \nValeurs choisies : " + h + " et " + secondH, null, context, listInterval);
                                        ((AlertDialog.Builder) intervalDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                if (intervalDialog.getValue() != -1) {
                                                    boolean inter = intervalDialog.getValue() == 1;
                                                    revertList.add(java.keepColor(h, secondH, inter));
                                                    refreshAction();
                                                }
                                            }
                                        });
                                        ((AlertDialog.Builder) intervalDialog.getBuilder()).show();
                                    }
                                }
                            });
                            ((ColorPickerDialog.Builder) keepSecondColorDialog.getBuilder()).show();
                        }
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
                        if (h != -1) {
                            final CustomColorDialog keepSecondColorRSDialog = new CustomColorDialog("Choix des couleurs (RS)", "Seconde couleur de l'intervalle", context);
                            ((ColorPickerDialog.Builder) keepSecondColorRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    final int secondH = keepSecondColorRSDialog.getValue();
                                    if (secondH != -1){
                                        final CustomRadioDialog intervalRSDialog = new CustomRadioDialog("Couleurs à garder \nValeurs choisies : " + h + " et " + secondH, null, context, listInterval);
                                        ((AlertDialog.Builder) intervalRSDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                if (intervalRSDialog.getValue() != -1) {
                                                    boolean inter = intervalRSDialog.getValue() == 1;
                                                    revertList.add(rs.keepColor(h, secondH, inter));
                                                    refreshAction();
                                                }
                                            }
                                        });
                                        ((AlertDialog.Builder) intervalRSDialog.getBuilder()).show();
                                    }
                                }
                            });
                            ((ColorPickerDialog.Builder) keepSecondColorRSDialog.getBuilder()).show();
                        }
                    }
                });
                ((ColorPickerDialog.Builder) keepColorRSDialog.getBuilder()).show();
                return true;
            case R.id.brightness:
                final CustomSeekBarDialog brightnessDialog = new CustomSeekBarDialog("Modification de la luminosité", null, this, rangeValuesBrightness, rangeValuesBrightness / 2, 0);
                ((AlertDialog.Builder) brightnessDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = brightnessDialog.getValue();
                        if (value >= 0) {
                            revertList.add(java.brightnessModification(value - (rangeValuesBrightness / 2)));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) brightnessDialog.getBuilder()).show();
                return true;
            case R.id.brightnessRS:
                final CustomSeekBarDialog brightnessDialogRS = new CustomSeekBarDialog("Modification de la luminosité (RS)", null, this, rangeValuesBrightness, rangeValuesBrightness / 2, 0);
                ((AlertDialog.Builder) brightnessDialogRS.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = brightnessDialogRS.getValue();
                        if (value >= 0) {
                            revertList.add(rs.brightnessModification(value - (rangeValuesBrightness / 2)));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) brightnessDialogRS.getBuilder()).show();
                return true;
            case R.id.contrast:
                final CustomSeekBarDialog contrastDialog = new CustomSeekBarDialog("Modification du contraste", null, this, 500, 0, 1);
                ((AlertDialog.Builder) contrastDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        double value = contrastDialog.getValue();
                        if (value >= 0) {
                            revertList.add(java.contrastModification(value / 100.0));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) contrastDialog.getBuilder()).show();
                return true;
            case R.id.contrastRS:
                final CustomSeekBarDialog contrastDialogRS = new CustomSeekBarDialog("Modification du contraste (RS)", null, this, 500, 0, 1);
                ((AlertDialog.Builder) contrastDialogRS.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        double value = contrastDialogRS.getValue();
                        if (value >= 0) {
                            revertList.add(rs.contrastModification(value / 100.0));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) contrastDialogRS.getBuilder()).show();
                return true;
            case R.id.dynamicExpansion:
                revertList.add(java.dynamicExpansion());
                refreshAction();
                return true;
            case R.id.dynamicExpansionRS:
                revertList.add(rs.dynamicExpansion());
                refreshAction();
                return true;
            case R.id.histogramEqualization:
                revertList.add(java.histogramEqualization());
                refreshAction();
                return true;
            case R.id.histogramEqualizationRS:
                revertList.add(rs.histogramEqualization());
                refreshAction();
                return true;
            case R.id.averageFilter:
                final CustomInputDialog averageDialog = new CustomInputDialog("Choix de la taille du noyau de convolution", "Le nombre rentré doit être impair", this);
                ((AlertDialog.Builder) averageDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = averageDialog.getValue();
                        if (value > 0 && value % 2 == 1) {
                            revertList.add(java.blurConvolution(0, value));
                            refreshAction();
                        } else
                            Toast.makeText(context, "Nombre invalide", Toast.LENGTH_LONG).show();
                    }
                });
                ((AlertDialog.Builder) averageDialog.getBuilder()).show();
                return true;
            case R.id.averageFilterRS:
                final CustomInputDialog averageDialogRS = new CustomInputDialog("Choix de la taille du noyau de convolution (RS)", "Le nombre rentré doit être impair", this);
                ((AlertDialog.Builder) averageDialogRS.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = averageDialogRS.getValue();
                        if (value > 0 && value % 2 == 1) {
                            revertList.add(rs.blurConvolution(0, value));
                            refreshAction();
                        } else
                            Toast.makeText(context, "Nombre invalide", Toast.LENGTH_LONG).show();
                    }
                });
                ((AlertDialog.Builder) averageDialogRS.getBuilder()).show();
                return true;
            case R.id.gaussConvolution:
                final CustomRadioDialog gaussDialog = new CustomRadioDialog("Choix de la taille du filtre de Gauss", null, this, listGauss);
                ((AlertDialog.Builder) gaussDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = gaussDialog.getValue();
                        if (value >= 0) {
                            revertList.add(java.blurConvolution(1, Integer.parseInt(listGauss[value])));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) gaussDialog.getBuilder()).show();
                return true;
            case R.id.gaussConvolutionRS:
                final CustomRadioDialog gaussDialogRS = new CustomRadioDialog("Choix de la taille du filtre de Gauss (RS)", null, this, listGauss);
                ((AlertDialog.Builder) gaussDialogRS.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = gaussDialogRS.getValue();
                        if (value >= 0) {
                            revertList.add(rs.blurConvolution(1, Integer.parseInt(listGauss[value])));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) gaussDialogRS.getBuilder()).show();
                return true;
            case R.id.sobelConvolution:
                revertList.add(java.sobelFilterConvolution());
                refreshAction();
                return true;
            case R.id.sobelConvolutionRS:
                revertList.add(rs.sobelFilterConvolution());
                refreshAction();
                return true;
            case R.id.laplacienConvolution:
                revertList.add(java.laplacienFilterConvolution());
                refreshAction();
                return true;
            case R.id.laplacienConvolutionRS:
                revertList.add(rs.laplacienFilterConvolution());
                refreshAction();
                return true;
            case R.id.pencilEffect:
                final CustomRadioDialog graySketchDialog = new CustomRadioDialog("Niveau de détails", null, this, listSketch);
                ((AlertDialog.Builder) graySketchDialog.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = graySketchDialog.getValue();
                        if (value >= 0) {
                            revertList.add(java.sketchEffect(value));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) graySketchDialog.getBuilder()).show();
                return true;
            case R.id.pencilEffectRS:
                final CustomRadioDialog graySketchDialogRS = new CustomRadioDialog("Niveau de détails (RS)", null, this, listSketch);
                ((AlertDialog.Builder) graySketchDialogRS.getBuilder()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int value = graySketchDialogRS.getValue();
                        if (value >= 0) {
                            revertList.add(rs.sketchEffect(value));
                            refreshAction();
                        }
                    }
                });
                ((AlertDialog.Builder) graySketchDialogRS.getBuilder()).show();
                return true;
            case R.id.cartoonEffect:
                revertList.add(java.cartoonEffect());
                refreshAction();
                return true;
            case R.id.cartoonEffectRS:
                revertList.add(rs.cartoonEffect());
                refreshAction();
                return true;
            case R.id.snowEffect:
                revertList.add(java.snowEffect());
                refreshAction();
                return true;
            case R.id.snowEffectRS:
                revertList.add(rs.snowEffect());
                refreshAction();
                return true;
            case R.id.imageIncrustation:
                int[] objectIncrust = java.objectIncrustation();
                if (objectIncrust != null) {
                    revertList.add(objectIncrust);
                    refreshAction();
                } else {
                    Toast.makeText(this, "Pas d'objets à insérer sur cette image", Toast.LENGTH_LONG).show();
                }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        saveImage();
                    }
                } else {
                    saveImage();
                }
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
