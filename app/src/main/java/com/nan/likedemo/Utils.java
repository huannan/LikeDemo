package com.nan.likedemo;

import android.content.res.Resources;

public class Utils {

    public static int getDisplayWidth() {
        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        return w;
    }

    public static int getDisplayHeight() {
        int h = Resources.getSystem().getDisplayMetrics().heightPixels;
        return h;
    }
}
