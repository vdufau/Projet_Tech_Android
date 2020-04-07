package com.example.myappimage.dialog;

import android.content.Context;

/**
 * CustomDialog Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public abstract class CustomDialog {
    private String title;
    private String description;
    private Context context;
    private int value;
    private Object builder;

    public CustomDialog(String title, String description, Context context) {
        this.title = title;
        this.description = description;
        this.context = context;
        value = -1;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Context getContext() {
        return context;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Object getBuilder() {
        return builder;
    }

    public void setBuilder(Object builder) {
        this.builder = builder;
    }

    /**
     * Abstract method to create a dialog.
     *
     * @param customDialog
     * @return an object which can be a AlertDialog.Builder or a ColorPickerDialog.Builder
     */
    protected abstract Object createDialog(final CustomDialog customDialog);
}
