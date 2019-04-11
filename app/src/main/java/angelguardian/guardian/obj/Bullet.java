package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;


public class Bullet extends WorldObj{
    protected static final String TAG = "Bullet";
    protected static final int BULLET_1ALL = 0;
    protected static final int BULLET_1OUT = 1;
    protected static final int BULLET_3ALL = 2; //三顆都留下來
    protected static final int BULLET_3A = 3; //第一顆留下來
    protected static final int BULLET_3C = 4; //第三顆留下來
    protected static final int BULLET_3OUT = 5; //三顆都沒打到
    protected static final int BULLET_5ALL = 6;
    protected static final int BULLET_5A = 7;
    protected static final int BULLET_5ABC = 8;
    protected static final int BULLET_5E = 9;
    protected static final int BULLET_5CDE = 10;
    protected static final int BULLET_5OUT = 11;

    private int bulletType = 1;

    private int currentStatus;
    private int radius;

    private static Bitmap bulletBitmap;
    private int bulletBottom;

    private int bitmapWidth;
    private int unitWidth;
    private int bitmapHeight;
    private int paintX; //畫圖的起始座標
    private int paintY;
    private int cutRight;
    private int cutLeft;

    protected int colCenX1; //偵測碰撞範圍中心點的x座標
    protected int colCenX2;
    protected int colCenX3;
    protected int colCenX4;
    protected int colCenX5;

    private int firePower; //子彈火力
    private static Rect rectOrigin;
    private Rect rectTarget;

    public Bullet(Context context, BitmapManager bm, int x, int y, int bulletType){
        super(context);

        bitmapWidth = 130;
        bitmapHeight = 46;
        unitWidth = bitmapWidth / 5;

//        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 60;
        this.colRadius = unitWidth / 2;
        this.bulletBottom = this.y + this.bitmapHeight;
        this.firePower = 1;

        if(bulletBitmap == null) {
            bulletBitmap = bm.getBitmap(R.drawable.darkgraybullet, bitmapWidth, bitmapHeight);
        }
        if(rectOrigin == null) { //預設值，正確值會在onPaint調整
            rectOrigin = new Rect(0, 0, bitmapWidth, bitmapHeight);
        }
        rectTarget = new Rect(x, y, bitmapWidth + x, bitmapHeight + y);

        this.bulletType = bulletType;
        setBulletTypeL(this.bulletType);
        switch(bulletType) {
            case 1:
                this.x = x - unitWidth / 2;
                break;
            case 3:
                this.x = x - unitWidth * 3 / 2;
                break;
            case 5:
                this.x = x - unitWidth * 5 / 2;
                break;
        }
    }
    public void setCurrentStatus(int a){
        this.currentStatus = a;
    }

