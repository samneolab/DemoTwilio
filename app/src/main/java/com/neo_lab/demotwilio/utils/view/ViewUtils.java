package com.neo_lab.demotwilio.utils.view;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sam_nguyen on 25/04/2017.
 */

public class ViewUtils {

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
