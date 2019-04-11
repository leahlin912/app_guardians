package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class PlusSign extends WorldObj{
    private static final int STATUS_SHOW = 1;
    private static final int STATUS_REMOVE = 0;
    private static final int ADD_ONE = 1;
    private static final int ADD_TWO = 2;
    private static final int ADD_FIVE = 5;
    private static Bitmap pointBitmap;
    private int cutHeight;
    private int cutWidth;
    private int currentBitmapPosition;
    private int x;
    private int y;
    private int delay;
    private int pointNum;
    int bitmapWidth;
    int bitmapHeight;

    private Rect rectOrigin;
    private Rect rectTarget;

    public PlusSign(Context context, BitmapManager bm, int x, int y,int number){
        super(context);
        this.x = x;
        this.y = y;
        this.delay = 0;
        if (number <= 20) {
            pointNum = ADD_ONE;
        } else if (number <= 70){ //para
            pointNum = ADD_TWO;
        }else{
            pointNum = ADD_FIVE;
        }

        //init bitmap
        this.bitmapWidth = 109;
        this.bitmapHeight = 171;

        cutWidth = bitmapWidth/2;
        cutHeight = bitmapHeight/3;

        //圖檔大小
        pointBitmap = bm.getBitmap(R.drawable.plussign, bitmapWidth, bitmapHeight);
        this.currentBitmapPosition = 0;

        rectOrigin = new Rect(0, 0, cutWidth, cutHeight);
        rectTarget = new Rect(x, y, cutWidth + x, cutHeight + y);
    }

    @Override
    public void onPaint(Canvas canvas, Paint paint) {
        rectOrigin.set(cutWidth * (currentBitmapPosition%2),
                cutHeight * (currentBitmapPosition/2),
                cutWidth * (currentBitmapPosition%2 + 1),
                cutHeight * (currentBitmapPosition/2 + 1));
        rectTarget.set(x, y, cutWidth + x, cutHeight + y);

        canvas.drawBitmap(pointBitmap,
                rectOrigin,
                rectTarget,
                null);
    }

    @Override
    public int getTop() {
        return this.y;
    }

    @Override
    public int getBottom() {
        return this.y + cutHeight;
    }

    @Override
    public int getLeft() {
        return this.x;
    }

    @Override
    public int getRight() {
        return this.x + cutWidth;
    }

    @Override
    public void changeStatus() {

    }

    @Override
    public void update() {
        this.y = this.y -20;
        switch(pointNum){
            case ADD_ONE:
                this.currentBitmapPosition = 0;
                //this.currentBitmapPosition = (this.currentBitmapPosition+1)%2;
                break;
            case ADD_TWO :
                this.currentBitmapPosition = 2;
                //this.currentBitmapPosition = (this.currentBitmapPosition+1)%2+2;
                break;
            case ADD_FIVE:
                this.currentBitmapPosition = 4;
                //this.currentBitmapPosition = (this.currentBitmapPosition+1)%2+4;
                break;
        }

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

    public int getPointNum(){
        return this.pointNum;
    }
}
