package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class Coin extends WorldObj{
    private static final int STATUS_DISAPPEAR = 0;
    private static final int STATUS_APPEAR_LEFT = 1;
    private static final int STATUS_APPEAR_RIGHT = 2;
    private static final int WIDTH = 55;
    private static final int HEIGHT = 54;
    private static final int GO_UP = 1;
    private static final int GO_DOWN = 2;
    private static final int GO_STOP = 0;
    private static final int g = 1;
    //private int speed;//速度固定？？
    private static final int TIME_DELAY = 10;
    private Bitmap coinBitmap;
    private int currentStatus;
    private int goUpDown;
    private int value;

    private int delay;

    private static Rect rectOrigin;
    private Rect rectTarget;

    public Coin(Context context, BitmapManager bm, int x, int y,int direction, int value){ //建構子
        super(context);
        this.x = x;//起始x座標
        this.y = y;//起始y座標
        this.delay = 0;
        this.goUpDown = GO_UP;
        this.value = value;

        if(direction==1){
            this.vx = -2;//向左，暫放數值
            this.currentStatus = STATUS_APPEAR_LEFT;
        }else {
            this.vx = 2;//向右，暫放數值
            this.currentStatus = STATUS_APPEAR_RIGHT;
        }
        this.vy = -30;//暫放數值

        //init bitmap
        int bitmapWidth = WIDTH;
        int bitmapHeight = HEIGHT;
        //圖檔大小
        coinBitmap = bm.getBitmap(R.drawable.dollar_1,bitmapWidth, bitmapHeight);

        if(rectOrigin == null) {
            int bitmapLeft = 0;
            int bitmapTop = 0;
            int bitmapRight = WIDTH;
            int bitmapBottom = HEIGHT;
            rectOrigin = new Rect(bitmapLeft, bitmapTop, bitmapRight, bitmapBottom);
        }
        rectTarget = new Rect(x, y, WIDTH + x,HEIGHT + y);
    }

    @Override
    public void onPaint(Canvas canvas, Paint paint){

        rectTarget.set(x,y, WIDTH+x,HEIGHT+y);

        canvas.drawBitmap(coinBitmap,
                rectOrigin,
                rectTarget,null);
    }

    public void move(){
        this.x += vx;
        this.y += vy;
    }
    @Override
    public int getBottom(){
        return y+HEIGHT;
    }
    @Override
    public int getTop(){
        return y;
    }
    @Override
    public int getLeft(){
        return x;
    }
    @Override
    public int getRight(){
        return x+WIDTH;
    }
    @Override
    public  void changeStatus(){
        this.currentStatus = STATUS_DISAPPEAR;
    };
    @Override
    public  void update(){
        move();
    }

    public void setGoUpDown(int goDirection){
        this.goUpDown = goDirection;
    }

    public int getGoUpDown(){
        return this.goUpDown;
    }

    public void setCurrentStatus(int currentStatus){
        this.currentStatus = currentStatus;
    }

    public int getCurrentStatus(){
        return this.currentStatus;
    }
    public int getDelay(){//@Leah
        return this.delay;
    };

    public void addDelay(){//@Leah
        this.delay++;
    };

    public void resetDelay(){//@Leah
        this.delay = 0;
    };

    public int getValue(){
        return this.value;
    }

}



