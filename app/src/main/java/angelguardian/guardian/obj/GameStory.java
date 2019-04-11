package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class GameStory extends WorldObj{
    public interface GameStoryInterface{
        public void updateStatus(GameStory gameStory, int status);
    }
    private GameStoryInterface gameStoryInterface;

    private int delay;

    public static final int STORY_START = 1;
    public static final int STORY_END = 0;

    private static int storyType;
    private static final int STORY_OFF = 0;
    private static final int STORY_ON_NIGHT = 1;
    private static final int STORY_ON_DAYTIME = 2;
    private static final int STORY_Tutorial = 3;

    private static int letterType;
    private static final int LETTER_HIDE = 0;
    private static final int LETTER_SHOW = 1;
    private int reelDelay;
    private int reelCount;
    private boolean reelfinish;

    private static int pageType;
    private static final int LETTER_NOCONTENT = 0;
    private static final int LETTER_PAGE1 = 1;
    private static final int LETTER_PAGE2 = 2;
    private static final int LETTER_PAGE3 = 3;
    private static final int PAGE_TUTORIAL = 4; //tutorial

    private static int rightArrowType;
    private static final int R_ARROW_HIDE = 0;
    private static final int R_ARROW_ORIGIN = 1;
    private static final int R_ARROW_PRESSED = 2;

    private int deviceRight;
    private int deviceBottom;

    private BitmapManager bm;

    //背景
    private static Bitmap backGroundN;
    private static Bitmap backGroundD;
    private static Bitmap backGroundT;
    private int backgroundHeight;
    private int backgroundWidth;
    private static Rect rectOrigin;
    private static Rect rectTarget;


    //dark 轉場
//    private static Bitmap darkBitmap;
    private static final int DARK_HIDE = 0;
    private static final int DARK_SHOW = 1;
    private static int darkType;
    private int darkRepeat;

    //信紙
    private static Bitmap letter;
    private int letterHeight;
    private int letterWidth;
    private int letterLeft;
    private int letterTop;
    private static Rect letterRectOrigin;
    private static Rect letterRectTarget;

    //Soundpool
    private static int papperSound;
    private static int clickSound;


    //箭頭（共用）
    private int arrowHeight;
    private int arrowCutHeight;
    //右箭頭
    private Bitmap rightarrowBitmap;
    private int arrowWidth;
    private int rightArrowX;
    private int rightArrowY;
    private static Rect rightArrowRectOrigin;
    private static Rect rightArrowRectTarget;
    private static Rect rightArrowRectOrigin_pressed;
    private int arrowMove;

    private String pageContent1;
    private String pageContent2;
    private String pageContent3;
    private String pageContent4;
    private String pageContent5;


    private Paint darkPaint;
    private Paint wordPaint;

    //tutorial
    private Menu menu;
    private static int tutorialType;
    private static final int TUTORIAL_HIDE = 0;
    private static final int TUTORIAL_SHOW = 1;

    private Bitmap tutorialMoney;
    private int tutorialMoneyHeight;
    private int tutorialMoneyWidth;
    private int tutorialMoneyLeft;
    private int tutorialMoneyTop;
    private static Rect tutorialMoneyRectOrigin;
    private static Rect tutorialMoneyRectTarget;

    private Bitmap tutorialPay;
    private int tutorialPayHeight;
    private int tutorialPayWidth;
    private int tutorialPayLeft;
    private int tutorialPayTop;
    private static Rect tutorialPayRectOrigin;
    private static Rect tutorialPayRectTarget;

    private Bitmap tutorialSelect;
    private int tutorialSelectHeight;
    private int tutorialSelectWidth;
    private int tutorialSelectLeft;
    private int tutorialSelectTop;
    private static Rect tutorialSelectRectOrigin;
    private static Rect tutorialSelectRectTarget;
    //震動
    private Vibrator vibrator;

    public GameStory(Context context, BitmapManager bm, Menu menu, GameStoryInterface gameStoryInterface){
        super(context);
        this.bm = bm;
        this.menu = menu;

        delay = 0;
        darkRepeat = 230;
        reelDelay = 0;
        reelCount = 0;
        arrowMove = 0;
        reelfinish = false;

        //所有圖片狀態初始化
        switchPage0();
        letterType = LETTER_HIDE;
        pageType = LETTER_NOCONTENT;
        rightArrowType = R_ARROW_HIDE;
        darkType = DARK_SHOW;
        this.gameStoryInterface = gameStoryInterface;
        this.gameStoryInterface.updateStatus(this, STORY_START);

        //音效
        papperSound = Music.soundPool.load(context,R.raw.letter_show,1);
        clickSound = Music.soundPool.load(context,R.raw.cashshop_click,1);

        darkPaint = new Paint();
        wordPaint = new Paint();

        //背景圖
        this.deviceRight = context.getResources().getDisplayMetrics().widthPixels;
        this.deviceBottom = context.getResources().getDisplayMetrics().heightPixels;
        backgroundWidth = 1080;
        backgroundHeight = 1920;
        rectOrigin = new Rect(0,0,backgroundWidth,backgroundHeight);
        rectTarget = new Rect(0,0,this.deviceRight,this.deviceBottom);
        backGroundN = bm.getBitmap(R.drawable.nightstory, backgroundWidth, backgroundHeight);
        backGroundD = bm.getBitmap(R.drawable.daytimestory, backgroundWidth, backgroundHeight);
        backGroundT = bm.getBitmap(R.drawable.gamebackground1, backgroundWidth, backgroundHeight);

        //信紙圖
        if(deviceRight < 1080){
            letterWidth = 526;
            letterHeight = 949;

            letter = bm.getBitmap(R.drawable.letter2,letterWidth,letterHeight);
        }else {
            letterWidth = 788;
            letterHeight = 1264;
            letter = bm.getBitmap(R.drawable.letter, letterWidth, letterHeight);//待修改
        }
            letterLeft = (int)(this.deviceRight/2f - letterWidth/2f);
            letterTop =  (int)(this.deviceBottom/2f - letterHeight/2f);
            letterRectOrigin = new Rect();//擷取圖片
            letterRectTarget = new Rect(letterLeft,letterTop,letterLeft+letterWidth,letterTop+letterHeight);//展示位置


        //箭頭
        if(deviceRight<1080){
            arrowWidth = 134;
            arrowHeight = 188;
            rightarrowBitmap = bm.getBitmap(R.drawable.arrow_right2,arrowWidth,arrowHeight);
        }else{
            arrowWidth = 200;
            arrowHeight = 280;
            rightarrowBitmap = bm.getBitmap(R.drawable.arrow_right,arrowWidth,arrowHeight);
        }
        arrowCutHeight = (int)(arrowHeight / 2f);
        rightArrowRectOrigin = new Rect (0, arrowCutHeight, arrowWidth, arrowHeight);
        rightArrowRectOrigin_pressed = new Rect (0,0, arrowWidth, arrowCutHeight);
        rightArrowRectTarget = new Rect();

//        darkBitmap = bm.getBitmap(R.drawable.dark, backgroundWidth, backgroundHeight);

        //教學頁面money 箭頭
        tutorialMoneyWidth = 490;
        tutorialMoneyHeight = 210;
        tutorialMoneyLeft = (int)((deviceRight) * 0.72) - tutorialMoneyWidth;
        tutorialMoneyTop = (int)((deviceBottom) * 0.052);
        tutorialMoneyRectOrigin = new Rect(0,0,tutorialMoneyWidth,tutorialMoneyHeight);
        tutorialMoneyRectTarget = new Rect(tutorialMoneyLeft,tutorialMoneyTop,tutorialMoneyLeft + tutorialMoneyWidth,tutorialMoneyTop + tutorialMoneyHeight);

        //教學頁面 pay 箭頭
        tutorialPayWidth = 300;
        tutorialPayHeight = 260;
        tutorialPayLeft = (int)((deviceRight) * 0.85) - tutorialPayWidth;
        tutorialPayTop  = (int)(deviceBottom * 0.89) - tutorialPayHeight;
        tutorialPayRectOrigin = new Rect(0,0,tutorialPayWidth,tutorialPayHeight);
        tutorialPayRectTarget = new Rect(tutorialPayLeft,tutorialPayTop,tutorialPayLeft + tutorialPayWidth, tutorialPayTop + tutorialPayHeight);

        //教學頁面 select 箭頭
        tutorialSelectWidth = 580;
        tutorialSelectHeight = 370;
        tutorialSelectLeft = (int)((deviceRight) * 0.1);
        tutorialSelectTop = (int)((deviceBottom) * 0.82) - tutorialSelectHeight;
        tutorialSelectRectOrigin = new Rect(0,0,tutorialSelectWidth,tutorialSelectHeight);
        tutorialSelectRectTarget = new Rect(tutorialSelectLeft, tutorialSelectTop, tutorialSelectLeft + tutorialSelectWidth, tutorialSelectTop + tutorialSelectHeight);


        tutorialMoney = bm.getBitmap(R.drawable.tutorial_money, tutorialMoneyWidth,tutorialMoneyHeight);
        tutorialPay = bm.getBitmap(R.drawable.tutorial_pay,tutorialPayWidth,tutorialPayHeight);
        tutorialSelect = bm.getBitmap(R.drawable.tutorial_select,tutorialSelectWidth,tutorialSelectHeight);




    }

    public boolean onTouch(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(this.tutorialType == TUTORIAL_SHOW){
                    if(x >= 0 && x <= backgroundWidth){
                        if(y >= 0 && y <= backgroundHeight){
                            switchEnd();
                            shake("on", context);
                        }
                    }
                }else if( x>= rightArrowX && x <= rightArrowX+arrowWidth){
                    if(y >= rightArrowY && y <= rightArrowY+arrowCutHeight){
                        rightArrowType = R_ARROW_PRESSED;
                        Music.soundPool.play(clickSound, 1, 1, 0, 0, 0);
                        shake("on", context);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if(this.tutorialType == TUTORIAL_SHOW){
                    if(x >= 0 && x <= backgroundWidth){
                        if(y >= 0 && y <= backgroundHeight){
                        }
                    }
                }else if( x>= rightArrowX && x <= rightArrowX+arrowWidth){
                    if(y >= rightArrowY && y <= rightArrowY+arrowCutHeight){
                        changeNextPage();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onPaint(Canvas canvas, Paint paint) {
        //畫背景
        switch(this.storyType){
            case STORY_ON_NIGHT:
                canvas.drawBitmap(backGroundN,
                        rectOrigin,
                        rectTarget,
                        null);
                break;
            case STORY_ON_DAYTIME:
                canvas.drawBitmap(backGroundD,
                        rectOrigin,
                        rectTarget,
                        null);
                break;
            case STORY_Tutorial:
                canvas.drawBitmap(backGroundT,
                        rectOrigin,
                        rectTarget,
                        null);
                break;
        }


        //畫dark （fade-in & fade-out效果）
        if(darkType == DARK_SHOW){
            if(darkRepeat>0){
//                darkPaint.setStyle(Paint.Style.STROKE);
                darkPaint.setColor(Color.argb(darkRepeat,0,0,0));
                canvas.drawRect(0,0, this.deviceRight, this.deviceBottom, darkPaint);
//                darkPaint.setAlpha(repeat);
//                canvas.drawBitmap(darkBitmap,
//                        rectOrigin,
//                        rectTarget,
//                        darkPaint);
                darkRepeat -= 5;

            }else if(darkRepeat <= 0) {
                darkRepeat = 0;
                darkType = DARK_HIDE;
            }
        }

        //畫信紙
        if(this.letterType == LETTER_SHOW) {
            letterRectOrigin.set(0,0,letterWidth,letterHeight);
            letterRectTarget.set(letterLeft,letterTop,letterLeft+letterWidth,letterTop+reelCount);//展示位置
            canvas.drawBitmap(letter,
                    letterRectOrigin,
                    letterRectTarget,
                    null);
            if(reelCount == 0){
                Music.soundPool.play(papperSound, 1, 1, 0, 0, 0);
            }
            if(reelCount<letterHeight){
                reelCount += 20;
            }else if(reelCount >= letterHeight){
                reelfinish = true;
            }


//            if(reelCount < 7){
//                reelDelay ++;
//                if(reelDelay%7 == 6) {//捲軸效果
//                    reelCount++;
//                }
//            }else if(reelCount == 7){
//                reelfinish = true;
//            }


        }
        //畫右箭頭
        if(arrowMove%10 <5){
            rightArrowX = (int)((deviceRight / 2f) - (arrowWidth / 2f));
        }else{
            rightArrowX = (int)((deviceRight / 2f) - (arrowWidth / 2f))+10;
        }
        rightArrowY = (int)(letterTop + letterHeight-(arrowHeight/2)*1.3);
//        rightArrowY = (int)((deviceBottom / 8f) * 6f);
        rightArrowRectTarget.set(rightArrowX, rightArrowY,rightArrowX + arrowWidth,rightArrowY + arrowCutHeight);
        if(rightArrowType == R_ARROW_ORIGIN){
            canvas.drawBitmap(rightarrowBitmap,rightArrowRectOrigin,rightArrowRectTarget,null);
        }else if(rightArrowType == R_ARROW_PRESSED){
            canvas.drawBitmap(rightarrowBitmap,rightArrowRectOrigin_pressed,rightArrowRectTarget,null);
        }

        //畫文字
        drawText(canvas);

        //畫教學圖

        if(tutorialType == TUTORIAL_SHOW){
            menu.onPaint(canvas, paint);
            paint.setColor(Color.argb(165,0,0,0));
            canvas.drawRect(0, 0, this.deviceRight, this.deviceBottom, paint);

            canvas.drawBitmap(tutorialMoney, tutorialMoneyRectOrigin, tutorialMoneyRectTarget, null);
            canvas.drawBitmap(tutorialPay, tutorialPayRectOrigin, tutorialPayRectTarget, null);
            canvas.drawBitmap(tutorialSelect, tutorialSelectRectOrigin, tutorialSelectRectTarget, null);
        }
    }

    public void drawText(Canvas canvas){//畫出text
        wordPaint.setColor(Color.parseColor("#54242f"));//字體顏色：咖啡色
        if(deviceRight<1080){
            wordPaint.setTextSize(40);//字體大小
        }else{
            wordPaint.setTextSize(45);//字體大小
        }

        wordPaint.setTypeface(Typeface.DEFAULT_BOLD);//字體

        switch (pageType){
            case LETTER_PAGE1:
                pageContent1 ="";
                pageContent2 ="世紀創造之初，";
                pageContent3 ="星球與星球之間的";
                pageContent4 ="戰爭就開始了...";
                pageContent5 ="";
                break;
            case LETTER_PAGE2:
                pageContent1 ="為了使地球不受外來者";
                pageContent2 ="侵略，自古就生存在";
                pageContent3 ="地球的Mamori背負著";
                pageContent4 ="秘密保護地球的任務，";
                pageContent5 ="抵禦入侵者。";
                break;
            case LETTER_PAGE3:
                pageContent1 ="身為Mamori一員的你，";
                pageContent2 ="同樣承襲了部族傳統...";
                pageContent3 ="";
                pageContent4 ="在惡勢力來襲之際，挺身";
                pageContent5 ="保衛地球和愛人吧！";
                break;
            default:
                pageContent1 ="";
                pageContent2 ="";
                pageContent3 ="";
                pageContent4 ="";
                pageContent5 ="";
        }

        float length1 = wordPaint.measureText(pageContent1);
        float length2 = wordPaint.measureText(pageContent2);
        float length3 = wordPaint.measureText(pageContent3);
        float length4 = wordPaint.measureText(pageContent4);
        float length5 = wordPaint.measureText(pageContent5);

        canvas.drawText(pageContent1,this.deviceRight/2-(length1/2),deviceBottom/2-200, wordPaint);
        canvas.drawText(pageContent2,this.deviceRight/2-(length2/2),deviceBottom/2-100, wordPaint);
        canvas.drawText(pageContent3,this.deviceRight/2-(length3/2),deviceBottom/2, wordPaint);
        canvas.drawText(pageContent4,this.deviceRight/2-(length4/2),deviceBottom/2+100, wordPaint);
        canvas.drawText(pageContent5,this.deviceRight/2-(length5/2),deviceBottom/2+200, wordPaint);

    }

    @Override
    public void changeStatus() {
        storyType = STORY_ON_DAYTIME;
    }

    @Override
    public void update() {
        arrowMove++;
        if(delay<10){
            delay++;
        }else if(delay==10) {
            letterType = LETTER_SHOW;
            if(reelfinish == true) {
                delay++;
            }
        }else if(delay==11){
            switchPage1();
            delay++;
        }
    }


    public void changeNextPage(){//下一頁
        switch (pageType){
            case LETTER_NOCONTENT://第零頁，純黑夜背景（沒有左右箭頭）
                switchPage1();
                break;
            case LETTER_PAGE1:
                switchPage2();
                break;
            case LETTER_PAGE2:
                switchPage3();
                storyType = STORY_ON_DAYTIME;
                break;
            case LETTER_PAGE3:
                switchTutorial();
                break;
            case PAGE_TUTORIAL:
                switchEnd();
                break;
        }
    }

    public void switchPage0(){
        darkType = DARK_SHOW;
        storyType = STORY_ON_NIGHT;
        letterType = LETTER_HIDE;;
        rightArrowType = R_ARROW_HIDE;
    }


    public void switchPage1(){
        darkType = DARK_HIDE;
        storyType = STORY_ON_NIGHT;
        letterType = LETTER_SHOW;
        pageType = LETTER_PAGE1;
        rightArrowType = R_ARROW_ORIGIN;
    }

    public void switchPage2(){
        darkType = DARK_HIDE;
        storyType = STORY_ON_NIGHT;
        letterType = LETTER_SHOW;
        pageType = LETTER_PAGE2;
        rightArrowType = R_ARROW_ORIGIN;

    }

    public void switchPage3(){
        darkRepeat = 230;
        darkType = DARK_SHOW;
        storyType = STORY_ON_DAYTIME;
        letterType = LETTER_SHOW;
        pageType = LETTER_PAGE3;
        rightArrowType = R_ARROW_ORIGIN;
        tutorialType = TUTORIAL_HIDE;
    }

    public void switchTutorial(){
        storyType = STORY_OFF;
        letterType = LETTER_HIDE;
        rightArrowType = R_ARROW_HIDE;
        pageType = PAGE_TUTORIAL;
        storyType = STORY_Tutorial;
        tutorialType = TUTORIAL_SHOW;
    }

    public void switchEnd(){
        tutorialType = TUTORIAL_HIDE;
        this.gameStoryInterface.updateStatus(this,STORY_END);
    }

    public void shake(String value, Context context) {
        if (value.equals("on")) {
            // Get instance of Vibrator from current Context
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 300 milliseconds
            vibrator.vibrate(100);
        }
    }

    @Override
    public int getTop() { return 0; }

    @Override
    public int getBottom() { return 0; }

    @Override
    public int getLeft() { return 0; }

    @Override
    public int getRight() { return 0; }

}

