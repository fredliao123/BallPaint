/**
 *
 */
package com.bupt.liao.fred.ballpainting.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.bupt.liao.fred.ballpainting.ColorManager;
import com.bupt.liao.fred.ballpainting.Utils;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.HashMap;

/**
 * @author Fred
 */
public class CircleBody {
    //圆形的宽高与半径
    private float x, y, previousX, previousY, r;
    private HashMap<Path, Integer> pathColorMap;
    private int id;
    private Path currentPath;
    private int idOfBallTouched;
    private World world;
    private boolean isBallTouched;
    private Body body;


    public CircleBody(World world, Body body, float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
        previousX = x;
        previousY = y;
        this.world = world;
        this.body = body;
        pathColorMap = new HashMap<Path, Integer>();
        currentPath = new Path();
        currentPath.moveTo(x, y);
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNewPathAndColor() {
        currentPath = new Path();
        currentPath.moveTo(x, y);
        ColorManager.getInstance().getNewColor();
        pathColorMap.put(currentPath, ColorManager.getInstance().getCurrentColor());
    }

    //绘制圆形
    public void draw(Canvas canvas, Paint paint, Paint paint2) {
        currentPath.lineTo(x, y);
        paint.setColor(pathColorMap.get(currentPath));
        paint.setAlpha(128);
        paint2.setColor(pathColorMap.get(currentPath));
        paint2.setAlpha(128);
        canvas.drawPath(currentPath, paint2);
        canvas.drawArc(new RectF(x - r, y - r, x + r, y + r), 0, 360, true, paint);
        for (Path path : pathColorMap.keySet()) {
            if (!path.equals(currentPath)) {
                paint2.setColor(pathColorMap.get(path));
                paint2.setAlpha(128);
                canvas.drawPath(path, paint2);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float eventX;
        float eventY;
        int Xvelocity = 0;
        int Yvelocity = 0;
        final int action = event.getAction();
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isBallTouched = false;
                eventX = event.getX();
                eventY = event.getY();
                float distance = (float) Math.sqrt(Math.pow((eventX - x), 2) + Math.pow((eventY - y), 2));
                if (distance < (r + Utils.TOUCHRANGE)) {
                    isBallTouched = true;
                    velocityTracker.clear();
                    velocityTracker.recycle();//if don't recycle the velocitytracker here, it will cause memory leak.
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                if (isBallTouched) {
                    velocityTracker.computeCurrentVelocity(100);
                    Xvelocity = (int) velocityTracker.getXVelocity();
                    Yvelocity = (int) velocityTracker.getYVelocity();
                    Vec2 vec2 = new Vec2((float) Xvelocity, (float) Yvelocity);
                    body.applyForce(vec2, body.getWorldCenter());
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        velocityTracker.clear();
        velocityTracker.recycle();
        return false;
    }
}