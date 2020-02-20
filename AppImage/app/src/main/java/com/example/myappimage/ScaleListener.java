package com.example.myappimage;

public class ScaleListener extends android.view.ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float scale;

    ScaleListener() {
        scale = 1f;
    }

    @Override
    public boolean onScale(android.view.ScaleGestureDetector detector) {
        scale = scale * detector.getScaleFactor();
        scale = Math.max(0.1f, Math.min(scale, 5f));
        MainActivity.getIm().setScaleX(scale);
        MainActivity.getIm().setScaleY(scale);
        return true;
    }
}
