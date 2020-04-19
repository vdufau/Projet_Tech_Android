package com.example.myappimage.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import com.example.myappimage.R;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorListener;

import static com.example.myappimage.PixelTransformation.myRgbToHsv;

/**
 * CustomColorDialog Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class CustomColorDialog extends CustomDialog {

    public CustomColorDialog(String title, String description, Context context) {
        super(title, description, context);
        this.setBuilder(createDialog(this));
    }

    /**
     * Create a color dialog to choose one color which will be useful to an image transformation algorithm.
     * Use of an external widget : ColorPickerView
     * Link : https://github.com/skydoves/ColorPickerView
     *
     * @param customDialog
     * return the created dialog builder
     */
    @Override
    protected ColorPickerDialog.Builder createDialog(final CustomDialog customDialog) {
        Context context = customDialog.getContext();

        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(context);
        builder.setTitle(customDialog.getTitle());
        if (customDialog.getDescription() != null)
            builder.setMessage(customDialog.getDescription());

        builder.setPositiveButton(context.getString(R.string.validate), new ColorListener() {
            @Override
            public void onColorSelected(int color, boolean fromUser) {
                customDialog.setValue((int) myRgbToHsv(Color.red(color), Color.green(color), Color.blue(color))[0]);
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(false);

        return builder;
    }
}
