/**
 *
 */
package com.bupt.liao.fred.ballpainting.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bupt.liao.fred.ballpainting.ColorManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Fred
 *
 */
public class MyCircle {
    //圆形的宽高与半径
    float x, y, previousX, previousY, r;
    private HashMap<Path, Integer> pathColorMap;
    int id;
    private Path currentPath;
    public MyCircle(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
        previousX = x;
        previousY = y;
        pathColorMap = new HashMap<Path, Integer>();
        currentPath = new Path();
        currentPath.moveTo(x , y );
        pathColorMap.put(currentPath, ColorManager.getInstance().getCurrentColor());
    }
    //设置圆形的X坐标
    public void setX(float x) {
        previousX = this.x;
        this.x = x;
    }
    //设置半径的Y坐标
    public void setY(float y) {
        previousY = this.y;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNewPathAndColor(){
        currentPath = new Path();
        currentPath.moveTo(x  , y);
        ColorManager.getInstance().getNewColor();
        pathColorMap.put(currentPath, ColorManager.getInstance().getCurrentColor());
    }

    //绘制圆形
    public void draw(Canvas canvas, Paint paint, Paint paint2) {
        currentPath.lineTo(x , y );
        paint.setColor(pathColorMap.get(currentPath));
        paint.setAlpha(128);
        paint2.setColor(pathColorMap.get(currentPath));
        paint2.setAlpha(128);
        canvas.drawPath(currentPath,paint2);
        canvas.drawArc(new RectF(x - r, y - r, x + r, y + r), 0, 360, true, paint);
        for(Path path : pathColorMap.keySet()){
            if(!path.equals(currentPath)){
                paint2.setColor(pathColorMap.get(path));
                paint2.setAlpha(128);
                canvas.drawPath(path,paint2);
            }
        }
    }
}