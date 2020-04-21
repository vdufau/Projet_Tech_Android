package com.example.myappimage.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myappimage.R;

public class CustomSeekBarDialog extends CustomDialog {
    private int max;
    private int progress;
    // 0 -> luminosity / 1 -> contrast
    private int typeAlgorithm;

    public CustomSeekBarDialog(String title, String description, Context context, int max, int progress, int typeAlgorithm) {
        super(title, description, context);
        this.max = max;
        this.progress = progress;
        this.typeAlgorithm = typeAlgorithm;
        this.setBuilder(createDialog(this));
    }

    @Override
    protected AlertDialog.Builder createDialog(final CustomDialog customDialog) {
        Context context = customDialog.getContext();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(customDialog.getTitle());

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView tv = new TextView(context);
        tv.setPadding(40, 30, 0, 30);
        tv.setText("Valeur choisie : 0");
        layout.addView(tv);

        final SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        seekBar.setKeyProgressIncrement(1);
        layout.addView(seekBar);

        builder.setView(layout);

        if (customDialog.getDescription() != null) {
            builder.setMessage(customDialog.getDescription());
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (typeAlgorithm) {
                    case 0:
                        tv.setText("Valeur choisie : " + (progress - max / 2));
                        break;
                    case 1:
                        tv.setText("Valeur choisie : " + (progress / 100.0));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton(context.getString(R.string.validate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int value = seekBar.getProgress();
                customDialog.setValue(value);
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
