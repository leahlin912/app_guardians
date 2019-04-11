package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class Background extends WorldObj {

    private static Bitmap background;

    private static final int BACKGROUND_ONE = 1;
    private static final int BACKGROUND_TWO = 2;
    private static final int BACKGROUND_THREE = 3;
    private static final int BACKGROUND_FOUR = 4;

    private int backgroundType;

    private int deviceRight;
    private int deviceBottom;
    private int bitmapHeight;
    private int bitmapWidth;

    private static Rect rectOrigin;
    private static Rect rectTarget;

    private BitmapManager bm;


    public Background(Context context, BitmapManager bm, int x, int y){
        super(context);
        this.bm = bm;
        this.x = x;
        this.y = y;
        int level;
        if(Data.gamelevel != 0){
            level = Data.gamelevel % 4;
        } else {
            level = 1;
        }

        if(level == Data.stageFour){
            backgroundType = BACKGROUND_FOUR;
        } else if (level == Data.stageThree){
            backgroundType = BACKGROUND_THREE;
        } else if (level == Data.stageTwo){
            backgroundType = BACKGROUND_TWO;
        } else if (level == Data.stageOne){
            backgroundType = BACKGROUND_ONE;
        }

        this.deviceRight = context.getResources().getDisplayMetrics().widthPixels;
        this.deviceBottom = context.getResources().getDisplayMetrics().heightPixels;

        bitmapHeight = 1920;
        bitmapWidth = 1080;

        rectOrigin = new Rect(0, 0, bitmapWidth, bitmapHeight);
        rectTarget = new Rect(0, 0, this.deviceRight, this.deviceBottom);


    }

    @Override
    public void onPaint(Canvas canvas, Paint paint) {
        canvas.drawBitmap(background,
                rectOrigin,
                rectTarget,
                null);
    }

    public void updateLimit(int botLimit, int rightLimit){
        switchBackground();
        this.deviceBottom = botLimit;
        this.deviceRight = rightLimit;

    }

    @Override
    public int getTop() {
        return 0;
    }

    @Override
    public int getBottom() {
        return 0;
    }

    @Override
    public int getLeft() {
        return 0;
    }

    @Override
    public int getRight() {
        return 0;
    }

    @Override
    public void changeStatus() {

    }

    @Override
    public void update() {

    }

    public void setBackground(int gameLevel){
        if(gameLevel == Data.stageFour){
            backgroundType = BACKGROUND_FOUR;
        } else if (gameLevel == Data.stageThree){
            backgroundType = BACKGROUND_THREE;
        } else if (gameLevel == Data.stageTwo){
            backgroundType = BACKGROUND_TWO;
        } else if (gameLevel == Data.stageOne){
            backgroundType = BACKGROUND_ONE;
        }
        switchBackground();

    }

    public void switchBackground(){
        switch (this.backgroundType){
            case BACKGROUND_ONE:
                background = bm.getBitmap(R.drawable.gamebackground1, bitmapWidth, bitmapHeight);
                break;
            case BACKGROUND_TWO:
                background = bm.getBitmap(R.drawable.gamebackground2, bitmapWidth, bitmapHeight);
                break;
            case BACKGROUND_THREE:
                background = bm.getBitmap(R.drawable.gamebackground3, bitmapWidth, bitmapHeight);
                break;
            case BACKGROUND_FOUR:
                background = bm.getBitmap(R.drawable.gamebackground4, bitmapWidth, bitmapHeight);
                break;
        }

    }
}
