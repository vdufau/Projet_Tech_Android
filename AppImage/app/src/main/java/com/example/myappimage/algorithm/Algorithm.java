package com.example.myappimage.algorithm;

import android.graphics.Bitmap;

public abstract class Algorithm {
    private Bitmap bitmap;

    public Algorithm(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Calculate the correct interval for the keepColor algorithm.
     *
     * @param color    the hue chosen by the user
     * @param interval the interval to keep
     * @return an array with the left and right intervals and a boolean to know if the pixels to change
     * into grayscale are between the intervals or not
     */
    public int[] keepColorInteval(int color, int interval) {
        int inter = 0;
        int interLeft = color - interval / 2;
        int interRight = color + interval / 2;
        if (interLeft < 0) {
            inter = 1;
            int tmp = interLeft;
            interLeft = interRight;
            interRight = 360 + (tmp % 360);
        }
        if (interRight > 360) {
            inter = 1;
            int tmp = interRight;
            interRight = interLeft;
            interLeft = tmp % 360;
        }

        return new int[]{inter, interLeft, interRight};
    }
}
