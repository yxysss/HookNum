package com.example.yxy.hooknum;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import cn.sharesdk.framework.ShareSDK;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Y.X.Y on 2017/4/30 0030.
 */
public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{

    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d("uncaughtException", "uncaughtException");

        ex.printStackTrace();
        /*
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("/storage/emulated/0/Android/data/b/exception.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileHandler == null) return ;
        LogRecord record = new LogRecord(Level.ALL, ex.getMessage());
        fileHandler.publish(record);
        fileHandler.close();
        */

    }

    public void writeToFile(Throwable ex) {

    }

}
