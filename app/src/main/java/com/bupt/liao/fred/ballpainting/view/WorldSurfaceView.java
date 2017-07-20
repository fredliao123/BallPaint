package com.bupt.liao.fred.ballpainting.view;

/**
 * Created by fred on 2017/7/14.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import com.bupt.liao.fred.ballpainting.ColorManager;
import com.bupt.liao.fred.ballpainting.R;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import java.util.ArrayList;
import java.util.HashMap;

public class WorldSurfaceView extends SurfaceView implements Callback, Runnable, ContactListener {
    private static final String TAG = "WorldSurfaceView";
    private Thread th;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private Paint paint, paint2, paint3;
    private boolean flag;
    // ----添加一个物理世界---->>
    final float RATE = 30;// 屏幕到现实世界的比例 30px：1m;
    World world;// 声明一个物理世界对象
    AABB aabb;// 声明一个物理世界的范围对象
    Vec2 gravity;// 声明一个重力向量对象
    float timeStep = 1f / 60f;// 物理世界模拟的的频率
    int iterations = 10;// 迭代值，迭代越大模拟越精确，但性能越低
    // --->>给第一个Body赋予力
    Body body1;

    private int ScreenW, ScreenH;
    private int ballCount = 1;
    float radius = 10;
    private int idOfBallTouched = -1;

    private int frameWidthRatio = 20;

    public WorldSurfaceView(Context context) {
        super(context);
        this.setKeepScreenOn(true);
        sfh = this.getHolder();
        sfh.addCallback(this);

        ColorManager.getInstance().getNewColor();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        paint.setColor(ColorManager.getInstance().getCurrentColor());

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Style.STROKE);
        paint2.setColor(ColorManager.getInstance().getCurrentColor());

        ColorManager.getInstance().getNewColor();
        paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setStyle(Style.STROKE);
        paint3.setColor(Color.GREEN);
        //paint3.setColor(getContext().getResources().getColor(R.color.lightblue));


        this.setFocusable(true);
        // --添加一个物理世界--->>
        aabb = new AABB();// 实例化物理世界的范围对象
        gravity = new Vec2(0, 0);// 实例化物理世界重力向量对象
        aabb.lowerBound.set(-100, -100);// 设置物理世界范围的左上角坐标
        aabb.upperBound.set(100, 100);// 设置物理世界范围的右下角坐标
        world = new World(aabb, gravity, true);// 实例化物理世界对象
        world.setContactListener(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        flag = true;
        ScreenW = this.getWidth();
        ScreenH = this.getHeight();
        radius = ScreenW / ballCount / 30;
        paint2.setStrokeWidth(2*radius);
        createBodies();
        th = new Thread(this);
        th.start();
    }

    private void createBodies(){
        int ballCountTemp = ballCount;
        boolean isEven = (ballCountTemp % 2 == 0) ;
        int half = ballCountTemp / 2;
        if(!isEven){

        }
        for (int i = 0; i < ballCount; i++) {
            body1 = createCircle(ScreenW / 2, ScreenH / 2 , radius, false);
            MyCircle circle = (MyCircle)body1.m_userData;
            circle.setId(i + 1);
        }
        createPolygon(-1000f, ScreenH, 100 * ScreenW, 1f, true);//down
        createPolygon(-1f, -1000f, 1f, 100 * ScreenH, true);//left
        createPolygon(-1000f, -1f, 100 * ScreenW, 1f, true);//top
        createPolygon(ScreenW, -1000f, 1f, 100 * ScreenH, true);//right
    }

    public Body createCircle(float x, float y, float r, boolean isStatic) {
        CircleDef cd = new CircleDef();
        if (isStatic) {
            cd.density = 0;
        } else {
            cd.density = 1;
        }
        cd.friction = 0f;
        cd.restitution = 1f;
        cd.radius = r / RATE;
        BodyDef bd = new BodyDef();
        Log.d(TAG, "Screen: "  + ScreenW + " " + ScreenH);
        Log.d(TAG, "createCircle: "  + x + " " + y);
        bd.position.set((x) / RATE, (y ) / RATE);
        Body body = world.createBody(bd);
        body.m_userData = new MyCircle(x, y, r);
        body.createShape(cd);
        body.setMassFromShapes();
        return body;
    }

    public Body createPolygon(float x, float y, float width, float height,
                              boolean isStatic) {
        // ---创建多边形皮肤
        PolygonDef pd = new PolygonDef(); // 实例一个多边形的皮肤
        if (isStatic) {
            pd.density = 0; // 设置多边形为静态
        } else {
            pd.density = 1; // 设置多边形的质量
        }
        pd.friction = 0.8f; // 设置多边形的摩擦力
        pd.restitution = 0.3f; // 设置多边形的恢复力
        // 设置多边形快捷成盒子(矩形)
        // 两个参数为多边形宽高的一半
        pd.setAsBox(width / 2 / RATE, height / 2 / RATE);
        // ---创建刚体
        BodyDef bd = new BodyDef(); // 实例一个刚体对象
        bd.position.set((x + width / 2) / RATE, (y + height / 2) / RATE);// 设置刚体的坐标
        // ---创建Body（物体）
        Body body = world.createBody(bd); // 物理世界创建物体
        body.m_userData = new MyRect(x, y, width, height);
        body.createShape(pd); // 为Body添加皮肤
        body.setMassFromShapes(); // 将整个物体计算打包
        return body;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x;
        float y;
        int Xvelocity = 0;
        int Yvelocity = 0;
        final int action = event.getAction();
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                idOfBallTouched = -1;
                x = event.getX();
                y = event.getY();
                Body body = world.getBodyList();
                for (int i = 1; i < world.getBodyCount(); i++) {
                    if (body.m_userData instanceof MyCircle) {
                        float bodyX = body.getPosition().x * RATE;
                        float bodyY = body.getPosition().y * RATE;
                        float distance = (float)Math.sqrt(Math.pow((x - bodyX), 2) + Math.pow((y - bodyY), 2));
                        if(distance < radius + 15){
                            MyCircle temp = (MyCircle)body.m_userData;
                            idOfBallTouched = temp.getId();
                        }
                    }
                    body = body.m_next;
                }
                velocityTracker.clear();
                velocityTracker.recycle();
                return true;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.computeCurrentVelocity(100);
                Xvelocity = (int)velocityTracker.getXVelocity();
                Yvelocity = (int)velocityTracker.getYVelocity();
                if(idOfBallTouched < 0){
                    break;
                }
                Body body2 = world.getBodyList();
                for (int i = 1; i < world.getBodyCount(); i++) {
                    if (body2.m_userData instanceof MyCircle) {
                        MyCircle temp = (MyCircle)body2.m_userData;
                        if(idOfBallTouched == temp.getId());
                        Vec2 vec2 = new Vec2((float)Xvelocity, (float)Yvelocity);
                        body2.applyForce(vec2, body2.getWorldCenter());
                    }
                    body2 = body2.m_next;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        velocityTracker.clear();
        velocityTracker.recycle();
        return super.onTouchEvent(event);
    }


    public void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);

                Body body = world.getBodyList();
                for (int i = 1; i < world.getBodyCount(); i++) {
                    if (body.m_userData instanceof MyCircle) {
                        ((MyCircle) body.m_userData).draw(canvas, paint, paint2);

                    }
//                    if(body.m_userData instanceof MyRect) {
//                        Log.d("fred", "rect");
//                        //((MyRect) body.m_userData).draw(canvas, paint);
//                    }
                    body = body.m_next;
                }
            }
        } catch (Exception e) {
            Log.e("Himi", "myDraw is Error!");
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    public void Logic() {
        // --开始模拟物理世界--->>
        world.step(timeStep, iterations);// 物理世界进行模拟
        // 取出body链表表头
        Body body = world.getBodyList();
        for (int i = 1; i < world.getBodyCount(); i++) {
            // 设置MyCircle的X，Y坐标
            if (body.m_userData instanceof MyCircle) {
                MyCircle mc = (MyCircle) body.m_userData;
                mc.setX(body.getPosition().x * RATE);
                mc.setY(body.getPosition().y * RATE);
                // 将链表指针指向下一个body元素
            }
            body = body.m_next;
        }
    }

    public void run() {
        while (flag) {
            myDraw();
            Logic();
            try {
                Thread.sleep((long) timeStep * 1000);
            } catch (Exception ex) {
                Log.e("Himi", "Thread is Error!");
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void add(ContactPoint contactPoint) {
        Log.d("fred", "contact");
        if(contactPoint.shape1.m_body.m_userData instanceof MyCircle){
            MyCircle circle = (MyCircle) contactPoint.shape1.m_body.m_userData;
            circle.setNewPathAndColor();
        }
        if(contactPoint.shape2.m_body.m_userData instanceof MyCircle){
            MyCircle circle = (MyCircle) contactPoint.shape2.m_body.m_userData;
            circle.setNewPathAndColor();
        }
//        ColorManager colorManager = ColorManager.getInstance();
//        int color = colorManager.getColor();
//        paint.setColor(color);
//        paint2.setColor(color);
//        mainPath.reset();
//        mainPath.moveTo((contactPoint.position.x* RATE - radius), (contactPoint.position.y* RATE - radius));
    }

    @Override
    public void persist(ContactPoint contactPoint) {

    }

    @Override
    public void remove(ContactPoint contactPoint) {

    }

    @Override
    public void result(ContactResult contactResult) {

    }
}