    public int getCurrentStatus(){
        int a = this.currentStatus;
        return a;
    }
    @Override
    public void onPaint(Canvas canvas, Paint paint){
        if(rectOrigin == null) {
            rectOrigin = new Rect(0, 0, bitmapWidth, bitmapHeight);
        }
        rectTarget = new Rect(x, y, bitmapWidth + x, bitmapHeight + y);

        setBulletTypeL(bulletType);
        switch(currentStatus){
            case BULLET_1ALL:
            case BULLET_3ALL:
            case BULLET_5ALL:
                rectOrigin.set(0,0, cutRight, bitmapHeight);
                rectTarget.set(this.x, this.y, this.x +cutRight, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
                break;
            case BULLET_1OUT: //目前沒用到
            case BULLET_3A:
                rectOrigin.set(0,0, cutRight / 3, bitmapHeight);
                rectTarget.set(this.x, this.y, this.x +cutRight / 3, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
                break;
            case BULLET_3C:
                rectOrigin.set(0,0, cutRight / 3, bitmapHeight);
                rectTarget.set(this.x + unitWidth * 2, this.y, this.x + unitWidth * 2 +cutRight / 3, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
                break;
            case BULLET_3OUT: //目前沒用到
            case BULLET_5A:
                rectOrigin.set(0,0, cutRight / 5, bitmapHeight);
                rectTarget.set(this.x, this.y, this.x +cutRight / 5, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
                break;
            case BULLET_5ABC:
                rectOrigin.set(0,0, cutRight / 5 * 3, bitmapHeight);
                rectTarget.set(this.x, this.y, this.x +cutRight / 5 * 3, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
                break;
            case BULLET_5E:
                rectOrigin.set(0,0, cutRight / 5, bitmapHeight);
                rectTarget.set(this.x + unitWidth * 4, this.y, this.x + unitWidth * 4 +cutRight / 5, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
                break;
            case BULLET_5CDE:
                rectOrigin.set(0,0, cutRight / 5 * 3, bitmapHeight);
                rectTarget.set(this.x + unitWidth * 4, this.y, this.x + unitWidth * 2 + cutRight / 5 * 3, this.y + this.bitmapHeight);

                canvas.drawBitmap(bulletBitmap,
                rectOrigin,
                rectTarget,
                null);
//                Log.d(TAG, "onPaint: 5CDE");
                break;
            case BULLET_5OUT: //目前沒用到
//                Log.d(TAG, "onPaint: 5OUT");
        }
//        canvas.drawCircle(bitmapWidth, bitmapHeight,5, paint);
    }

    @Override
    public int getTop(){
        return paintY;
//        return y - radius;
    }
    @Override
    public int getBottom(){
        return this.bulletBottom;
    }
    @Override
    public int getLeft(){
        return cutLeft;
    }
    @Override
    public int getRight(){
        return cutRight;
    }
    @Override
    public void changeStatus(){ }

    public void changeX() {
        switch(bulletType){
            case 1:
                this.x = x - unitWidth / 2;
                break;
            case 2:
                this.x = x - unitWidth * 3 / 2;
                break;
            case 3:
                this.x = x- unitWidth * 5 / 2;
                break;
        }
    }
    @Override
    public void update() {
        this.y -= this.vy;
        this.colCenY = this.y + bitmapWidth / 2;
        this.bulletBottom = this.y + bitmapHeight;
    }
    public int getFirePower(){
        return this.firePower;
    }

    public void setFirePower(int power){
        if(power == 0){
            this.firePower = 1;
        }else {
            this.firePower += power;
        }
    }
    public int getBulletType(){ return this.bulletType; }

    public void setBulletTypeL(int bulletType){
        this.bulletType = bulletType;
        switch(this.bulletType) {
            case 1:
//                this.x = x - unitWidth / 2;
                cutRight = unitWidth;

                this.colCenY = this.y + unitWidth / 2;
                this.colCenX1 = this.x + unitWidth / 2;
//              this.colRadius = unitWidth / 2;
//              this.colCenX = this.x + unitWidth / 2;
//              this.colRadius = bitmapWidth / 2;

//                this.firePower = 1;
                break;
            case 3:
//                this.x = x - unitWidth * 3 / 2;
                cutRight = unitWidth * 3;

                this.colCenY = this.y + unitWidth / 2;
                this.colCenX1 = this.x + unitWidth / 2;
                this.colCenX2 = colCenX1 + unitWidth; //暫無使用
                this.colCenX3 = colCenX2 + unitWidth;
//              this.colRadius = unitWidth * 3 / 2;
//              this.colCenX = this.x + unitWidth / 2;
//              this.colRadius = bitmapWidth / 2;

//                this.firePower = 2;
                break;
            case 5:
//                this.x = x- unitWidth * 5 / 2;
                cutRight = unitWidth * 5;

                this.colCenY = this.y + unitWidth / 2;
                this.colCenX1 = this.x + unitWidth / 2;
                this.colCenX2 = colCenX1 + unitWidth; //暫無使用
                this.colCenX3 = colCenX2 + unitWidth;
                this.colCenX4 = colCenX3 + unitWidth; //暫無使用
                this.colCenX5 = colCenX4 + unitWidth;
//              this.colRadius = unitWidth * 5 / 2;
//              this.colCenX = this.x + unitWidth / 2;
//              this.colRadius = bitmapWidth / 2;

//                this.firePower = 3;
                break;
        }
    }
    public void setBulletTypeR(int bulletType){
        this.bulletType = bulletType;
        switch(this.bulletType) {
            case 1:
//                this.x = x - unitWidth / 2;
                this.x = x + unitWidth * 2;
                cutRight = unitWidth;

                this.colCenY = this.y + unitWidth / 2;
                this.colCenX1 = this.x + unitWidth / 2;
//              this.colRadius = unitWidth / 2;
//              this.colCenX = this.x + unitWidth / 2;
//              this.colRadius = bitmapWidth / 2;

//                this.firePower = 1;
                break;
            case 3:
//                this.x = x - unitWidth * 3 / 2;
                this.x = x + unitWidth * 2;
                cutRight = unitWidth * 3;

                this.colCenY = this.y + unitWidth / 2;
                this.colCenX1 = this.x + unitWidth / 2;
                this.colCenX2 = colCenX1 + unitWidth; //暫無使用
                this.colCenX3 = colCenX2 + unitWidth;
//              this.colRadius = unitWidth * 3 / 2;
//              this.colCenX = this.x + unitWidth / 2;
//              this.colRadius = bitmapWidth / 2;

//                this.firePower = 2;
                break;
            case 5:
//                this.x = x- unitWidth * 5 / 2;
                cutRight = unitWidth * 5;

                this.colCenY = this.y + unitWidth / 2;
                this.colCenX1 = this.x + unitWidth / 2;
                this.colCenX2 = colCenX1 + unitWidth; //暫無使用
                this.colCenX3 = colCenX2 + unitWidth;
                this.colCenX4 = colCenX3 + unitWidth; //暫無使用
                this.colCenX5 = colCenX4 + unitWidth;
//              this.colRadius = unitWidth * 5 / 2;
//              this.colCenX = this.x + unitWidth / 2;
//              this.colRadius = bitmapWidth / 2;

//                this.firePower = 3;
                break;
        }
    }
}
