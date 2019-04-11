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

import java.util.ArrayList;
import java.util.List;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class World{
    protected Context context;
    private static final String TAG = "World";

    private int deviceLeft;
    private int deviceRight;
    private int deviceBottom;
    public static int horizon;  //遊戲中的地平線y座標
    private int ballCount = 0;
    public int ballGenDelayCount;
    private Vibrator vibrator; //震動型態的變數

    private int gameStatus = 1;

    private static final int G = 1; //gravity
    
    //擁有一個inner interface 因為這個只會處理 world而已
    public static final int WORLD_STATUS_START = 1; //遊戲開始
    public static final int WORLD_STATUS_STOP = 0; //遊戲結束
    public static final int BALL_GEN_DELAY = 65; //球出生的速度(值越小產生越快)
    public static final int BALL_DROP_DELAY = 30;
    public static final int GAME_PASS_DELAY = 90; //球打完切換過關畫面的延遲時間
    public static final int Chara_INVINCIBLE_MODE_DELAY = 90;
    public static final int BALL_FREEZE_DELAY = 399;   //打中特殊球(Freeze)時間暫停長度
    public static final int BALL_FREEZE_RATE = 33;     //有Freeze功能的球出現的機率
    public static final int BALL_ALLCLEAR_RATE = 33;   //有Allclear功能的球出現的機率

    public static final double BALL_VY_COEFFICIENT_A = 0.00202d;  //*** 計算球落地回彈出速度的修正係數
    public static final double BALL_VY_COEFFICIENT_B = 3.5d;  //*** 計算球落地回彈出速度的修正係數
    public static final double BALL_VY_CALCULATION_CONSTANCE_C1 = 0.00809d;  //*** 計算球落地回彈出速度的常數;
    public static final double BALL_VY_CALCULATION_CONSTANCE_C2 = 14d;  //*** 計算球落地回彈出速度的常數;

    //*** 所有WorldObj共用一個bitmapManager
    private BitmapManager bm;

    //SharedPreference
    public static int wallet;
    private int power;
    private int bulletPerSec;
    private double coinMultiplier; //金幣加乘倍數
    private int gameLevel;

    //過關
    private final int SERVEBALL_OFF = 0;
    private final int SERVEBALL_ON = 1;
    private int serveballStatus;
    private int ballCountOfLevel;
    private int gamePassDelayCount;

    //Ball
    public static int passCountOfBreak;//需要達成的擊破球次數，才能破關
    public static int countOfBreak;//計數器，已擊破的球數
    private int freezeDelayCount; //球暫停更新計數器
    private boolean freezeMode; //是否凍結所有球的位置
    private boolean freezeCancel = false;
    private int freezeCancelShow = 0;
    private boolean allClearMode;   //是否清空畫面中所有的球
    private int ballFunction;   //球被打破時的特殊功能

    //Coin
    private int coinImgWidth;
    private int coinImgHeight;
    private Bitmap coinBitmap;
    private Rect coinrectOrigin;
    private Rect coinrectTarget;
    public static int earnCoin; //單一關實際吃到的金幣值
    public static int bonusCoin; //加乘後多得的錢

    public interface WorldStatusInterface{
        public void updateStatus(int status);
    }
    private WorldStatusInterface worldStatusInterface; //存別人處理好的updateStatus method

    // obj
    private Chara chara;
    private Background background;
    private List<WorldObj> worldObjs; // will save worldObjs in List
    private List<Ball> ballList; //save all balls
    private List<WorldObj> coinList; //save all coins
    private List<Bullet> bulletList;
    private List<PlusSign> plusSignList;
    private List<WorldObj> toRemove; //save all Obj to remove
    private List<Ball> toBornList; //save ball to born
    private List<Blasting> blastingList; //save blasting

    //Wallet
    private int rightWallet;
    private int leftWalllet;
    private int walletTextX;

    private int rW;
    private int lW;

    private boolean walletShow;
    private boolean wShowFirst;

    private int walletDelay;
    private final static int WD = 50;

    private int walletYDisplay;
    private int walletXDisplay;


    //SoundPool
    private static int levelUpSound;
    private static int coinSound;

    //Variables
    private float textLength;
    private int keeper;
    private int keeperNum;

    //OnTouch
    private boolean actionDown = false;
    private int prevX;

    //擁有兩個 world constructor, 因為有多種情況 如果不處理worldstatusInterface 的時候就是只用World with context only
    public World(Context context, BitmapManager bm){
        init(context, bm);
    }

    public World(Context context, BitmapManager bm, WorldStatusInterface worldStatusInterface){  //***
        init(context, bm);
        this.worldStatusInterface = worldStatusInterface;
    }

    private void init(Context context, BitmapManager bm){
        this.context = context;
        this.bm = bm;

        this.chara = new Chara(this.context, this.bm,450, this.deviceBottom - 300, 10);   //*** bm
        this.background = new Background(this.context, this.bm, 0 , 0);   //***

        chara.setBulletDelay(Data.bulletPerSec);

        this.bulletList = chara.getBulletArray();
        this.ballList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.toRemove = new ArrayList<>();
        this.toBornList = new ArrayList<>();
        this.blastingList = new ArrayList<>();
        this.plusSignList = new ArrayList<>();
        this.ballGenDelayCount = 0;
        this.gamePassDelayCount = 0;

        this.serveballStatus = SERVEBALL_ON;
//        Log.d("SERVEBALLDEBUG", "" + serveballStatus);

        this.freezeDelayCount = 0; //球凍結時間計數器
        this.freezeMode = false; //是否凍結所有球的位置
        this.allClearMode = false;  //清空畫面中所有球

        walletShow = false;
        wShowFirst = true;
        walletDelay = 0;

        levelUpSound = Music.soundPool.load(context, R.raw.levelup2, 4);
        coinSound = Music.soundPool.load(context, R.raw.coin, 4);

        //~~~~~
        Blasting.genBitmap(bm);
        //~~~~~
    }

    public void updateLimit(int botLimit, int rightLimit){
        this.deviceBottom = botLimit;
        this.deviceRight = rightLimit;

        //球跟錢會到的底線
        this.horizon = (int)(deviceBottom * 0.85);

        background.updateLimit(this.deviceBottom, this.deviceRight);
        chara.updateLimit(this.deviceRight);

        //WALLET
        rW = this.deviceRight;
        lW = this.deviceRight + (int)(this.deviceRight * 0.4);
        walletTextX = rightWallet + (int)(this.deviceRight * 0.15);

        //Coin
        coinImgWidth = 55;
        coinImgHeight = 54;
        coinBitmap = bm.getBitmap(R.drawable.dollar_1, coinImgWidth, coinImgHeight);

        walletYDisplay = (int)(this.deviceRight * 0.075);
        walletXDisplay = (int)(this.deviceRight * 0.65);
        coinrectOrigin = new Rect(0, 0, coinImgWidth, coinImgHeight);
        coinrectTarget = new Rect( walletXDisplay, walletYDisplay, walletXDisplay + coinImgWidth, walletYDisplay + coinImgHeight);

        this.passCountOfBreak = 0;
        this.countOfBreak = 0;
    }

    public void onPaint(Canvas canvas, Paint paint){

        background.onPaint(canvas, paint);


        for(Blasting blasting : blastingList){
            blasting.onPaint(canvas, null); //爆破效果使用自己的paint, world的paint不用傳入
        }

        for(WorldObj coin: coinList){
            coin.onPaint(canvas, paint);
        }
        for(WorldObj plusSign: plusSignList){
            plusSign.onPaint(canvas, paint);
        }

        chara.onPaint(canvas, paint);

        for(Ball ball: ballList){
            ball.onPaint(canvas, null);  //球使用自己的畫筆，world的paint不用傳入
        }

        //this.deviceRight; //origin to -> (int)(this.deviceRight * 0.6)
        rightWallet = rW;

        //this.deviceRight + (int)(this.deviceRight * 0.4); // origin to ->  this.deviceRight
        leftWalllet = lW;

        //rightWallet + (int)(this.deviceRight * 0.15);
        walletTextX = rightWallet + (int)(this.deviceRight * 0.15);

        walletXDisplay = rW + (int)(this.deviceRight * 0.05);

        //Rec for wallet
        paint.setColor(Color.parseColor(ColorsFont.transGray));
        canvas.drawRect(leftWalllet, (int)(this.deviceBottom * 0.02), rightWallet,(int)(this.deviceBottom * 0.1), paint);

        //wallet number display
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(0.0556 * this.deviceRight));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        if(this.wallet >= 1000){
            canvas.drawText(String.format ("%.1f", this.wallet/1000f) + "K", walletTextX, (int)(this.deviceBottom * 0.075), paint);
        } else {
            canvas.drawText("" + this.wallet, walletTextX, (int)(this.deviceBottom * 0.075), paint);
        }

        //Coin
        canvas.drawBitmap(coinBitmap, coinrectOrigin, coinrectTarget,null);
        coinrectTarget.set(walletXDisplay, walletYDisplay, walletXDisplay + coinImgWidth, walletYDisplay + coinImgHeight);

        //FREEZE CANCEL NOTICE
        if(freezeCancel == true){
            paint.setShadowLayer(5, 5, 5, Color.parseColor(ColorsFont.darkGrayM));
            paint.setTypeface(Font.snowwinter);
            paint.setTextSize((int)(this.deviceRight * 0.138));
            paint.setColor(Color.WHITE);
            textLength = paint.measureText(TextKeeper.FREEZEC);
            canvas.drawText(TextKeeper.FREEZEC, (int) (this.deviceRight / 2 - textLength / 2), (int) (this.deviceBottom * 0.25), paint);
            textLength = paint.measureText(TextKeeper.FREEZE);
            canvas.drawText(TextKeeper.FREEZE, (int) (this.deviceRight / 2 - textLength / 2), (int) (this.deviceBottom * 0.35), paint);

            freezeCancelShow++;
            if(freezeCancelShow == 20){
                freezeCancelShow = 0;
                freezeCancel = false;
            }
            paint.clearShadowLayer();
        }

        //FREEZE freezeMode  BALL_FREEZE_DELAY >= freezeDelayCount
        keeper = BALL_FREEZE_DELAY - freezeDelayCount;  //freeze mode 倒數剩餘的時間

        keeperNum = (int)(keeper/40f) + 1;
        if(freezeMode == true && gamePassDelayCount == 0){
//            Log.d("FREEZE","BALL_FREEZE_DELAY: " + BALL_FREEZE_DELAY);
//            Log.d("FREEZE","freezeDelayCount: " + freezeDelayCount);
//            Log.d("FREEZE","keeper: " + keeper);
//            Log.d("FREEZE","BOOLEAN: " + (keeper % 40 >= 25));

//***            if(ballList.isEmpty()){
//                freezeMode = false;
//                freezeCancel = true;
//            }
            //畫出freeze字樣
            if(keeper != 0) {

                paint.setShadowLayer(5, 5, 5, Color.parseColor(ColorsFont.darkGrayM));
                paint.setTypeface(Font.snowwinter);
                paint.setTextSize((int)(this.deviceRight * 0.204));
                paint.setColor(Color.WHITE);
                textLength = paint.measureText(TextKeeper.FREEZE);
                canvas.drawText(TextKeeper.FREEZE, (int) (this.deviceRight / 2 - textLength / 2), (int) (this.deviceBottom * 0.25), paint);
            }
            //畫出freeze mode 倒數時間
            if(keeper % 40 >= 25){
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize((int)(this.deviceRight * 0.185));

                textLength = paint.measureText(Integer.toString(keeperNum));
                canvas.drawText(Integer.toString(keeperNum), (int) (this.deviceRight / 2 - textLength / 2), (int) (this.deviceBottom * 0.4), paint);
            }
            paint.clearShadowLayer();
//***            freezeDelayCount ++;

        }
//***        if(BALL_FREEZE_DELAY == freezeDelayCount){
//            freezeMode = false;
//            freezeDelayCount = 0;
//        }

        removeObj();
        toRemove.clear();
        System.gc();
    }

    public void updateData(){

        if(walletShow == true){
            updateShowWallet();
        }

//        Log.d("SERVEBALLDEBUG gamelevel", "" + gameLevel);


        if(gameLevel > 200) {
            this.ballCountOfLevel = 30;//依照等級，球數做調整
        } else if(gameLevel > 100) {
//            Log.d("SERVEBALLDEBUG startt", "" + ballCountOfLevel);

            this.ballCountOfLevel = 25;//依照等級，球數做調整
//            Log.d("SERVEBALLDEBUG startd", "" + ballCountOfLevel);

        } else if(gameLevel > 50){
            this.ballCountOfLevel = 20;//依照等級，球數做調整
        } else if(gameLevel > 10){
            this.ballCountOfLevel = 15;//依照等級，球數做調整
        } else if(gameLevel > 0){
            this.ballCountOfLevel = (gameLevel);//依照等級，球數做調整
        }

//        Log.d("SERVEBALLDEBUG HH", "" + serveballStatus);
//        Log.d("SERVEBALLDEBUG ballCount", "" + ballCount);
//        Log.d("SERVEBALLDEBUG ballCountOfLevel", "" + ballCountOfLevel);


        if(ballCount >= ballCountOfLevel){
            this.serveballStatus = SERVEBALL_OFF;
        }
//        Log.d("SERVEBALLDEBUG GG", "" + serveballStatus);
//        Log.d("SERVEBALLDEBUG ballCount", "" + ballCount);
//
//        Log.d("SERVEBALLDEBUG ballCountOfLevel", "" + ballCountOfLevel);


        //判斷球是否和子彈發生碰撞
        for(Bullet bullet : bulletList){
            if(bullet.getBottom() <= 0){    //若子彈超出螢幕上邊界
                toRemove.add(bullet);
                bullet.updateSpeed(0, 0);
//                return;
            }
            for(Ball ball : ballList){
                if(bulletCollision(ball, bullet)){
                    ball.updateHp(Data.power + 1);
                    //若球的血量為0(被擊破)
                    if(ball.getHp() <= 0){
                        //若擊破freeze功能的球
                        if(ball.getBallFunction() == Ball.BALL_FUNCTION_FREEZE){
                            freezeMode = true;
                        }//若擊破allClear功能的球
                        else if(ball.getBallFunction() == Ball.BALL_FUNCTION_ALLCLEAR ){
                            allClearMode = true;
                        }
                        else if(freezeMode != true){//若擊破的球沒有功能，且不為freeze狀態，球分裂生小球
                            genBallBaby(ball);  //大、中size的球打爆會生小球
                        }

                        //震動, 錢幣產生, 爆破效果, 球消失
                        shake("on", context);
//                        if((int)(Math.random() * 2) == 1) {
                        generateCoin(ball.getInitHp(), ball.x, ball.y); //para
//                        }
                        //*** 產生爆破效果
                        blastingList.add(genBlasting(ball.getLevel(), ball.colCenX, ball.colCenY));

                        //ballList.remove(ball);
                        ball.setXY(-800, -800); //球要爆裂，將球位置移到螢幕顯示區域以外(-800,-800)
                        ball.updateSpeed(0,0);  //球速度歸零
                        toRemove.add(ball);

                        //球的擊破數+1
                        countOfBreak++;

                    }
                }
            }
        }

//        Log.d("SERVEBALLDEBUG 22", "" + serveballStatus);

        if(this.serveballStatus == SERVEBALL_OFF){//先判斷球已經發完了 true

            if(countOfBreak >= passCountOfBreak){
                if( GAME_PASS_DELAY >= gamePassDelayCount){  //球打完延遲一段時間才顯示破關畫面
                    gamePassDelayCount++;
                }
                else{
                    gameStatus = 0;
                    gameLevel++;
                    Music.worldPlayer.pause();
                    int count = 0;
                    while(count != 50000){
                        count++;
                    }
                    Music.soundPool.play(levelUpSound, 100, 100, 10, 0, 1);
                    count = 0;
                    while(count != 50000){
                        count++;
                    }
                    Music.worldPlayer.start();
//                    Music.worldPlayer.pause();
//                        gamePassDelayCount = 0;
                }
            }
        }
        // 將球破裂生出的球放入ballList中
        for(int i = toBornList.size()-1; i>=0; i--){
            ballList.add(toBornList.get(i));
            toBornList.remove(i);
        }

        // add ball to stage
        if(BALL_GEN_DELAY > ballGenDelayCount){
            ballGenDelayCount++;
        }else{
            ballGenDelayCount = 0;
            // 已經產生的球數 < 該level預計的發球數ballCountOfLevel
            if(ballCount < ballCountOfLevel && freezeMode != true){

                ballList.add(genBallEnterStage());    //產生一顆要出場的球

                this.ballCount++;   //已經產生的球數增加
                this.passCountOfBreak++;  //需要擊破的球數增加
            }
        }

        //bullet speed & power update
        if(this.bulletPerSec != Data.bulletPerSec) {
            chara.setBulletDelay(Data.bulletPerSec);
        }
        if(this.power != Data.power) {
            chara.setPower(Data.power);
        }

        //chara update
        chara.update();

        // ballList update
        Log.d("HEY ALLCLEAR ", " C: " + allClearMode);
        if(allClearMode){  //*** 若為allClear模式
            for(Ball ball : ballList){
                blastingList.add(genBlasting(ball.getLevel(), ball.colCenX, ball.colCenY));    //產生破裂效果
                generateCoin(ball.getInitHp(), ball.x, ball.y);  //產生金幣 //para
                ball.setXY(-800, -800);
                ball.updateSpeed(0,0);
                toRemove.add(ball);
                countOfBreak++;
            }
            shake("on", context);
            countOfBreak--;  //被擊破的特殊球還未從ballList移除,避免重複計算,countOfBreak需-1
            allClearMode = false;
        }else{
            //判斷是否有freeze功能的球被擊破
            if(freezeMode == true && BALL_FREEZE_DELAY >= freezeDelayCount && checkBallShow(ballList)) {
                freezeDelayCount++;
//                Log.d("HEY 1111","1111");
                for(Ball ball : ballList){
                    //偵測球是否和人、地面及螢幕邊界發生碰撞
                    ballHitDetect(ball);
//                    updateBall(ball);
                }

            }
            else if(freezeMode == true && !checkBallShow(ballList)){
                freezeMode = false;
                freezeCancel = true;
//                Log.d("HEY 2222","222");
                for(Ball ball : ballList){
                    //偵測球是否和人、地面及螢幕邊界發生碰撞
                    ballHitDetect(ball);
//                    updateBall(ball);
                }

            }
            else{
//                Log.d("HEY 3333","3333");

                freezeDelayCount = 0;
                freezeMode = false;
                for(Ball ball : ballList){
                    //偵測球是否和人、地面及螢幕邊界發生碰撞
                    ballHitDetect(ball);
                    updateBall(ball);
                }
            }
        }

        //BlastingList update
        for(Blasting blasting : blastingList){
            updateBlasting(blasting);
        }

        //CoinList update
        for(WorldObj coin : coinList){
            updateCoin((Coin)coin);
        }

        //PlusSign update
        for(WorldObj plusSign : plusSignList){
            updatePlusSign((PlusSign)plusSign);
        }

        //if my interface is not null -> then let the game start
        if(worldStatusInterface != null){
            worldStatusInterface.updateStatus(WORLD_STATUS_START);
        }
        if(gameStatus == 0) {
            rW = this.deviceRight;
            lW = this.deviceRight + (int)(this.deviceRight * 0.4);
            walletTextX = rightWallet + (int)(this.deviceRight * 0.15);
            worldStatusInterface.updateStatus(WORLD_STATUS_STOP);
        }
    }


    
    private void updatePlusSign(PlusSign plusSign){
        plusSign.update();
        if(plusSign.getDelay() < 30){
            plusSign.addDelay();
        }else{
            toRemove.add(plusSign);
            plusSign.changeStatus();
            plusSign.resetDelay();
        }
    }

    private void updateBlasting(Blasting blasting){   //*** 更新blasting
        blasting.update();
        //判斷blasting是否要繼續顯示
        if(blasting.getToShow() != true){
            toRemove.add(blasting);
        }
    }

    private void ballHitDetect(Ball ball){

        // 球體碰到人物，球會停止不動
        if(chara.getInvicible()){
            //若為2nd Chance繼續遊戲(continue game)，延遲偵測碰撞
            if(Chara_INVINCIBLE_MODE_DELAY >= chara.getCollisionDelayCount()){
                chara.setCollisionDelayCount(chara.getCollisionDelayCount()+1);
            }else{
                chara.setInvincible(false);
            }
        }else {   //若不是繼續遊戲(restart game)
            if (ball.getBottom() >= chara.getTop() + 65 &&
                    (ball.getRight() >= chara.getLeft() + 60 && ball.getLeft() <= chara.getRight() - 60)) {
//                ball.updateSpeed(0, 0);
                chara.setCollisionDelayCount(0);
                chara.deductCharaLife();
                gameStatus = 0;
                return;
            }
        }
    }

    private void updateBall(Ball ball) {

        //球剛產生尚未落地，先delay不落下，以x軸向平移出場
        while(BALL_DROP_DELAY > ball.getDropDelayCount() && !ball.getIsASon()) {
            ball.setDropDelayCount(ball.getDropDelayCount() + 1);
            ball.updateSpeed(ball.getVX(), 0);
            ball.update();
            return;
        }

        // 偵測球體落地回彈, when Ball touch floor it will bounce back when get to it
        if (ball.getBottom() >= horizon) { //球碰到地板
            ball.updateSpeed(ball.getVX(), -ball.getVY());  //每次撞到底部，變為反作用力(速度為負值)
            ball.addVyAdjust(1);
        }
        //球正常落下或上升
        else {
            //依不同大小的螢幕及球level, 計算出第一次落地回彈的vy, 藉此得到相同比例的彈跳高度
            int vyFirstTime = (int)(((BALL_VY_CALCULATION_CONSTANCE_C1 + ball.getLevel() * BALL_VY_COEFFICIENT_A) * this.deviceBottom)
                    + (BALL_VY_CALCULATION_CONSTANCE_C2 + BALL_VY_COEFFICIENT_B * ball.getLevel()));
            if(ball.getVyAdjust() == 1 ){ //球第一次落地後的下一次update需做vy修正，以調整各不同Level的球回彈指定的高度
                ball.updateSpeed(ball.getVX(),  -vyFirstTime);
                ball.addVyAdjust(1);
            }
            else{   //修正過一次後即不用再修正
                ball.updateSpeed(ball.getVX(), ball.getVY() + G);  //g為重力加速度
            }
        }

        // 偵測球體碰到螢幕左右邊框回彈 when get to left or right, it will keep a normal speed when bounce back
        if (ball.getRight() >= deviceRight){
            ball.updateSpeed(-Ball.BALL_VX, ball.getVY());
        }
        if(ball.getLeft() <= deviceLeft) {
            ball.updateSpeed(Ball.BALL_VX, ball.getVY());
        }

        ball.update();  //位置變更
    }

    private void updateCoin(Coin coin) {
        //coin被人物吃掉消失
        if(coin.getBottom() >= chara.getTop()){
            if( (coin.getLeft() <= chara.getRight()&& coin.getLeft() >= chara.getLeft())
                    ||(coin.getRight() >= chara.getLeft()&& coin.getRight() <= chara.getRight())){
                coin.updateSpeed(0, 0);
                coin.changeStatus();//改變狀態
                toRemove.add(coin);//回收
                Music.soundPool.play(coinSound, 100, 100, 5, 0, 3);
                showPlusSign(coin.getLeft(), coin.getTop(), coin.getValue());
                walletShow = true;
                walletDelay--;
            }
        }

        if(walletShow == true && rW == (int)(this.deviceRight * 0.6)) {
            walletDelay++;
        }
        if(walletDelay == WD){
            wShowFirst = false;
            walletDelay = 0;
        }

        //coin碰到螢幕左右邊框回彈
        if (coin.getRight() >= deviceRight) {
            coin.updateSpeed(-3, coin.getVY());
        } else if (coin.getLeft() <= deviceLeft) {
            coin.updateSpeed(3, coin.getVY());
        }

        //coin碰到地面回彈
        if(coin.getBottom() >= horizon && coin.getTimes()== 3 && coin.getGoUpDown()==2) {//掉到地板且速度為零，且已經反彈三次
            coin.updateSpeed(0, 0);
            coin.setGoUpDown(0);//設定停止
        }else if(coin.getBottom() >= horizon && coin.getGoUpDown() == 2) {//撞到下邊界反彈
            coin.updateSpeed(coin.getVX(), -20 + (coin.getTimes() * 4));
            coin.setGoUpDown(1);//設定反轉往上
            coin.addTimes();
        }else if(coin.getVX() == 0 && coin.getVY() == 0 && coin.getGoUpDown() == 0) {//如果已經暫停在地上
            coin.updateSpeed(0, 0);
            if (500 > coin.getDelay()) {
                coin.addDelay();
                return;
            } else {
                coin.resetDelay();
            }
            coin.changeStatus();//改變狀態
            toRemove.add(coin);//回收
        }else if(coin.getBottom() < horizon && coin.getVY() == 0 && coin.getGoUpDown() == 1) {//垂直速度為零時，垂直反向
            coin.updateSpeed(coin.getVX(), 10 - (coin.getTimes()*4));
            coin.setGoUpDown(2);//設定反轉往下
        }
        else {
            coin.updateSpeed(coin.getVX(), coin.getVY() + G);
        }

        coin.update();  //位置變更
    }

    public void showPlusSign(int x, int y, int number){
        PlusSign plusSign = new PlusSign(context, bm, x, y, number);
        plusSignList.add(plusSign);

        //Add amount of coins to wallet
        this.wallet += plusSign.getPointNum();
        this.earnCoin += plusSign.getPointNum(); //單一關實際吃到的金幣值
        this.bonusCoin = (int)(this.earnCoin * (getCoinMultiplier() - 1));
    }
    public int multiCoin(){ //加乘計算後的金幣值
        this.wallet += this.bonusCoin;
        return this.wallet;
    }

    public void updateShowWallet(){

        if(rW >= (int)(this.deviceRight * 0.6) && wShowFirst == true){
            rW -= 15;
            lW -= 15;
            if(rW <= (int)(this.deviceRight * 0.6)){
                rW = (int)(this.deviceRight * 0.6);
                lW = this.deviceRight;
//                wShowFirst = false;
            }
        }

        if(rW <= this.deviceRight && wShowFirst == false){
            rW += 10;
            lW += 10;
            if(rW >= this.deviceRight){
                rW = this.deviceRight;
                lW = this.deviceRight + (int)(this.deviceRight * 0.4);
                wShowFirst = true;
                walletShow = false;
            }
        }
//        if(rW == this.deviceRight){
//            walletShow = false;
//        }
    }

    public boolean onTouch(MotionEvent event){

        //Get 角色
        int charWidth2 = chara.getWidth()/2;

        //event.getX(); 取得手指按下位置的X座標
        int fingerX = (int) event.getX();
        int eventTouch = event.getAction();

        if(fingerX > prevX - 20 && fingerX < prevX + 20 && eventTouch != 1 && eventTouch != 0){
            chara.setBFlag(true);
            return true;
        }

        switch(eventTouch){
            case MotionEvent.ACTION_DOWN:

                chara.setTranslate(true);
                prevX = fingerX;
                chara.setDestination(fingerX);
                chara.setBFlag(true);  //手指按下，bullet變成可發射狀態
                break;

            case MotionEvent.ACTION_MOVE:
                prevX = - 400;
                chara.setBFlag(true);
                chara.setTranslate(false);

                //判斷角色是否離開螢幕的匡 有的話把他設定在裡面
                if(fingerX + 15 >= deviceRight ){
                    fingerX = deviceRight - 15;
                }else if(fingerX - 15 <= deviceLeft ){
                    fingerX = deviceLeft + 15;
                }

                //更新角色位子 可能會和chara.update()重複而造成問題
                chara.touchEvent(fingerX - charWidth2);

                break;

            case MotionEvent.ACTION_UP:
                prevX = - 400;
                chara.setTranslate(false);
                chara.setBFlag(false);  //手指離開，bullet變成不可發射狀態
                break;
        }
        return true;
    }

    private Ball genBallEnterStage(){
        int ballLevel = (int)(Math.random()*3)+1;
        int hideX = 0;
        switch(ballLevel){
            case 1: //ballLevel最小顆的球一機率決定是否具有function
                hideX = Ball.BALL_Level1_DIAMETER;
                //*** 球依照設定的機率決定ballFunction(Freeze, Allclear, 或 Null)
                int random = (int)(Math.random()* 100);
                if(random < BALL_FREEZE_RATE){
                    this.ballFunction = Ball.BALL_FUNCTION_FREEZE;  //ballFunction = Freeze = 1
                }else if(random < (BALL_FREEZE_RATE + BALL_ALLCLEAR_RATE) && random >= BALL_FREEZE_RATE){
                    this.ballFunction = Ball.BALL_FUNCTION_ALLCLEAR;  //ballFunction = All clear = 2
                }else{
                    this.ballFunction = Ball.BALL_FUNCTION_NULL; //ballFunction = null = 0
                }
                break;
            case 2:
                hideX = Ball.BALL_Level2_DIAMETER;
                this.ballFunction = Ball.BALL_FUNCTION_NULL;
                break;
            case 3:
                hideX = Ball.BALL_Level3_DIAMETER;
                this.ballFunction = Ball.BALL_FUNCTION_NULL;
                break;
        }
        //*** 隨機產生球出現的X、Y座標
        int initX = - hideX + (hideX + deviceRight) * (int)(Math.random() * 2);
        //X座標隨機出現在螢幕外左/右側
        int initY = (int)(this.deviceBottom * (0.02 + 0.2 * (int)(Math.random() * 3)));
        //Ｙ座標隨機畫面高度(*0.02, *0.22, *0.42)
        int hp = (int)((Math.random() * 5 ) + 1 ) * gameLevel ;

        return new Ball(context, bm, gameLevel, initX, initY, hp, ballLevel, ballFunction);
    }

    private void genBallBaby(Ball ball){
        int exBallLevel = ball.getLevel();
        int exBallHP = ball.getInitHp();
        Ball ballSon1;
        Ball ballSon2;
        //Level 2和 Level 3的球分裂時才會生小球
        if(exBallLevel == 2 || exBallLevel == 3){
            ballSon1 = new Ball(context, bm, gameLevel, ball.x -35, ball.y - 50, exBallHP / 2, exBallLevel-1);
            ballSon1.updateSpeed(-3, -10);
            ballSon1.setIsSun(true);
            ballSon2 = new Ball(context, bm, gameLevel, ball.x +35, ball.y - 50, exBallHP / 2, exBallLevel-1);
            ballSon2.updateSpeed(3, -10);
            ballSon2.setIsSun(true);
            toBornList.add(ballSon1);
            toBornList.add(ballSon2);
            this.passCountOfBreak += 2; //每次球分裂產生2個小孩,過關需要擊破的球數往上+2
        }
    }

    private Blasting genBlasting(int ballLevel, int ballCenX, int ballCenY){  //爆破效果
        int x = 0;
        int y = 0;
        switch(ballLevel){
            //修正x,y起始點的位置
            case 1:
                x = ballCenX - Blasting.BLASTING_LEVEL1_DIAMETER/2;
                y = ballCenY - Blasting.BLASTING_LEVEL1_DIAMETER/2;
                break;
            case 2:
                x = ballCenX - Blasting.BLASTING_LEVEL2_DIAMETER/2;
                y = ballCenY - Blasting.BLASTING_LEVEL2_DIAMETER/2;
                break;
            case 3:
                x = ballCenX - Blasting.BLASTING_LEVEL3_DIAMETER/2;
                y = ballCenY - Blasting.BLASTING_LEVEL3_DIAMETER/2;
                break;
        }
        return new Blasting(context, bm, x, y, ballLevel);
    }

    private void generateCoin(int ballInitialHp, int x, int y){//球傳回的資料
        int count;  //球體原先的數值換算成金幣數量
        if(ballInitialHp < 50){
            count = 1;
        }else{
            count = 2;
        }
        int directionR = 0;
        for(int i=0;i<count;i++) {
            int randomNum =((int)(Math.random()*3)*200);//隨意設定的random範圍
            if(directionR%2==0){
                coinList.add(new Coin(context, bm, x, y + randomNum, 1, ballInitialHp));
            }else {
                coinList.add(new Coin(context, bm, x, y + randomNum, 2, ballInitialHp));
            }
            directionR++;
        }
    }

    //Obj以半徑方式判斷和其他Obj是否碰撞
    private boolean collisionDetect(WorldObj obj1, WorldObj obj2){

        int objDistance; //兩物體中心點的距離
        int radiusSum;   //兩物體半徑的和

        objDistance = (int)(Math.sqrt(Math.pow(obj2.colCenX - obj1.colCenX, 2) + Math.pow(obj2.colCenY - obj1.colCenY , 2)));
        radiusSum = obj1.colRadius + obj2.colRadius;
        if(objDistance <= radiusSum){  //判斷兩物體的中心距離，是否小於2物體半徑的和
            return true;
        }else{
            return false;
        }
    }

    //判斷是否有球在畫面中
    private boolean checkBallShow(List<Ball> ballList){
        int size = ballList.size();
        for(int i = 0; i<size; i++){
            if(ballList.get(i).getLeft() < this.deviceRight && ballList.get(i).getRight() > this.deviceLeft){
                return true;
            }
        }
        return false;
    }

    //判斷球和不同類型子彈是否碰撞
    private boolean bulletCollision(WorldObj ball, Bullet bullet){
        int objDistance1;
        int objDistance2;
        int objDistance3;
        int radiusSum;
        radiusSum = ball.colRadius + bullet.colRadius;

        switch(bullet.getBulletType()) {
            case 1:
                objDistance1 = (int) (Math.sqrt(Math.pow(bullet.colCenX1 - ball.colCenX, 2) + Math.pow(bullet.colCenY - ball.colCenY, 2)));
                if (objDistance1 <= radiusSum) {
                    bullet.setCurrentStatus(Bullet.BULLET_1OUT);
                    bullet.setXY(-100, -300);   //發生碰撞，將子彈位置移到螢幕顯示區域以外(-100,-100)
                    bullet.updateSpeed(0,0);    //將子彈速度歸零
                    toRemove.add(bullet);
                    return true;
                } else {
                    bullet.setCurrentStatus(Bullet.BULLET_1ALL);
                    return false;
                }
//                break;
            case 3:
                objDistance1 = (int) (Math.sqrt(Math.pow(bullet.colCenX1 - ball.colCenX, 2) + Math.pow(bullet.colCenY - ball.colCenY, 2)));
                objDistance2 = (int) (Math.sqrt(Math.pow(bullet.colCenX3 - ball.colCenX, 2) + Math.pow(bullet.colCenY - ball.colCenY, 2)));
                if (objDistance1 <= radiusSum) {
                    if (objDistance2 <= radiusSum) {
                        bullet.setCurrentStatus(Bullet.BULLET_3OUT);
                        bullet.setXY(-100, -300);   //發生碰撞，將子彈位置移到螢幕顯示區域以外(-100,-100)
                        bullet.updateSpeed(0,0);    //將子彈速度歸零
                        toRemove.add(bullet);
                        return true;
                    } else {
                        bullet.setBulletTypeR(1);
                        bullet.setCurrentStatus(Bullet.BULLET_3C);
                        return true;
                    }
                } else if (objDistance2 <= radiusSum && objDistance1 > radiusSum) {
                    bullet.setBulletTypeL(1);
                    bullet.setCurrentStatus(Bullet.BULLET_3A);
                    return true;
                } else {
                    bullet.setCurrentStatus(Bullet.BULLET_3ALL);
                    return false;
                }
//                break;
            case 5:
                objDistance1 = (int) (Math.sqrt(Math.pow(bullet.colCenX1 - ball.colCenX, 2) + Math.pow(bullet.colCenY - ball.colCenY, 2)));
                objDistance2 = (int) (Math.sqrt(Math.pow(bullet.colCenX3 - ball.colCenX, 2) + Math.pow(bullet.colCenY - ball.colCenY, 2)));
                objDistance3 = (int) (Math.sqrt(Math.pow(bullet.colCenX5 - ball.colCenX, 2) + Math.pow(bullet.colCenY - ball.colCenY, 2)));
                if (objDistance1 <= radiusSum) {
                    if (objDistance2 <= radiusSum) {
                        if (objDistance3 <= radiusSum) {
                            bullet.setCurrentStatus(Bullet.BULLET_5OUT);
                            bullet.setXY(-100, -300);   //發生碰撞，將子彈位置移到螢幕顯示區域以外(-100,-100)
                            bullet.updateSpeed(0,0);    //將子彈速度歸零
                            toRemove.add(bullet);
                            return true;
                        } else {
                            bullet.setBulletTypeR(1);
                            bullet.setCurrentStatus(Bullet.BULLET_5E);
                            return true;
                        }
                    } else {
                        bullet.setBulletTypeR(3);
                        bullet.setCurrentStatus(Bullet.BULLET_5CDE);
                        return true;
                    }
                } else if (objDistance3 <= radiusSum && objDistance1 > radiusSum) {
                    if (objDistance2 <= radiusSum) {
                        bullet.setBulletTypeL(1);
                        bullet.setCurrentStatus(Bullet.BULLET_5A);
                        return true;
                    } else {
                        bullet.setBulletTypeL(3);
                        bullet.setCurrentStatus(Bullet.BULLET_5ABC);
                        return true;
                    }
                }else{
                    bullet.setCurrentStatus(Bullet.BULLET_5ALL);
                    return false;
                }
//                break;
        }
        return false;
    }

    public void removeObj(){
        int countToRemove = toRemove.size();
        for(int i = 0; i<countToRemove;i++){
            if(toRemove.get(i) instanceof Ball){
                ballList.remove(toRemove.get(i));
            }
            else if(toRemove.get(i) instanceof Bullet){
                bulletList.remove(toRemove.get(i));
            }
            else if(toRemove.get(i) instanceof Coin){
                coinList.remove(toRemove.get(i));
            }
            else if(toRemove.get(i) instanceof PlusSign){
                plusSignList.remove(toRemove.get(i));
            }
            else if(toRemove.get(i) instanceof Blasting){
                blastingList.remove(toRemove.get(i));
            }
        }
    }

    public void setGameContinue(){
        this.gameStatus = 1;
    }

    public void setCharaInvincible(boolean t){
        this.chara.setInvincible(t);
    }

    public void restartGame(){
        this.bulletList = chara.getBulletArray();
        this.ballList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.toRemove = new ArrayList<>();
        this.toBornList = new ArrayList<>();
        this.plusSignList = new ArrayList<>();
        this.ballCount = 0;
        this.gamePassDelayCount = 0;
        this.ballGenDelayCount = 0;
        this.chara.shutBulletFlag();

        this.passCountOfBreak = 0;
        this.countOfBreak = 0;

        this.freezeDelayCount = 0; //球凍結時間計數器
        this.freezeMode = false; //是否凍結所有球的位置
        this.allClearMode = false; //是否清空畫面中所有的球

        this.serveballStatus = SERVEBALL_ON;

        walletShow = false;
        wShowFirst = true;
        walletDelay = 0;
        earnCoin = 0;
        bonusCoin = 0;

        rW = this.deviceRight;
        lW = this.deviceRight + (int)(this.deviceRight * 0.4);
        walletTextX = rightWallet + (int)(this.deviceRight * 0.15);

        removeObj();
        System.gc();

        updateData();
        Music.worldPlayer.pause();
    }

    public void shake(String value, Context context) {
        if (value.equals("on")) {
            // Get instance of Vibrator from current Context
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 300 milliseconds
            vibrator.vibrate(100);
        }
    }

    //GET & SET DATA
    public void setWallet(int wallet){
        this.wallet = wallet;
    }
    public void setPower(int power){
        this.power = power;
    }
    public void setSpeed(int speed){
        this.bulletPerSec = speed;
    }
    public void setCoinMultiplier(double coinMultiplier){
        this.coinMultiplier = coinMultiplier;
    }
    public void setGamelevel(int gameLevel){
        this.gameLevel = gameLevel;
    }

    public int getWallet(){return this.wallet; }
    public int getPower(){return this.power; }
    public int getSpeed(){return this.bulletPerSec; }
    public double getCoinMultiplier(){return this.coinMultiplier; }
    public int getGamelevel(){
        return this.gameLevel;
    }

    //Set Stage changes
    public void setStage(int dataGameLevel){
        dataGameLevel = dataGameLevel % 4;
        this.chara.setChara(dataGameLevel);
        this.background.setBackground(dataGameLevel);
    }

    public void onMusic(){
        Music.mediaPlayer.pause();
        Music.setMusic(context);
//        Music.worldPlayer= MediaPlayer.create(context, R.raw.battle);
        Music.worldPlayer.setLooping(true);
        Music.worldPlayer.start();
//        Music.startGameMusic();
    }

    //SKULL
    public void skullOn(){
        chara.skullMode();
    }

    public void normalOn(){
        chara.normalMode();
    }



}

