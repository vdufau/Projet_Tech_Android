package com.example.myappimage.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.InputType;
import android.widget.EditText;

import com.example.myappimage.R;

/**
 * CustomInputDialog Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class CustomInputDialog extends CustomDialog {

    public CustomInputDialog(String title, String description, Context context) {
        super(title, description, context);
        this.setBuilder(createDialog(this));
    }

    /**
     * Create an alert dialog with an input to allow the user to choose a number.
     *
     * @param customDialog
     * @return the created dialog builder
     */
    @Override
    protected AlertDialog.Builder createDialog(final CustomDialog customDialog) {
        Context context = customDialog.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(customDialog.getTitle());
        if (customDialog.getDescription() != null)
            builder.setMessage(customDialog.getDescription());

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setView(input);

        builder.setPositiveButton(context.getString(R.string.validate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().matches("")) {
                    int value = Integer.parseInt(input.getText().toString());
                    customDialog.setValue(value);
                }
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        return builder;
    }
}
