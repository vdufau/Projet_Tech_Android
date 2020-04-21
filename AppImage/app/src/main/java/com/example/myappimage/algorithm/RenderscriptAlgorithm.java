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
    @Override
    public int[] toGray() {
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
    @Override
    public int[] colorize(int color) {
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
    @Override
    public int[] keepColor(int h, int secondH, boolean inter) {
        Bitmap bitmap = getBitmap();
        int[] interval = keepColorInteval(h, secondH);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_keepColor keepColorScript = new ScriptC_keepColor(rs);

        keepColorScript.set_inter(inter ? 1 : 0);
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
     * Modifies the bitmap's brightness.
     *
     * @param brightness the value to add to each pixel
     * @return the new pixels
     */
    @Override
    public int[] brightnessModification(int brightness) {
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_brightness brightnessScript = new ScriptC_brightness(rs);

        brightnessScript.set_brightness(brightness);

        brightnessScript.forEach_changeBrightness(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        brightnessScript.destroy();
        return getPixels();
    }

    /**
     * Modifies the bitmap's contrast.
     *
     * @param multiplier the diminution asked by the user
     * @return the new pixels
     */
    @Override
    public int[] contrastModification(double multiplier) {
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_contrast contrastScript = new ScriptC_contrast(rs);

        contrastScript.set_multiplier(multiplier);

        contrastScript.forEach_changeContrast(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        contrastScript.destroy();
        return getPixels();
    }

    /**
     * Extend the pixels values using renderscript.
     *
     * @return the new pixels
     */
    @Override
    public int[] dynamicExpansion() {
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
     * Equalize the pixels values using renderscript.
     *
     * @return the new pixels
     */
    @Override
    public int[] histogramEqualization() {
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
     * Apply an average or a gaussian filter on the image.
     * It will blur the image.
     *
     * @param filterType the type of filter : average (0) or gaussian (1)
     * @param size       the size of the kernel
     * @return the new pixels
     */
    @Override
    public int[] blurConvolution(int filterType, int size) {
        float[] kernel = new float[size * size];
        if (filterType == 0) {
            for (int i = 0; i < kernel.length; i++) {
                kernel[i] = 1.f;
            }
        } else {
            if (size == 3) {
                kernel = new float[]{
                        1.f, 2.f, 1.f,
                        2.f, 4.f, 2.f,
                        1.f, 2.f, 1.f
                };
            } else {
                kernel = new float[]{
                        1.f, 2.f, 3.f, 2.f, 1.f,
                        2.f, 6.f, 8.f, 6.f, 2.f,
                        3.f, 8.f, 10.f, 8.f, 3.f,
                        2.f, 6.f, 8.f, 6.f, 2.f,
                        1.f, 2.f, 3.f, 2.f, 1.f
                };
            }
        }

        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        Allocation kernelAlloc = Allocation.createSized(rs, Element.F32(rs), kernel.length);
        kernelAlloc.copyFrom(kernel);
        Allocation pixelsAlloc = Allocation.createSized(rs, Element.I32(rs), getPixels().length);
        pixelsAlloc.copyFrom(getPixels());

        ScriptC_blur blurScript = new ScriptC_blur(rs);

        blurScript.bind_matrix(kernelAlloc);
        blurScript.bind_pixels(pixelsAlloc);

        float total = 0.f;
        for (float f : kernel) {
            total += f;
        }

        blurScript.set_div(total);
        blurScript.set_size(size);
        blurScript.set_gIn(input);

        blurScript.invoke_setup();

        blurScript.forEach_applyFilter(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        blurScript.destroy();
        return getPixels();
    }

    /**
     * Apply a Sobel filter on the image.
     * It will mark the image outlines.
     *
     * @return the new pixels
     */
    @Override
    public int[] sobelFilterConvolution() {
        float[] sobelHorizontal = new float[]{
                -1.0f, 0.0f, 1.0f,
                -2.0f, 0.0f, 2.0f,
                -1.0f, 0.0f, 1.0f
        };
        float[] sobelVertical = new float[]{
                -1.0f, -2.0f, -1.0f,
                0.0f, 0.0f, 0.0f,
                1.0f, 2.0f, 1.0f
        };

        toGray();
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        Allocation sobelHAlloc = Allocation.createSized(rs, Element.F32(rs), sobelHorizontal.length);
        sobelHAlloc.copyFrom(sobelHorizontal);
        Allocation sobelVAlloc = Allocation.createSized(rs, Element.F32(rs), sobelVertical.length);
        sobelVAlloc.copyFrom(sobelVertical);

        Allocation pixelsAlloc = Allocation.createSized(rs, Element.I32(rs), getPixels().length);
        pixelsAlloc.copyFrom(getPixels());

        ScriptC_sobel sobelScript = new ScriptC_sobel(rs);

        sobelScript.bind_sobelHorizontal(sobelHAlloc);
        sobelScript.bind_sobelVertical(sobelVAlloc);
        sobelScript.bind_pixels(pixelsAlloc);
        sobelScript.set_size((int) Math.sqrt(sobelHorizontal.length));

        sobelScript.set_gIn(input);

        sobelScript.invoke_setup();

        sobelScript.forEach_applySobel(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        sobelScript.destroy();
        return getPixels();
    }

    /**
     * Apply a Sobel filter on the image.
     * It will mark the image outlines.
     *
     * @return the new pixels
     */
    @Override
    public int[] laplacienFilterConvolution() {
        float[] laplacien = new float[]{
                1.0f, 1.0f, 1.0f,
                1.0f, -8.0f, 1.0f,
                1.0f, 1.0f, 1.0f
        };

        toGray();
        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        Allocation laplacienAlloc = Allocation.createSized(rs, Element.F32(rs), laplacien.length);
        laplacienAlloc.copyFrom(laplacien);

        Allocation pixelsAlloc = Allocation.createSized(rs, Element.I32(rs), getPixels().length);
        pixelsAlloc.copyFrom(getPixels());

        ScriptC_laplacien laplacienScript = new ScriptC_laplacien(rs);

        laplacienScript.bind_laplacien(laplacienAlloc);
        laplacienScript.bind_pixels(pixelsAlloc);
        laplacienScript.set_size((int) Math.sqrt(laplacien.length));

        laplacienScript.set_gIn(input);

        laplacienScript.invoke_setup();

        laplacienScript.forEach_applyLaplacien(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        laplacienScript.destroy();
        return getPixels();
    }

    /**
     * Apply a cartoon effect to the bitmap.
     *
     * @return the new pixels
     */
    @Override
    public int[] cartoonEffect() {
        blurConvolution(0, 5);

        Bitmap bitmap = getBitmap();

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_cartoon cartoonScript = new ScriptC_cartoon(rs);

        cartoonScript.forEach_cartoonize(input, output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        cartoonScript.destroy();
        return getPixels();
    }

    /**
     * Apply a snow effect.
     *
     * @return the new pixels
     */
    @Override
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
