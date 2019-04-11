package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.text.DecimalFormat;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class Ball extends WorldObj {

    private static final int STATUS_STOP = 0;
    private static final int STATUS_FREE_FULL = 1;
    private static final int STATUS_DISAPPEAR = 2;
    public static final int BALL_VX = 8; //水平移動速度
    public static final int BALL_Level1_DIAMETER = 120;
    public static final int BALL_Level2_DIAMETER = 165;
    public static final int BALL_Level3_DIAMETER = 230;
    public static final int BALL_FUNCTION_NULL = 0;
    public static final int BALL_FUNCTION_FREEZE = 1;
    public static final int BALL_FUNCTION_ALLCLEAR = 2;

    private int width;
    private int height;

    private boolean isSon;  //判斷是否為球的小孩

    private Paint ballPaint;    //球的畫筆
    private Paint textPaint;    //球上數字的畫筆

    private Bitmap ballBitmap;
    private int hp; //每個球的血量值
    private int initHp; //每個球的血量預設值
    private int level; //每個球的大小等級
    private int vyAdjust;
    private int dropDelayCount;
    private int ballFunction;   //球被擊破時的的特殊功能
    private int ballType;  //球的圖案形式

    private Rect rectOrigin;
    private Rect rectTarget;
    private DecimalFormat decimalFormat;  //用來格式化血量要顯示的小數點位數

    public Ball(Context context, BitmapManager bm, int gameLevel, int x, int y, int hp, int level){
        super(context);

        this.x = x;
        this.y = y;
        if(this.x > 0){
            this.vx = -BALL_VX;   //水平移動速度(向左)
        }else{
            this.vx = BALL_VX;   //水平移動速度(向又)
        }
        this.vy = 0;    //上下移動的初始速度

        this.level = level;  //球的大中小等級
        this.ballFunction = BALL_FUNCTION_NULL;


        //*** 球和球上面的字需設定各自的畫筆
        this.ballPaint = new Paint();
        this.textPaint = new Paint();

        this.height = BALL_Level1_DIAMETER;
        this.width = BALL_Level1_DIAMETER;
        ballBitmap = bm.getBitmap(R.drawable.fallingball_s1, width, height);


        //依關卡判斷要顯示的ball圖片形式(ballType)
        if(gameLevel != 0){
            this.ballType = gameLevel % 4;
        } else {
            this.ballType = 1;
        }
        switch(this.ballType){
            case 0:     //第4個場景
                switchSize(bm, R.drawable.ball_bumbman_s1, R.drawable.ball_bumbman_s2, R.drawable.ball_bumbman_s3);
                break;
            case 1:     //第1個場景
                switchSize(bm, R.drawable.ball_drooling_s1, R.drawable.ball_drooling_s2, R.drawable.ball_drooling_s3);
                break;
            case 2:     //第2個場景
                switchSize(bm, R.drawable.fallingball_s1, R.drawable.fallingball_s2, R.drawable.fallingball_s3);
                break;
            case 3:     //第3個場景
                switchSize(bm, R.drawable.bearball_black_s1, R.drawable.bearball_black_s2, R.drawable.bearball_black_s3);
                break;
        }

        //每個球的血量預設值
        if(hp >= 1){
            this.hp = this.initHp = hp;
        }else{
            this.hp = this.initHp = 1;
        }
        if(this.hp >= 1000){
            decimalFormat = new DecimalFormat("#.#"); //用來格式化血量要顯示的小數點位數
        }

        this.colCenX = this.x + this.width / 2;
        this.colCenY = this.y + this.height / 2;
        this.colRadius = width/2;
        this.vyAdjust = 0;
        this.dropDelayCount = 0;
        this.isSon = false;

        int bitmapHeight = height;
        int bitmapWidth = width;

        //圖檔大小 and ball file
        rectOrigin = new Rect(0, 0, ballBitmap.getWidth(), ballBitmap.getHeight());
        rectTarget = new Rect(x, y,ballBitmap.getWidth()+x, ballBitmap.getHeight()+y);
    }

    public Ball(Context context, BitmapManager bm, int gameLevel, int x, int y, int hp, int level, int function){	//***
        this(context, bm, gameLevel, x, y, hp, level);
        this.ballFunction = function;
    }

    public void onPaint(Canvas canvas, Paint paint){
        rectTarget.set(x, y, ballBitmap.getWidth()+x, ballBitmap.getHeight()+y);

        switch(this.ballFunction){   // ***有特殊功能的球設定不同顏色
            case BALL_FUNCTION_FREEZE :
                ballPaint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(ColorsFont.ballBlue),
                        PorterDuff.Mode.MULTIPLY));  //打中會讓所有球動作停止的球, 設定為藍色
                break;
            case BALL_FUNCTION_ALLCLEAR :
                ballPaint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(ColorsFont.ballRed),
                        PorterDuff.Mode.MULTIPLY));  //打中會讓所有球動爆裂的球, 設定為紅色
                break;
            case BALL_FUNCTION_NULL :
                break;
        }

        canvas.drawBitmap(ballBitmap,
                rectOrigin,
                rectTarget,
                ballPaint);

        //*** 設定球上文字的textPaint
        switch(level){
            case 1:
                if(hp < 100){
                    textPaint.setTextSize(60);
                } else if (hp >= 100 && hp < 1000){
                    textPaint.setTextSize(45);
                } else if (hp >= 1000){
                    textPaint.setTextSize(43);
                }
                break;
            case 2:
                if(hp < 100){
                    textPaint.setTextSize(90);
                } else if (hp >= 100 && hp < 1000){
                    textPaint.setTextSize(65);
                } else if (hp >= 1000){
                    textPaint.setTextSize(60);
                }
                break;
            case 3:
                if(hp < 100){
                    textPaint.setTextSize(140);
                } else if (hp >= 100 && hp < 1000){
                    textPaint.setTextSize(100);
                } else if (hp >= 1000){
                    textPaint.setTextSize(90);
                }
                break;
        }
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER); //文字置中
        if(hp < 1000){
            canvas.drawText(Integer.toString(hp),colCenX,colCenY + height / 4, textPaint);  //抓中間的位置;小於1000直接顯示
        }else{
            String hpByKilo = decimalFormat.format(this.hp / 1000d);
            canvas.drawText(hpByKilo + "K",colCenX,colCenY + height / 4, textPaint);  //抓中間的位置;大於1000以Ｋ顯示
        }
    }

    public void move(){
        this.x += vx;
        this.y += vy;
    }

    @Override
    public void setXY(int x, int y) {
        super.setXY(x, y);
        this.colCenX = this.x + width/2;
        this.colCenY = this.y + height/2;
    }

    public void changeStatus(){}

    public void update(){
        move();
        this.colCenX = this.x + width/2;
        this.colCenY = this.y + height/2;
    }

    public void switchSize(BitmapManager bm, int resourceId1, int resourceId2, int resourceId3){
        switch(this.level){
            case 1:
                this.height = BALL_Level1_DIAMETER;
                this.width = BALL_Level1_DIAMETER;
                ballBitmap = bm.getBitmap(resourceId1, width, height);
                break;
            case 2:
                this.height = BALL_Level2_DIAMETER;
                this.width = BALL_Level2_DIAMETER;
                ballBitmap = bm.getBitmap(resourceId2, width, height);
                break;
            case 3:
                this.height = BALL_Level3_DIAMETER;
                this.width = BALL_Level3_DIAMETER;
                ballBitmap = bm.getBitmap(resourceId3, width, height);
                break;
        }
    }

    public void setHp(int hp){
        this.hp = hp;
    }

    public int getLevel(){
        return this.level;
    }

    public int getHp(){
        return this.hp;
    }

    public void updateHp(int firePower){
        this.hp -= firePower;
    }

    @Override
    public int getTop(){
        return y;
    }

    @Override
    public int getBottom(){
        return y + height;
    }

    @Override
    public int getLeft() {
        return x;
    }

    @Override
    public int getRight() {
        return x + width;
    }

    public int getVyAdjust() {
        return this.vyAdjust;
    }

    public void setVyAdjust(int t) {
        this.vyAdjust = t;
    }

    public void addVyAdjust(int t) {
        this.vyAdjust += t;
    }

    public int getDropDelayCount() {
        return this.dropDelayCount;
    }

    public void setDropDelayCount(int num) {
        this.dropDelayCount = num;
    }

    public void setIsSun(boolean yes) {  this.isSon = yes; }

    public boolean getIsASon (){
        return isSon;
    }

    public int getInitHp(){
        return initHp;
    }

    public int getBallFunction(){
        return this.ballFunction;
    }

}
