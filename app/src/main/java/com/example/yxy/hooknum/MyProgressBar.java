package com.example.yxy.hooknum;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Y.X.Y on 2017/4/19 0019.
 */
public class MyProgressBar extends View {

    private int backgroundcolor = Color.GRAY;

    private int progresscolor = Color.WHITE;

    private int aroundcolor = Color.BLUE;

    private double progress = 0;

    private double verticallength = 0.9;

    private double horizontallength = 0.9;

    private Paint paint;

    private int left, top, right, bottom;

    private int verticallong, horizontallong;

    public MyProgressBar(Context context) {
        super(context);
        init();
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams(context, attrs);
    }

    private void init() {
        Log.d("init", "init");
        paint = new Paint();
    }

    private void initParams(Context context, AttributeSet attrs) {
        Log.d("initParams", "initParams");
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar);
        if (typedArray != null) {
            Log.d("initParams", "not null");
            backgroundcolor = typedArray.getColor(R.styleable.ProgressBar_backgroundcolor, Color.GRAY);
            progresscolor = typedArray.getColor(R.styleable.ProgressBar_progresscolor, Color.WHITE);
            aroundcolor = typedArray.getColor(R.styleable.ProgressBar_aroundcolor, Color.BLUE);
            progress = typedArray.getFraction(R.styleable.ProgressBar_progress, 1, 1, 0);
            verticallength = typedArray.getFraction(R.styleable.ProgressBar_verticallength, 1, 1, 0.8f);
            horizontallength = typedArray.getFraction(R.styleable.ProgressBar_horizontallength, 1 , 1, 0.98f);
            typedArray.recycle();
        }

    }

    public void setProgress(float progress0, int time) {
        ValueAnimator valueAnimator;
        if (progress0 > 1) progress0 -= 1;
        if (this.progress > progress0) {
            valueAnimator = ValueAnimator.ofFloat((float)this.progress, 1f, progress0);
        } else {
            valueAnimator = ValueAnimator.ofFloat((float)this.progress, progress0);
        }

        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float)animation.getAnimatedValue();
                invalidate();

            }
        });
        valueAnimator.start();

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.left = left;
        this.top = 0;
        this.right = right;
        this.bottom = bottom - top;
        this.verticallong = bottom - top;
        this.horizontallong = right - left;
        Log.d("onLayout", left + "," + top + "," + right + "," + bottom + "," + progress);
    }


    // Canvas中的坐标为绝对坐标！！！
    protected  void  onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("onDraw", "onDraw");
        Log.d("backgroundcolor", (backgroundcolor == Color.GRAY) + "");
        Log.d("aroundcolor", (aroundcolor == Color.BLUE) + "");
        Log.d("location", left + " " + top + " " + right + " " + bottom);
        paint.setColor(aroundcolor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(left, top, right, bottom, (bottom-top)/20, (bottom-top)/20, paint);
        Log.d("location", left + " " + top + " " + right + " " + bottom);
        paint.setColor(backgroundcolor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float left = (float)this.left + (float)horizontallong * (1.0f-(float)horizontallength) * 0.5f;
        float right = (float)this.right - (float)horizontallong * (1.0f-(float)horizontallength) * 0.5f;
        float top = (float)this.top + (float)verticallong * (1.0f-(float)verticallength) * 0.5f;
        float bottom = (float)this.bottom - (float)verticallong * (1.0f-(float)verticallength) * 0.5f;
        canvas.drawRoundRect(left, top, right, bottom, (bottom-top)/20, (bottom-top)/20, paint);
        paint.setColor(progresscolor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float length = right - left;
        float end = left + (float)progress * length;
        canvas.drawRoundRect(left, top, end, bottom, (bottom-top)/20, (bottom-top)/20, paint);
    }

}
