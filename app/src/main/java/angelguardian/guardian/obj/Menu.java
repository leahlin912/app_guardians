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
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class Menu{
    Context context;

    private int deviceRight;
    private int deviceBottom;

    private CashShop cashShop;
    private static Bitmap swipeBar;
    private static Bitmap swipeHand;

    private int swipeHeight;
    private int swipeWidth;

    private int handHeight;
    private int handWidth;

    public static final int MENU_STATUS_START = 1; //遊戲開始
    public static final int MENU_STATUS_STOP = 0; //遊戲結束

    //HAND RELATED VARIABLES
    public static final int HAND_TO_RIGHT = 0;
    public static final int HAND_TO_LEFT = 1;
    private int handPosition;
    private int handLeft;
    private int handRight;
    private int handXAdd;
    private int handSpeed;

    //BAR RELATED VARIABLES
    private int xBar;
    private int yBar;

    private int textSize;
    private float lenght;

    private Rect barRectOrigin;
    private Rect barRectTarget;

    //WALLET RELATED VARIABLES
    private int wallet;
    private int power;
    private int speed;
    private double coinMultiplier;
    private int gamelevel;

    private int walletYDisplay;
    private int walletXDisplay;

    //COIN  IMG RELATED VARIABLES
    private Bitmap coinBitmap;
    private int coinImgWidth;
    private int coinImgHeight;

    private static Rect coinrectOrigin;
    private Rect coinrectTarget;

    //HAND
    private Rect handRectOrigin;
    private Rect handRectTarget;

    //MUSIC
    private boolean musicOnOff = true;

    public interface MenuStatusInterface{
        public void updateStatus(Menu menu, int status);
    }

    private MenuStatusInterface menuStatusInterface;

    public Menu(Context context, BitmapManager bm){  //***
        init(context, bm);
    }

    public Menu(Context context, BitmapManager bm, MenuStatusInterface menuStatusInterface){
        init(context, bm);
        this.menuStatusInterface = menuStatusInterface;
        menuStatusInterface.updateStatus(this, MENU_STATUS_START);

        //Coin
        coinImgWidth = 55;
        coinImgHeight = 54;
        coinBitmap = bm.getBitmap(R.drawable.dollar_1, coinImgWidth, coinImgHeight);

        //WALLET
        walletYDisplay = (int)(this.deviceRight * 0.075);
        walletXDisplay = (int)(this.deviceRight * 0.65);

        coinrectOrigin = new Rect(0, 0, coinImgWidth, coinImgHeight);
        coinrectTarget = new Rect( walletXDisplay, walletYDisplay, walletXDisplay + coinImgWidth, walletYDisplay + coinImgHeight);


        //BAR & HAND NEEDED VARIABLES
        this.xBar = this.deviceRight/2 - swipeWidth/2;
        this.yBar = (int)(this.deviceBottom * 0.45);

        //BAR
        barRectOrigin = new Rect( 0, 0, swipeWidth, swipeHeight);
        barRectTarget = new Rect(xBar, yBar, xBar + swipeWidth, yBar + swipeHeight);

        //HAND
        handRectOrigin = new Rect( 0, 0, handWidth, handHeight);
        handRectTarget = new Rect(this.handLeft, yBar + swipeHeight/3, this.handRight, yBar + swipeHeight/3 + handHeight);

    }

    private void init(Context context, BitmapManager bm){

        this.context = context;
        cashShop = new CashShop(context, bm);
        deviceRight = context.getResources().getDisplayMetrics().widthPixels;
        deviceBottom = context.getResources().getDisplayMetrics().heightPixels;

        if(this.deviceRight >= 1080) {
            //BAR
            swipeHeight = 75;
            swipeWidth = 612;

            swipeBar = bm.getBitmap(R.drawable.bar, swipeWidth, swipeHeight);

            //HAND
            this.handPosition = HAND_TO_RIGHT;
            handHeight = 156;
            handWidth = 94;

            swipeHand = bm.getBitmap(R.drawable.pointer, handWidth, handHeight);

            this.handSpeed = 20;
            this.textSize = 70;
        } else {
            //BAR
            swipeHeight = 56;
            swipeWidth = 430;

            swipeBar = bm.getBitmap(R.drawable.barsmall, swipeWidth, swipeHeight);

            //HAND
            this.handPosition = HAND_TO_RIGHT;
            handHeight = 115;
            handWidth = 65;

            swipeHand = bm.getBitmap(R.drawable.pointersmall, handWidth, handHeight);

            this.handSpeed = 15;
            this.textSize = 50;


        }
        this.handXAdd = 0;

    }

    public void onPaint(Canvas canvas, Paint paint){

        this.handLeft = xBar + 20 + this.handXAdd;
        this.handRight = this.handLeft + handWidth;

        //Rec for wallet
        paint.setColor(Color.parseColor(ColorsFont.transGray));
        canvas.drawRect((int)(this.deviceRight * 0.6), (int)(this.deviceBottom * 0.02), this.deviceRight,(int)(this.deviceBottom * 0.1), paint);

        //wallet number display
        paint.setColor(Color.WHITE);

        //game name display:Guardians
        paint.setTextSize(100);
        paint.setTypeface(Font.headline);
        paint.setShadowLayer(5, 5, 5, Color.parseColor(ColorsFont.darkGrayM));
        lenght = paint.measureText(TextKeeper.GUARDIANS)/2;
        canvas.drawText(TextKeeper.GUARDIANS, (int)(this.deviceRight/2 - lenght), (int)(this.deviceBottom * 0.25), paint);
        paint.clearShadowLayer();

        paint.setTextSize(60);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        if(this.wallet >= 1000){
            canvas.drawText(String.format ("%.1f", this.wallet/1000f) + "K", (int)(this.deviceRight * 0.75), (int)(this.deviceBottom * 0.075), paint);
        } else {
            canvas.drawText(Integer.toString(this.wallet), (int)(this.deviceRight * 0.75), (int)(this.deviceBottom * 0.075), paint);
        }

        //Coin
        canvas.drawBitmap(coinBitmap, coinrectOrigin, coinrectTarget,null);

        //SWIPE TO START WORDING
        paint.setColor(Color.parseColor(ColorsFont.darkGrayM));
        if(Data.gamelevel%4 == Data.stageFour){
            paint.setColor(Color.WHITE);
        }

        paint.setTextSize(this.textSize);
        paint.setTypeface(Font.fredokafont);
        lenght = paint.measureText(TextKeeper.SWIPE)/2;
        canvas.drawText(TextKeeper.SWIPE, deviceRight/2 - lenght, yBar - 50, paint);

        //Game level
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        lenght = paint.measureText(TextKeeper.LEVEL)/2;
        canvas.drawText(TextKeeper.LEVEL + Data.gamelevel, deviceRight/2 - lenght, yBar - 150, paint);

        //BAR
        if(Data.gamelevel%4 == Data.stageFour){
            paint.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        }
        canvas.drawBitmap(swipeBar, barRectOrigin, barRectTarget, paint);

        paint.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.DST_IN));

        //HAND
        handRectTarget.set(this.handLeft, yBar + swipeHeight/3, this.handRight, yBar + swipeHeight/3 + handHeight);
        canvas.drawBitmap(swipeHand, handRectOrigin, handRectTarget, null);

        //CASH SHOP
        cashShop.onPaint(canvas, paint);
        System.gc();
    }

    public void updateData(){
        updateHand();
    }

    public void updateLimit(int botLimit, int rightLimit){
        this.deviceBottom = botLimit;
        this.deviceRight = rightLimit;
    }

    public boolean onTouch(MotionEvent event, Data data, Menu menu){
        float x = event.getX();
        float y = event.getY();
        float xFirst = 0;

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:

                //If pressed tabs section
                if(this.cashShop.onTouchTab(x, y, data, menu) == true){
                    break;
                }

                xFirst = x;
                break;
            case MotionEvent.ACTION_MOVE:

                //if pressed areas apart from tab, cashshop area and setting area
                if(y > this.deviceBottom * 0.1 && y < this.deviceBottom * 0.85){
                    //Must move an x distance to start game
                    if(Math.abs(xFirst - x) >= 400) {
                        menuStatusInterface.updateStatus(this, MENU_STATUS_STOP);
                    }
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                //If pressed tabs section
                if(this.cashShop.onTouchUp(x, y, data, menu) == true){
                    break;
                }
                break;

        }
        return true;
    }

    public void updateHand(){

        int farRight = xBar + swipeWidth - 20;
        int farLeft = xBar + 20;

        switch(this.handPosition){
            case HAND_TO_RIGHT:
                if(this.handRight + this.handSpeed <= farRight){
                    this.handXAdd += this.handSpeed;
                } else {
                    this.handXAdd += (farRight - this.handRight);
                }

                if(this.handRight + this.handSpeed >= farRight){
                    this.handPosition = HAND_TO_LEFT;
                }
                break;

            case HAND_TO_LEFT:
                if(this.handLeft - this.handSpeed >= farLeft){
                    this.handXAdd -= this.handSpeed;
                } else {
                    this.handXAdd -= (this.handLeft - farLeft);
                }

                if(this.handLeft <= farLeft){
                    this.handPosition = HAND_TO_RIGHT;
                }

        }

    }

    // DATA RELATED
    public void setWallet(int wallet){
        this.wallet = wallet;
    }

    public void setPower(int power){
        this.power = power;
    }
    public void setSpeed(int speed){
        this.speed = speed;
    }
    public void setCoinMultiplier(double coinMultiplier){
        this.coinMultiplier = coinMultiplier;
    }
    public void setGamelevel(int gamelevel){
        this.gamelevel = gamelevel;
    }

    public int getWallet(){return this.wallet; }
    public int getPower(){return this.power; }
    public int getSpeed(){return this.speed; }
    public double getCoinMultiplier(){return this.coinMultiplier; }
    public int getGamelevel(){return this.gamelevel; }

    //Menu MUSIC
    public void onMusic(MediaPlayer mediaPlayer){
        mediaPlayer.setLooping(musicOnOff);
        mediaPlayer.start();
    }

}
