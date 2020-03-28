package com.example.myappimage.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

public class RenderscriptAlgorithm extends Algorithm {
    private Context context;

    public RenderscriptAlgorithm(Bitmap bitmap, Context context) {
        super(bitmap);
        this.context = context;
    }

    /**
     * Transform the image in gray scale using renderscript.
     */
    public void toGrayRS() {
        Bitmap bitmap = getBitmap();
        RenderScript rs = RenderScript.create(context);

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
    public void colorizeRS(int color) {
        Bitmap bitmap = getBitmap();
        RenderScript rs = RenderScript.create(context);

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
    public void keepColorRS(int color, int interval) {
        Bitmap bitmap = getBitmap();
        int[] intervals = keepColorInteval(color, interval);

        RenderScript rs = RenderScript.create(context);

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
    public void dynamicExpansionRS() {
        Bitmap bitmap = getBitmap();
        RenderScript rs = RenderScript.create(context);

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
    public void contrastDiminutionRS(int diminution) {
        Bitmap bitmap = getBitmap();
        RenderScript rs = RenderScript.create(context);

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
    public void histogramEqualizationRS() {
        Bitmap bitmap = getBitmap();
        RenderScript rs = RenderScript.create(context);

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
