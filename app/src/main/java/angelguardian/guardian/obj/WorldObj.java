package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public abstract class WorldObj { // all attributes of all objects in game
    protected Context context;

    //x, y
    protected int x;
    protected int y;
    protected int vx; //velocity in x
    protected int vy; //velocity y
    protected int colCenX;      //偵測碰撞範圍中心點的x座標
    protected int colCenY;      //偵測碰撞範圍中心點的y座標
    protected int colRadius;    //偵測碰撞範圍的半徑
    protected int times;//@Leah

    public WorldObj(Context context){
        this.context = context;
    }

    public abstract void onPaint(Canvas canvas, Paint paint);

    //INCLUDED BY ME
    public abstract int getTop();
    public abstract int getBottom();
    public abstract int getLeft();
    public abstract int getRight();

    public abstract void changeStatus();
    public abstract  void update();

    public int getVX(){
        return vx;
    }
    public int getVY(){
        return vy;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void updateSpeed(int vx, int vy){
        this.vx = vx;
        this.vy = vy;
    }
    public int getTimes(){//@Leah
        return this.times;
    };

    public void addTimes(){
        this.times++;
    };

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }


}
