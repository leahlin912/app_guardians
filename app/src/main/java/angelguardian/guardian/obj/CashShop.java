package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class CashShop extends WorldObj {
    private int deviceRight;
    private int deviceBottom;

    private int shopRectBottom;

    private int cashShop;
    private int xSpace;

    private static final int MENU_BULLETPERSEC = 0;
    private static final int MENU_POWER = 1;
    private static final int MENU_COINS = 2;

    private static Bitmap speedButton;
    private static int speedHeight;
    private static int speedWidth;
//    private BitmapManager speed = new BitmapManager(context);
    private Rect speedRectOrigin;
    private Rect speedRectTarget;

    private static Bitmap powerButton;
    private static int powerHeight;
    private static int powerWidth;
//    private BitmapManager power = new BitmapManager(context);
    private Rect powerRectOrigin;
    private Rect powerRectTarget;

    private static Bitmap coinButton;
    private static int coinHeight;
    private static int coinWidth;
//    private BitmapManager coin = new BitmapManager(context);
    private Rect coinRectOrigin;
    private Rect coinRectTarget;

    private int text_size;
    private int text_size_cost;
    private int text_leftside;
    private int text_bottomside;

    private static int tabYShow;
    private static int tabYHide;

    //Coin
    private static Bitmap coinCS;
//    private BitmapManager coinCSY = new BitmapManager(context);
    private static Bitmap coinCSGr;
//    private BitmapManager coinCSG = new BitmapManager(context);
    private final int coinCSH = 52;
    private final int coinCSW = 52;
    private Rect coinCSOrigin;
    private Rect coinCSTarget;

    private int speedPrice;
    private int powerPrice;
    private int coinPrice;

    private int walletCheck;

    private boolean speedUpdate = false;
    private boolean powerUpdate = false;
    private boolean moneyUpdate = false;

    //音效
    //Soundpool
    private static int clickSound;
    private static int paySound;

    public CashShop(Context context, BitmapManager bm){
        super(context);

        //Can select which tab
        this.cashShop = MENU_BULLETPERSEC;

        this.speedPrice = 100; //各商品初始價錢
        this.powerPrice = 100;
        this.coinPrice = 100;

        this.deviceRight = context.getResources().getDisplayMetrics().widthPixels;
        this.deviceBottom = context.getResources().getDisplayMetrics().heightPixels;

        if(deviceRight >= 1080) {
            //Bitmap

            this.speedHeight = 117;
            this.speedWidth = 137;
            speedButton = bm.getBitmap(R.drawable.speedbutton1, this.speedWidth, this.speedHeight);

            this.powerHeight = 117;
            this.powerWidth = 137;
            powerButton = bm.getBitmap(R.drawable.powerbutton1, this.powerWidth, this.powerHeight);

            this.coinHeight = 117;
            this.coinWidth = 137;
            coinButton = bm.getBitmap(R.drawable.coinbutton1, this.coinWidth, this.coinHeight);

            text_size = 55;
            text_size_cost = 38;

        } else {
            //Bitmap
//            BitmapManager speed = new BitmapManager(context);
            this.speedHeight = 84;
            this.speedWidth = 99;
            speedButton = bm.getBitmap(R.drawable.speedbutton1small, this.speedWidth, this.speedHeight);

//            BitmapManager power = new BitmapManager(context);
            this.powerHeight = 84;
            this.powerWidth = 99;
            powerButton = bm.getBitmap(R.drawable.powerbutton1small, this.powerWidth, this.powerHeight);

//            BitmapManager coin = new BitmapManager(context);
            this.coinHeight = 84;
            this.coinWidth = 99;
            coinButton = bm.getBitmap(R.drawable.coinbutton1small, this.coinWidth, this.coinHeight);

            text_size = 40;
            text_size_cost = 20;
        }

        this.speedPrice = (int)(100 * 2 * Data.bulletPerSec + 100);
        this.powerPrice = (int)(100 * 1.5 * Data.power + 100);
        this.coinPrice = (int)(100 * 1.5 * Data.coinMultiplier + 100);

        //按鍵音效
        clickSound = Music.soundPool.load(context,R.raw.cashshop_click,1);
        paySound = Music.soundPool.load(context,R.raw.cost_click,1);

        text_leftside = (int)(this.deviceRight * 0.06);
        text_bottomside = (int)(this.deviceBottom * 0.9);

        this.tabYShow = speedHeight / 8 * 7;
        this.tabYHide = speedHeight / 8;

        this.shopRectBottom = (int)(this.deviceBottom * 0.85);
        this.xSpace = (int)(this.deviceRight * 0.05);

        //TABS RECT
        //SPEED RECTS
        this.speedRectOrigin = new Rect(0,0, this.speedWidth, this.speedHeight);
        this.speedRectTarget =  new Rect( xSpace, shopRectBottom - this.tabYShow,
                xSpace + this.speedWidth, shopRectBottom + this.tabYHide);

        //POWER RECTS
        this.powerRectOrigin = new Rect(0,0, this.speedWidth, this.speedHeight);
        this.powerRectTarget = new Rect( xSpace + this.speedWidth, shopRectBottom - this.tabYShow,
                xSpace + this.speedWidth + this.powerWidth, shopRectBottom + this.tabYHide);

        //COIN RECTS
        this.coinRectOrigin = new Rect(0,0, this.speedWidth, this.speedHeight);
        this.coinRectTarget = new Rect( xSpace + this.speedWidth + this.powerWidth, shopRectBottom - this.tabYShow,
                xSpace + this.speedWidth + this.powerWidth + this.coinWidth, shopRectBottom + this.tabYHide);

        this.coinCS = bm.getBitmap(R.drawable.cs_dollar, coinCSW, coinCSH);
        this.coinCSGr = bm.getBitmap(R.drawable.cs_dollarg, coinCSW, coinCSH);
        this.coinCSOrigin = new Rect(0, 0, this.coinCSW, this.coinCSH);
        this.coinCSTarget = new Rect((int)(this.deviceRight * 0.76) - coinCSW,
                (int)(this.deviceBottom * 0.905) - (coinCSH/2),
                (int)(this.deviceRight * 0.76),
                (int)(this.deviceBottom * 0.905) + (coinCSH/2));
//        (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.91), paint)
    }

    @Override
    public void onPaint(Canvas canvas, Paint paint) {

        paint.setColor(Color.parseColor(ColorsFont.transWhite));
        canvas.drawRect(0, 0, this.deviceRight, this.deviceBottom, paint);

        //INPUT CASHSHOP TABS
        canvas.drawBitmap(this.speedButton, this.speedRectOrigin, this.speedRectTarget, null);

        canvas.drawBitmap(this.powerButton, this.powerRectOrigin, this.powerRectTarget, null);

        canvas.drawBitmap(this.coinButton, this.coinRectOrigin, this.coinRectTarget, null);

        switch(this.cashShop){
            case MENU_BULLETPERSEC: // add speed 速度

                //RECTANGLE
                paint.setColor(Color.parseColor(ColorsFont.darkRed));
                canvas.drawRect(0, shopRectBottom, this.deviceRight, this.deviceBottom, paint);

                //LINE
                paint.setColor(Color.parseColor(ColorsFont.lightRed));
                paint.setStrokeWidth(10);
                canvas.drawLine(0, this.shopRectBottom, this.deviceRight, shopRectBottom, paint);

                //TEXT
                paint.setColor(Color.WHITE);
                paint.setTextSize(text_size);
                canvas.drawText(TextKeeper.CSSPEED + (Data.bulletPerSec + 1), text_leftside, text_bottomside, paint);
                break;

            case MENU_POWER: //add firepower 火力
                //RECTANGLE
                paint.setColor(Color.parseColor(ColorsFont.darkBlue));
                canvas.drawRect(0, this.shopRectBottom, this.deviceRight, this.deviceBottom, paint);

                //LINE
                paint.setColor(Color.parseColor(ColorsFont.lightBlue));
                paint.setStrokeWidth(10);
                canvas.drawLine(0, this.shopRectBottom, this.deviceRight, this.shopRectBottom, paint);

                //TEXT
                paint.setColor(Color.WHITE);
                paint.setTextSize(text_size);
                canvas.drawText(TextKeeper.CSPOWER + (Data.power + 1), text_leftside, text_bottomside, paint);
                break;
            case MENU_COINS:
                //RECTANGLE
                paint.setColor(Color.parseColor(ColorsFont.darkGreen));
                canvas.drawRect(0, shopRectBottom, this.deviceRight, this.deviceBottom, paint);

                //LINE
                paint.setColor(Color.parseColor(ColorsFont.lightGreen));
                paint.setStrokeWidth(10);
                canvas.drawLine(0, shopRectBottom, this.deviceRight, shopRectBottom, paint);

                //TEXT
                paint.setColor(Color.WHITE);
                paint.setTextSize(text_size);
                canvas.drawText(TextKeeper.CSCOIN + String.format ("%.1f", Data.coinMultiplier), text_leftside, text_bottomside, paint);
                break;
        }

        paint.setColor(Color.parseColor(ColorsFont.buttonGray));
        canvas.drawRect((int)(this.deviceRight * 0.65), (int)(this.deviceBottom * 0.87),
                (int)(this.deviceRight * 0.92), (int)(this.deviceBottom * 0.94), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(text_size_cost);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(TextKeeper.COST, (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.893), paint);

        switch(this.cashShop){
            case MENU_BULLETPERSEC:
                paint.setTextSize(text_size);
                if(this.speedPrice >= 1000){
                    canvas.drawText("" + String.format ("%.1f", this.speedPrice/1000f) + "K", (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.905 + coinCSH/2), paint);
                } else {
                    canvas.drawText("" + speedPrice, (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.905 + coinCSH/2), paint);
                }

                if(Data.wallet >= this.speedPrice){
                    canvas.drawBitmap(this.coinCS, this.coinCSOrigin, this.coinCSTarget, null);
                } else {
                    canvas.drawBitmap(this.coinCSGr, this.coinCSOrigin, this.coinCSTarget, null);
                }

                break;

            case MENU_POWER:
                paint.setTextSize(text_size);
                if(this.powerPrice >= 1000){
                    canvas.drawText("" + String.format ("%.1f", this.powerPrice/1000f) + "K", (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.905 + coinCSH/2), paint);
                } else {
                    canvas.drawText("" + powerPrice, (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.905 + coinCSH/2), paint);
                }

                if(Data.wallet >= this.powerPrice){
                    canvas.drawBitmap(this.coinCS, this.coinCSOrigin, this.coinCSTarget, null);
                } else {
                    canvas.drawBitmap(this.coinCSGr, this.coinCSOrigin, this.coinCSTarget, null);
                }

                break;
            case MENU_COINS:
                paint.setTextSize(text_size);
                if(this.coinPrice >= 1000){
                    canvas.drawText("" + String.format ("%.1f", this.coinPrice/1000f) + "K", (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.905 + coinCSH/2), paint);
                } else {
                    canvas.drawText("" + coinPrice, (int)(this.deviceRight * 0.77), (int)(this.deviceBottom * 0.905 + coinCSH/2), paint);
                }

                if(Data.wallet >= this.coinPrice){
                    canvas.drawBitmap(this.coinCS, this.coinCSOrigin, this.coinCSTarget, null);
                } else {
                    canvas.drawBitmap(this.coinCSGr, this.coinCSOrigin, this.coinCSTarget, null);
                }

                break;
        }



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

    public boolean onTouchUp(float x, float y, Data data, Menu menu){

        if(y > shopRectBottom - this.tabYShow && y < shopRectBottom + this.tabYHide){

            if(x > this.xSpace && x < xSpace + this.speedWidth){
                Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                this.cashShop = MENU_BULLETPERSEC;
                return true;
            } else if (x > xSpace + this.speedWidth && x < xSpace + this.speedWidth + this.powerWidth){
                Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                this.cashShop = MENU_POWER;
                return true;
            } else if (x > xSpace + this.speedWidth + this.powerWidth && x < xSpace + this.speedWidth + this.powerWidth + this.coinWidth) {
                Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                this.cashShop = MENU_COINS;
                return true;
            }
        }


        if(Data.wallet >= speedPrice && this.cashShop == MENU_BULLETPERSEC){
            if(payButton(x, y)){
                Music.soundPool.play(paySound, 1, 1, 0, 0, 0);
                data.reduceWallet(speedPrice);
                menu.setWallet(data.getWallet());
                Log.d("data11 Speed", "Speed: " + data.bulletPerSec);
                data.upDataSpeed(1);
                Log.d("data11 Speed", "Speed: " + data.bulletPerSec);
                this.speedPrice = (int)(100 * 2 * Data.bulletPerSec + 100);
                return true;
            }
        }

        if(Data.wallet >= powerPrice && this.cashShop == MENU_POWER){
            if(payButton(x, y)){
                Music.soundPool.play(paySound, 1, 1, 0, 0, 0);
                data.reduceWallet(powerPrice);
                menu.setWallet(data.getWallet());
                data.updatePower(1);
                this.powerPrice = (int)(100 * 1.5 * Data.power + 100);
                return true;
            }
        }

        if(Data.wallet >= coinPrice && this.cashShop == MENU_COINS){
            if(payButton(x, y)){
                Music.soundPool.play(paySound, 1, 1, 0, 0, 0);
                data.reduceWallet(coinPrice);
                menu.setWallet(data.getWallet());
                data.updateCoinMultiplier(); //每按ㄧ次加乘倍數的更新
                this.coinPrice = (int)(100 * 1.5 * Data.coinMultiplier + 100);
                return true;
            }
        }


        return false;
    }

    public boolean onTouchTab(float x, float y, Data data, Menu menu){

        if(y > shopRectBottom - this.tabYShow && y < shopRectBottom + this.tabYHide){

            if(x > this.xSpace && x < xSpace + this.speedWidth){
                Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                this.cashShop = MENU_BULLETPERSEC;
                return true;
            } else if (x > xSpace + this.speedWidth && x < xSpace + this.speedWidth + this.powerWidth){
                Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                this.cashShop = MENU_POWER;
                return true;
            } else if (x > xSpace + this.speedWidth + this.powerWidth && x < xSpace + this.speedWidth + this.powerWidth + this.coinWidth) {
                Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                this.cashShop = MENU_COINS;
                return true;
            }
        }

        return false;
    }

    //Check if they have click the button in CASH SHOP TO PAY
    public boolean payButton(float x, float y){
        if(y > (int)(this.deviceBottom * 0.87) && y < (int)(this.deviceBottom * 0.932)){
            if(x > (int)(this.deviceRight * 0.65) && x < (int)(this.deviceRight * 0.92)){
                return true;
            }

        }
        return false;
    }
}
