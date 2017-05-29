package com.example.yxy.hooknum;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Y.X.Y on 2017/5/29 0029.
 */
public class ActivityCollector {

    public static Activity activity = null;

    public static void add(Activity activity0) {
        if (activity != null) {
            activity.finish();
        }
        activity = activity0;
    }
}
