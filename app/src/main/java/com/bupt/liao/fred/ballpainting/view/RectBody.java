package com.bupt.liao.fred.ballpainting.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by fred on 2017/7/14.
 */

public class RectBody {
    float x, y, width, height;

    public RectBody(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(x, y, x + width, y + height, paint);// 绘画矩形
    }

}
