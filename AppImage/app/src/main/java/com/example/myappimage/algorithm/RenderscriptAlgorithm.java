package com.example.myappimage.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;

/**
 * RenderscriptAlgorithm Class
 *
 * @author Dufau Vincent
 * Link : https://github.com/vdufau/Projet_Tech_Android
 */
public class RenderscriptAlgorithm extends Algorithm {
    private RenderScript rs;

    public RenderscriptAlgorithm(Bitmap bitmap, Context context) {
        super(bitmap, context);
        rs = RenderScript.create(context);
    }

    /**
     * Transform the image in gray scale using renderscript.
     *
     * @return the new pixels
     */
    public int[] toGrayRS() {
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_gray grayScript = new ScriptC_gray(rs);

        grayScript.forEach_toGray(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        grayScript.destroy();
        return getPixels();
    }

    /**
     * Colorize the image using renderscript.
     *
     * @param color the color to apply to the image
     * @return the new pixels
     */
    public int[] colorizeRS(int color) {
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_colorize colorizeScript = new ScriptC_colorize(rs);

        colorizeScript.set_color(color);

        colorizeScript.forEach_colorize(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        colorizeScript.destroy();
        return getPixels();
    }

    /**
     * Keep an interval of colors using renderscript.
     *
     * @param h       the first hue chosen by the user
     * @param secondH the second hue chosen by the user
     * @param inter   the parameter which determine if the colors to keep are between the two hues or not
     * @return the new pixels
     */
    public int[] keepColorRS(int h, int secondH, int inter) {
        Bitmap bitmap = getBitmap();
        int[] interval = keepColorInteval(h, secondH);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_keepColor keepColorScript = new ScriptC_keepColor(rs);

        keepColorScript.set_inter(inter);
        keepColorScript.set_intervalLeft(interval[0]);
        keepColorScript.set_intervalRight(interval[1]);

        keepColorScript.forEach_keepColor(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        keepColorScript.destroy();
        return getPixels();
    }

    /**
     * Extend the pixels values using renderscript.
     *
     * @return the new pixels
     */
    public int[] dynamicExpansionRS() {
        Bitmap bitmap = getBitmap();

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
        return getPixels();
    }

    /**
     * Close the interval of pixels values using renderscript.
     *
     * @param dimChoice the diminution asked by the user
     * @return the new pixels
     */
    public int[] contrastDiminutionRS(int dimChoice) {
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_contrastDiminution contrastDiminutionScript = new ScriptC_contrastDiminution(rs);

        contrastDiminutionScript.set_dimChoice(dimChoice);

        contrastDiminutionScript.forEach_minMax(input);
        contrastDiminutionScript.invoke_initNewMinMaxValues();
        contrastDiminutionScript.invoke_createLUTExpanded();
        contrastDiminutionScript.forEach_applyDiminution(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        contrastDiminutionScript.destroy();
        return getPixels();
    }

    /**
     * Equalize the pixels values using renderscript.
     *
     * @return the new pixels
     */
    public int[] histogramEqualizationRS() {
        Bitmap bitmap = getBitmap();

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
        return getPixels();
    }

    /**
     * Apply an average filter on the image.
     * It will blur the image.
     *
     * @param size the size of the kernel
     * @return the new pixels
     */
    public int[] averageFilterConvolution(int size) {
        float[] kernel = new float[size * size];
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] = 1.f;
        }
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        Allocation kernelAlloc = Allocation.createSized(rs, Element.F32(rs), kernel.length);
        kernelAlloc.copyFrom(kernel);
        Allocation pixelsAlloc = Allocation.createSized(rs, Element.I32(rs), getPixels().length);
        pixelsAlloc.copyFrom(getPixels());

        ScriptC_averageFilter averageScript = new ScriptC_averageFilter(rs);

        averageScript.bind_kmatrix(kernelAlloc);
        averageScript.bind_kpixels(pixelsAlloc);

        float total = 0.f;
        for(float f : kernel){
            total += f;
        }
        averageScript.set_kdiv(total);
        averageScript.set_ksize(size);
        averageScript.set_gIn(input);

        averageScript.invoke_setup();

        averageScript.forEach_root(input, output);

        output.copyTo(bitmap);

        return getPixels();
    }

    /**
     * Apply a snow effect.
     *
     * @return the new pixels
     */
    public int[] snowEffect() {
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_snow snowScript = new ScriptC_snow(rs);

        snowScript.forEach_randomSnow(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        snowScript.destroy();
        return getPixels();
    }
}
