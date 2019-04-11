package angelguardian.guardian.obj;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import org.w3c.dom.Text;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;


public class SecondChance {
    Context context;

    public static final int GAME_CONTINUE = 0;
    public static final int GAME_RESTART = 1;
    public static final int GAME_CLEAR = 2;
    public static int SECONDCHANCE_PRICE = 50;
    private int deviceRight;
    private int deviceBottom;
    private int btnText;
    private int textTitle;
    private int levelText;
    private int continueText;

    private int buttonTop;
    private int buttonBottom;

    private float lenght;
    private float height;

    //GAME CLEAR
    private Bitmap star;
    private static int starWidth;
    private static int starHeight;
    private static Rect starOrigin;
    private Rect starTarget;

    //LOSE CHAR
    private Bitmap loseChar;
    private static final int LOSE_CHAR_WIDTH = 394;
    private static final int LOSE_CHAR_HEIGHT = 654;
    private static Rect loseCharOrigin;
    private Rect loseCharTarget;

    //REAL LOSE CHAR
    private Bitmap realLoseChar;
    private static final int RL_LOSE_CHAR_WIDTH = 316;
    private static final int RL_LOSE_CHAR_HEIGHT = 642;
    private static Rect rlLoseCharOrigin;
    private Rect getRlLoseCharTarget;

    //COIN
    private Bitmap coinContinue;
    private static int COIN_C_SIDE = 52;
    private static Rect coinCOrigin;
    private Rect coinCTarget;

    public interface SecondChanceInterface{
        public void updateStatus(int status);
    }

    private SecondChanceInterface secondChanceInterface;

    public SecondChance(Context context, BitmapManager bm){
        this.context = context;
    }

    public SecondChance(Context context, BitmapManager bm, SecondChanceInterface secondChanceInterface){
        this.context = context;
        this.secondChanceInterface = secondChanceInterface;

        this.deviceRight = context.getResources().getDisplayMetrics().widthPixels;
        this.deviceBottom = context.getResources().getDisplayMetrics().heightPixels;

        //STAR
        //Text size
        if(deviceRight >= 1080){
            this.btnText = 55;
            this.textTitle = 90;
            this.levelText = 180;
            this.continueText = 70;

            this.starWidth = 450;
            this.starHeight = 433;

            this.star = bm.getBitmap(R.drawable.starcs2, starWidth, starHeight);

        } else {
            this.btnText = 40;
            this.textTitle = 60;
            this.levelText = 150;
            this.continueText = 45;

            this.starWidth = 366;
            this.starHeight = 352;

            this.star = bm.getBitmap(R.drawable.starcs, starWidth, starHeight);
        }

        starOrigin = new Rect(0, 0, starWidth, starHeight);
        starTarget = new Rect((int)(this.deviceRight/2f - starWidth/2f), (int)(this.deviceBottom * 0.3),
                    (int)(this.deviceRight/2f + starWidth/2f), (int)(this.deviceBottom * 0.3 + starHeight));

        //LOSE CHAR
        this.loseChar = bm.getBitmap(R.drawable.losechar, LOSE_CHAR_WIDTH, LOSE_CHAR_HEIGHT);
        this.loseCharOrigin = new Rect(0, 0, LOSE_CHAR_WIDTH, LOSE_CHAR_HEIGHT);
        this.loseCharTarget = new Rect((int)(this.deviceRight / 2f - starWidth / 2f), (int)(this.deviceBottom * 0.25),
                (int)(this.deviceRight / 2f + LOSE_CHAR_WIDTH / 2f), (int)(this.deviceBottom * 0.25 + LOSE_CHAR_HEIGHT));

        //REAL LOSE
        this.realLoseChar = bm.getBitmap(R.drawable.lose_char, RL_LOSE_CHAR_WIDTH, RL_LOSE_CHAR_HEIGHT);
        this.rlLoseCharOrigin = new Rect(0, 0, RL_LOSE_CHAR_WIDTH, RL_LOSE_CHAR_HEIGHT);
        this.getRlLoseCharTarget =  new Rect((int)(this.deviceRight/2 - RL_LOSE_CHAR_WIDTH/2), (int)(this.deviceBottom * 0.25),
                (int)(this.deviceRight / 2f + RL_LOSE_CHAR_WIDTH / 2f), (int)(this.deviceBottom * 0.25 + RL_LOSE_CHAR_HEIGHT));

        //COIN
        this.coinContinue = bm.getBitmap(R.drawable.cs_dollar, COIN_C_SIDE, COIN_C_SIDE);
        this.coinCOrigin = new Rect(0, 0, COIN_C_SIDE, COIN_C_SIDE);
        this.coinCTarget = new Rect();
    }

