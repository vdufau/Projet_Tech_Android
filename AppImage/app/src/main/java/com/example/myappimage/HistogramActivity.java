package com.example.myappimage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

/**
 * HistogramActivity Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_L3
 */
public class HistogramActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private GraphView graphView;

    /**
     * Initialization of the activity.
     * Initialization of the histogram.
     * Listener to a button to return in the main activity.
     *
     * @param savedInstanceState the data to initialize if there is a save thanks to onSaveInstanceState (it will not be the case in this application)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bitmap = BitmapSingleton.getInstance().getBitmap();
        graphView = findViewById(R.id.graph);

        Button buttonReturn = (Button) findViewById(R.id.back);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        histogram();
    }

    /**
     * Create the histogram from the bitmap.
     * 
     * Use of an external widget : GraphView
     * Link : https://github.com/jjoe64/GraphView
     */
    private void histogram() {
        int size = 256;
        int[] histRed = new int[size];
        int[] histGreen = new int[size];
        int[] histBlue = new int[size];
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < size; i++) {
            histRed[i] = 0;
            histGreen[i] = 0;
            histBlue[i] = 0;
        }

        for (int i = 0; i < pixels.length; i++) {
            histRed[Color.red(pixels[i])]++;
            histGreen[Color.green(pixels[i])]++;
            histBlue[Color.blue(pixels[i])]++;
        }

        LineGraphSeries<DataPoint> redSeries = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> greenSeries = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> blueSeries = new LineGraphSeries<>();

        for (int i = 0; i < size; i++) {
            redSeries.appendData(new DataPoint(i, histRed[i]), false, Integer.MAX_VALUE);
            greenSeries.appendData(new DataPoint(i, histGreen[i]), false, Integer.MAX_VALUE);
            blueSeries.appendData(new DataPoint(i, histBlue[i]), false, Integer.MAX_VALUE);
        }

        redSeries.setColor(Color.RED);
        greenSeries.setColor(Color.GREEN);
        blueSeries.setColor(Color.BLUE);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0.0);
        graphView.getViewport().setMaxX(256.0);

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalableY(true);
        graphView.getViewport().setScrollableY(true);

        graphView.addSeries(redSeries);
        graphView.addSeries(greenSeries);
        graphView.addSeries(blueSeries);
    }
}
