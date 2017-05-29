package com.example.yxy.hooknum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Y.X.Y on 2017/5/1 0001.
 */
public class Circle extends View {

    public float radius = 50f;

    public float x = 50f;

    public float y = 50f;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(x, y, radius, paint);
    }
}