    public void updateSecondChancePrice(){
        if(Data.gamelevel <= 5){
            this.SECONDCHANCE_PRICE = 50;
        }else if(Data.gamelevel > 5 && Data.gamelevel <= 10){
            this.SECONDCHANCE_PRICE = 100;
        }else if(Data.gamelevel > 10 && Data.gamelevel <= 20){
            this.SECONDCHANCE_PRICE = 300;
        }else if (Data.gamelevel > 20 && Data.gamelevel <= 50){
            this.SECONDCHANCE_PRICE = 500;
        }else{
            this.SECONDCHANCE_PRICE = 1000;
        }
    }

    public void onPaint(Canvas canvas, Paint paint){
        //TypeFace
        paint.setTypeface(Font.fredokafont);

        paint.setColor(Color.parseColor(ColorsFont.transGraySC));
        canvas.drawRect(0, 0, this.deviceRight, this.deviceBottom, paint);

        //RECTANGLE
        paint.setColor(Color.parseColor(ColorsFont.transWhiteSC));

        canvas.drawRect((int) (this.deviceRight * 0.05), (int) (this.deviceBottom * 0.05),
                (int) (this.deviceRight * 0.95), (int) (this.deviceBottom * 0.92), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor(ColorsFont.darkOrange));
        canvas.drawRect((int) (this.deviceRight * 0.05), (int) (this.deviceBottom * 0.05),
                (int) (this.deviceRight * 0.95), (int) (this.deviceBottom * 0.92), paint);
        paint.setStyle(Paint.Style.FILL);

        //GAME CLEAR
        if(World.passCountOfBreak == World.countOfBreak){
            paint.setColor(Color.parseColor(ColorsFont.normalGray));
            paint.setTextSize(textTitle);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            lenght = paint.measureText(TextKeeper.CLEAR)/2;
            canvas.drawText(TextKeeper.CLEAR , (int) (this.deviceRight/2 - lenght), (int) (this.deviceBottom * 0.2), paint);

            //Draw Star & text on it
            canvas.drawBitmap(star, starOrigin, starTarget, null);

            paint.setTextSize(textTitle);
            paint.setTypeface(Font.fredokafont);
            lenght = paint.measureText(TextKeeper.LEVELUP)/2;
            canvas.drawText(TextKeeper.LEVELUP, (int) (this.deviceRight/2 - lenght), (int) (this.deviceBottom * 0.262), paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(levelText);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            lenght = paint.measureText("" + Data.gamelevel)/2;
            canvas.drawText(Integer.toString(Data.gamelevel), (int)(this.deviceRight/2) - lenght, (int)((this.deviceBottom * 0.3) + starHeight/3 * 2), paint);

            //Bonus Coins
            paint.setColor(Color.parseColor(ColorsFont.darkGrayM));
            paint.setTextSize(continueText);
            lenght = paint.measureText(TextKeeper.EARN + World.earnCoin + TextKeeper.BONUS + World.bonusCoin )/2;
            canvas.drawText(TextKeeper.EARN + World.earnCoin + TextKeeper.BONUS + World.bonusCoin, (int)(this.deviceRight/2) - lenght, (int)(this.deviceBottom * 0.7), paint);


            paint.setColor(Color.parseColor(ColorsFont.darkGrayM));
            paint.setTextSize(btnText);
            lenght = paint.measureText(TextKeeper.CLICKTOCONTINUE)/2;
            canvas.drawText(TextKeeper.CLICKTOCONTINUE, (int)(this.deviceRight/2) - lenght, (int)(this.deviceBottom * 0.75), paint);

            return;
        }

        if(Chara.charaLife > 0) {
            //SECOND CHANCE
            updateSecondChancePrice(); //將買命的錢做更新
            //BUTTONS
            paint.setColor(Color.parseColor(ColorsFont.darkBGreen));
            buttonTop = (int) (this.deviceBottom * 0.78);
            buttonBottom = (int) (this.deviceBottom * 0.88);

            canvas.drawRect((int) (this.deviceRight * 0.1), buttonTop,
                    (int) (this.deviceRight * 0.48), buttonBottom, paint);

            //LOSE CHAR
            canvas.drawBitmap(loseChar, loseCharOrigin, loseCharTarget, null);

            if(SECONDCHANCE_PRICE > World.wallet) {
                paint.setColor(Color.parseColor(ColorsFont.darkBGray));
            }

            canvas.drawRect((int) (this.deviceRight * 0.52), buttonTop,
                    (int) (this.deviceRight * 0.9), buttonBottom, paint);


            //BUTTONS TEXT
            paint.setColor(Color.WHITE);
            paint.setTextSize(btnText);
            lenght = paint.measureText(TextKeeper.LEVEL)/2;
            paint.setTypeface(Typeface.create("Arial Rounded", Typeface.BOLD));
            canvas.drawText(TextKeeper.GIVEUP, (int) ((this.deviceRight * 0.58)/2 - lenght), (int) (buttonTop + (buttonBottom - buttonTop)/2) + 20, paint);


            lenght = paint.measureText(TextKeeper.CONTINUE)/2;
            canvas.drawText(TextKeeper.CONTINUE, (int) (this.deviceRight * 1.42/2 - lenght + 10), (int) (buttonTop + (buttonBottom - buttonTop)/2) + 35, paint);

            coinCTarget.set((int) (this.deviceRight * 1.42/2 - lenght) - COIN_C_SIDE, (int) (buttonTop + (buttonBottom - buttonTop)/2) + 20 - COIN_C_SIDE, (int) (this.deviceRight * 1.42/2 - lenght),(int) (buttonTop + (buttonBottom - buttonTop)/2) + 20);
            canvas.drawBitmap(this.coinContinue, coinCOrigin, coinCTarget, null);

            paint.setTextSize(btnText - 10);
            canvas.drawText(TextKeeper.PAY  + Integer.toString(this.SECONDCHANCE_PRICE), (int) (this.deviceRight * 1.42/2 - lenght + 10), (int) (buttonTop + (buttonBottom - buttonTop)/2) - 10, paint);

            //TRY AGAIN
            paint.setColor(Color.parseColor(ColorsFont.normalGray));
            paint.setTextSize(textTitle);
            paint.setTypeface(Font.fredokafont);
            lenght = paint.measureText(TextKeeper.TRYAGAIN)/2;
            canvas.drawText(TextKeeper.TRYAGAIN, (int) (this.deviceRight/2 - lenght), (int) (this.deviceBottom * 0.15), paint);

//            //MESSAGE PAY
//            paint.setColor(Color.parseColor(ColorsFont.darkGrayM));
//            paint.setTypeface(Font.fredokafont);
//            paint.setTextSize(btnText);
//            lenght = paint.measureText(TextKeeper.PAY + this.SECONDCHANCE_PRICE + TextKeeper.TOCONTINUE)/2;
//            canvas.drawText(TextKeeper.PAY  + this.SECONDCHANCE_PRICE + TextKeeper.TOCONTINUE, (int) (this.deviceRight/2 - lenght), (int) (this.deviceBottom * 0.2), paint);

            //MESSAGE COIN
            paint.setColor(Color.parseColor(ColorsFont.darkGrayM));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(btnText);
            lenght = paint.measureText(TextKeeper.COINCS + Data.wallet)/2;
            canvas.drawText(TextKeeper.COINCS  + World.wallet, (int) (this.deviceRight/2 - lenght), (int) (this.deviceBottom * 0.74), paint);

            if(SECONDCHANCE_PRICE > World.wallet) {
                paint.setColor(Color.parseColor(ColorsFont.darkGrayM));
                paint.setTypeface(Font.fredokafont);
                paint.setTextSize(btnText);
                lenght = paint.measureText(TextKeeper.NOT_ENOUGHT) / 2;
                canvas.drawText(TextKeeper.NOT_ENOUGHT, (int) (this.deviceRight / 2 - lenght), (int) (this.deviceBottom * 0.66), paint);
                lenght = paint.measureText(TextKeeper.ENOUGHT_MONEY) / 2;
                canvas.drawText(TextKeeper.ENOUGHT_MONEY, (int) (this.deviceRight / 2 - lenght), (int) (this.deviceBottom * 0.69), paint);
            }


        } else {
            //GAME LOSE
            paint.setColor(Color.parseColor(ColorsFont.normalGray));
            paint.setTextSize(textTitle);
//            paint.setTypeface(Typeface.DEFAULT_BOLD);
            lenght = paint.measureText(TextKeeper.LOSE)/2;
            canvas.drawText(TextKeeper.LOSE, (int) (this.deviceRight/2 - lenght), (int) (this.deviceBottom * 0.2), paint);

            canvas.drawBitmap(realLoseChar, rlLoseCharOrigin, getRlLoseCharTarget, null);
        }

    }

    public boolean onTouch(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                //GAME CLEAR
                if(World.passCountOfBreak == World.countOfBreak){
                    secondChanceInterface.updateStatus(GAME_CLEAR);
                    Chara.resetCharaLife();
                    break;
                }

                // SECOND CHANCE OR LOSE
                if(Chara.charaLife > 0) {
                    //SECOND CHANCE
                    //PRESS TO CONTINUE THE GAME

                    if (x > this.deviceRight * 0.52 && x < this.deviceRight * 0.9) {
                        //If money not enough to buy second chance, cannot click continue
                        if(SECONDCHANCE_PRICE > World.wallet){
                            break;
                        }

                        //If money is enough then let it continue
                        if (y > buttonTop && y < buttonBottom) {
                            secondChanceInterface.updateStatus(GAME_CONTINUE);
                        }
                    }

                    //LOSE -> PRESS TO FINISH GAME --> Return to Menu
                    if (x > (int)(this.deviceRight * 0.1) && x < (int)(this.deviceRight * 0.48)) {
                        if (y > buttonTop && y < buttonBottom) {
                            Chara.resetCharaLife();
                            secondChanceInterface.updateStatus(GAME_RESTART);
                        }
                    }
                } else {
                    Chara.resetCharaLife();
                    secondChanceInterface.updateStatus(GAME_RESTART);
                }

                break;

        }

        return true;
    }
}
