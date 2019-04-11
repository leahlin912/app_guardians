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
import android.util.Log;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class Blasting extends WorldObj{

    public static final int BLASTING_LEVEL1_DIAMETER = 230;
    public static final int BLASTING_LEVEL2_DIAMETER = 380;
    public static final int BLASTING_LEVEL3_DIAMETER = 530;
    public static final int BLASTING_DELAY = 0;
    public static final int Blasting_SHOW_TIME = 4;  //爆破效果要顯示的幀數

    public static Bitmap BLAST_LEVEL1_BITMAP;
    public static Bitmap BLAST_LEVEL2_BITMAP;
    public static Bitmap BLAST_LEVEL3_BITMAP;

    private boolean toShow;  //判斷是否要顯示爆破效果

    private Paint blastPaint;    //球的畫筆
    private Bitmap blastBitmap;
    private int level; //每個球的大小等級
    private int showTimeCount;  // 爆破效果已顯示的幀數

    private int currentStatus;
    private int currentBitmapPosition;
    private int blastDelayCount;

    //陣列圖檔高和寬及分割成每張小圖的寬度
    private int width;
    private int height;
    private int uniWidth;

    private Rect rectOrigin;
    private Rect rectTarget;

    public Blasting(Context context, BitmapManager bm,  int x, int y, int level){
        super(context);

        this.x = x;
        this.y = y;

        this.level = level;
        this.toShow = true;
        this.showTimeCount = 0;


        this.blastPaint = new Paint();  //Blasting 自用的畫筆
        this.currentBitmapPosition = 0;
        this.blastDelayCount = 0;

//        this.height = this.uniWidth = BLASTING_LEVEL2_DIAMETER;
//        this.width = 3 * this.uniWidth;
//        blastBitmap = bm.getBitmap(R.drawable.blasting_s2, width, height);

        if(BLAST_LEVEL1_BITMAP == null || BLAST_LEVEL2_BITMAP == null){
            genBitmap(bm);
        }

        switch(this.level){
            case 1:
                blastBitmap = BLAST_LEVEL1_BITMAP;
                this.height = this.uniWidth = BLASTING_LEVEL1_DIAMETER;
                this.width = 3 * this.uniWidth;
//                blastBitmap = bm.getBitmap(R.drawable.blasting_s1, width, height);
                break;
            case 2:
//            case 3:
                blastBitmap = BLAST_LEVEL2_BITMAP;
                this.height = this.uniWidth = BLASTING_LEVEL2_DIAMETER;
                this.width = 3 * this.uniWidth;
//                blastBitmap = bm.getBitmap(R.drawable.blasting_s2, width, height);
                break;
            case 3:
                blastBitmap = BLAST_LEVEL3_BITMAP;
                this.height = this.uniWidth = BLASTING_LEVEL3_DIAMETER;
                this.width = 3 * this.uniWidth;
//                blastBitmap = bm.getBitmap(R.drawable.blasting_s3, width, height);
                break;
        }

        // ***爆破效果隨機為不同顏色
        int random = (int)(Math.random() * 4) + 1;
        switch (random) {
            case 1:
                blastPaint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(ColorsFont.lightYellow),
                        PorterDuff.Mode.MULTIPLY));
                break;
            case 2:
                blastPaint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(ColorsFont.blastLightGreen),
                        PorterDuff.Mode.MULTIPLY));
                break;
            case 3:
                blastPaint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(ColorsFont.blastLightBlue),
                        PorterDuff.Mode.MULTIPLY));
                break;
            case 4:
                blastPaint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(ColorsFont.blastLightPink),
                        PorterDuff.Mode.MULTIPLY));
                break;
        }

        this.colCenX = this.x + this.width / 2;
        this.colCenY = this.y + this.height / 2;
        this.colRadius = width/2;

        //剪裁圖檔和顯示
        rectOrigin = new Rect();
        rectTarget = new Rect();

    }

    public void onPaint(Canvas canvas, Paint paint) {
        if(toShow){
            rectOrigin.set(this.uniWidth * currentBitmapPosition, 0,
                    this.uniWidth * (currentBitmapPosition + 1), this.height);
            rectTarget.set(x, y, this.uniWidth + x, this.height + y);
            canvas.drawBitmap(blastBitmap,
                    rectOrigin,
                    rectTarget,
                    this.blastPaint);

//            canvas.drawBitmap(blastBitmap, x, y, this.blastPaint);
        }
    }

    public void switchBlast(){
        if(BLASTING_DELAY >= this.blastDelayCount ){
            this.blastDelayCount++;
        }
        else{
            this.currentBitmapPosition = (this.currentBitmapPosition + 1) % 3;
            this.blastDelayCount = 0;
        }
    }

    @Override
    public void update() {
        switchBlast();

        if(Blasting_SHOW_TIME >= showTimeCount){
            showTimeCount++;
        } else{
            this.toShow = false;
        }
    }

    public boolean getToShow(){
        return this.toShow;
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

    @Override
    public void changeStatus() {

    }

    public static void genBitmap(BitmapManager bm){
        BLAST_LEVEL1_BITMAP = bm.getBitmap(R.drawable.blasting_s1, BLASTING_LEVEL1_DIAMETER * 3, BLASTING_LEVEL1_DIAMETER);
        BLAST_LEVEL2_BITMAP = bm.getBitmap(R.drawable.blasting_s2, BLASTING_LEVEL2_DIAMETER * 3, BLASTING_LEVEL2_DIAMETER);
        BLAST_LEVEL3_BITMAP = bm.getBitmap(R.drawable.blasting_s3, BLASTING_LEVEL3_DIAMETER * 3, BLASTING_LEVEL3_DIAMETER);
    }
}

