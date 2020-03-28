package com.example.myappimage.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.myappimage.R;

/**
 * CustomRadioDialog Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class CustomRadioDialog extends CustomDialog {

    private String[] list;
    private int index;

    public CustomRadioDialog(String title, String description, Context context, String[] list) {
        super(title, description, context);
        this.list = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            this.list[i] = list[i];
        }
        index = 0;
        this.setBuilder(createDialog(this));
    }

    /**
     * Create an alert dialog with a radio choice.
     *
     * @param customDialog
     * @return the created dialog builder
     */
    @Override
    protected AlertDialog.Builder createDialog(final CustomDialog customDialog) {
        Context context = customDialog.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(customDialog.getTitle());

        builder.setSingleChoiceItems(list, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index = which;
            }
        });

        builder.setPositiveButton(context.getString(R.string.validate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customDialog.setValue(index);
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }
}
