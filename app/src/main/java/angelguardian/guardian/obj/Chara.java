package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.R;

public class Chara extends WorldObj implements OnTouchObject {
    private static final String TAG = "Chara";
    private static final int STATUS_STAND = 0;
    private static final int STATUS_RUN_RIGHT = 1;
    private static final int STATUS_RUN_LEFT = 2;
    private static final int CHAR_DELAY = 5;

    private static Bitmap characterBitmap;
    private BitmapManager bm;
    private int bitmapHeight;
    private int bitmapWidth;

    private List<Bullet> bulletArray;
    private boolean bulletFlag;
    private boolean invincibleMode;

    private int bulletDelay = 12; //子彈delay時間，要保持彈性
    private int bulletType;
    private int speed;
    private int power;

    private int rightLimit;

    private int charaNumber;

    private int currentStatus;
    private int currentBitmapPosition;
    private int charaDelayCount;
    private int collisionDelayCount;
    private int bulletDelayCount;

    //角色陣列圖檔中分割成每張小圖的寬度、高度、1/2寬度
    private int cutWidth;
    private int cutHeight;
    private int cutWidthHalf;

    //Chara life
    public static int charaLife;

    //Soundpool
    private static int bulletSound;

    private Rect rectOrigin;
    private Rect rectTarget;

    //BooleanTranslate
    private boolean translate = false;
    private int destination;
    private int direction = 0;

    public Chara(Context context, BitmapManager bm, int x, int y, int speed){
        super(context);
        this.bm = bm;

        this.speed = speed;
        this.x = x;
        this.y = y;
        this.currentStatus = STATUS_RUN_RIGHT;
        this.currentBitmapPosition = 0;
        this.charaDelayCount = 0;
        this.collisionDelayCount = 0;
        this.charaLife = 2;
        this.bulletType = 1;

        int level;
        if(Data.gamelevel != 0){
            level = Data.gamelevel % 4;
        } else {
            level = 1;
        }

        if(level == Data.stageFour){            //level = 0, 第4個場景
            charaNumber = 3;
        } else if (level == Data.stageThree){   //level = 3, 第3個場景
            charaNumber = 2;
        } else if (level == Data.stageTwo){     //level = 2, 第2個場景
            charaNumber = 1;
        } else if (level == Data.stageOne){     //level = 1, 第1個場景
            charaNumber = 0;
        }

        //SoundPool
        bulletSound = Music.soundPool.load(context, R.raw.laser1, 1);

        bulletArray = new ArrayList<>();    //List of bullets
        this.bulletFlag = false;    //能否發射子彈的狀態判斷
        this.bulletDelayCount = 0;

        this.colCenX = this.x + cutWidth / 2;
        this.colCenY = this.y + cutWidth / 2;
        this.colRadius = cutWidthHalf -25;

        rectOrigin = new Rect(0, 0, cutWidth , cutHeight);
        rectTarget = new Rect(x, y, cutWidth + x, cutHeight + y);
    }

    @Override public void onPaint(Canvas canvas, Paint paint) {
        rectOrigin.set(cutWidth* (currentBitmapPosition%4),
                cutHeight * (currentBitmapPosition/4),
                cutWidth * (currentBitmapPosition%4 + 1),
                cutHeight * (currentBitmapPosition/4 + 1));
        rectTarget.set(x, y, cutWidth + x, cutHeight + y);

        canvas.drawBitmap(characterBitmap,
                rectOrigin,
                rectTarget,
                null);

        for(Bullet bullets: bulletArray){
            bullets.onPaint(canvas, paint);
        }
    }

    //到時候可以使用這個換角色
//    public void changeCharacter(){
//        this.charaNumber = (int)(Math.random() * 4);
//    }

    public void update(){
        if(translate == true){
            //If keep pressed then move to the right place
            translationOn();
        }

        if(CHAR_DELAY > charaDelayCount){
            charaDelayCount++;
        }else{
            charaDelayCount = 0;
            switchChara();
        }

        if(bulletFlag == true){
            shoot();
        }

        //更新子彈位置
        for(Bullet bullet : bulletArray){
            bullet.update();    //變更位置往上
            if(bullet.getBottom() <= 0){    //若子彈超出螢幕上邊界
//                bulletArray.remove(bullet);   //從bulletArray移除子彈就會閃爍
                bullet.updateSpeed(0, 0);
            }
        }
    }

    //WORLDOBJ's METHOD
    @Override
    public void changeStatus() {
        if(this.currentStatus == STATUS_RUN_LEFT){
            this.currentStatus = STATUS_RUN_RIGHT;
        } else if(this.currentStatus == STATUS_RUN_RIGHT){
            this.currentStatus = STATUS_RUN_LEFT;
        }
    }

    //UPDATE STATUS WHEN ACTION_MOVE TRIGGERS!
    //INTERFACE METHODS
    @Override
    public void touchEvent(int touchX) {
        if( this.currentStatus == STATUS_RUN_LEFT && touchX > this.x){
            changeStatus();
        } else if(this.currentStatus == STATUS_RUN_RIGHT && touchX < this.x){
            changeStatus();
        }
        this.x = touchX;
        this.colCenX = this.x + cutWidthHalf;
    }

    //CHARMOVE
    public void translationOn(){
        //When pressed it will move to that x location
        int middlePosition = this.x + this.cutWidthHalf;
        int destination = this.destination - cutWidthHalf;

        if(this.currentStatus == 2 && destination > middlePosition){
            changeStatus();
        } else if(this.currentStatus == 1 && destination < middlePosition){
            changeStatus();
        }

        if(middlePosition != destination) {
            if(middlePosition >= destination - 5 && middlePosition <= destination + 5){
                setTranslate(false);
                this.bulletFlag = false;
                this.x = destination;
                return;
            }

            if (destination > middlePosition) {
                if(middlePosition >= destination - 30 && middlePosition <= destination + 30){
                    this.x += 10;
                    return;
                }
                this.x += 50;

            } else if (destination < middlePosition) {
                if(middlePosition >= destination - 30 && middlePosition <= destination + 30){
                    this.x -= 10;
                    return;
                }
                this.x -= 50;
            }
        }
    }


    //Bullet related

    public void setBFlag(boolean k){  //設定bullet的狀態是否可以發射
        this.bulletFlag = k;
    }

    public void shoot(){   //產生子彈
        if(getBulletDelay() > bulletDelayCount){
            bulletDelayCount++;
            //return;
        }else{
            bulletDelayCount = 0;
            this.bulletArray.add(new Bullet(context, bm,this.x + cutWidthHalf, this.y, getBulletType()));
            Music.soundPool.play(bulletSound, 1, 1, 0, 0, 0);
        }
    }

    public void setBulletDelay(int delayTime){
        if(bulletDelay > 0){
            this.bulletDelay -= delayTime;
        }
    }

    public int getBulletDelay(){
        return this.bulletDelay;
    }
    public void setPower(int power){
        Log.d(TAG, "power: " + power);
        for(Bullet bullet : bulletArray){
            bullet.setFirePower(power);
//            Log.d(TAG, "power: " + power);
        }

        if(power / 10 == 1){
            bulletType = 3;
        }
        if(power / 10 == 2){
            bulletType = 5;
        }
    }
    public int getBulletType(){
        return this.bulletType;
    }

    //GETTER
    @Override
    public int getTop() {
        return y;
    }

    @Override
    public int getBottom() {
        return y + this.cutHeight;
    }

    @Override
    public int getLeft() {
        return x;
    }

    @Override
    public int getRight() {
        return x + this.cutWidth;
    }

    public int getMiddle() {
        return this.cutWidthHalf;
    }

    public int getHeight() { return this.cutHeight; }

    public int getWidth(){
        return this.cutWidth;
    }

    public List<Bullet> getBulletArray(){
        return bulletArray;
    }

    public int getCurrentStatus(){
        return this.currentStatus;
    }

    public int getCharaLife(){
        return this.charaLife;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setCharSpeed(int speed){
        this.speed = speed;
    }

    public void shutBulletFlag(){
        this.bulletFlag = false;
    }

    public void updateLimit(int rightLimit){
        this.rightLimit = rightLimit;

        // init bitmap
        if(this.rightLimit >= 1080) {
            this.bitmapHeight = 2303;
            this.bitmapWidth = 872;
            //圖檔大小 and character file
            characterBitmap = bm.getBitmap(R.drawable.allcharas, bitmapWidth, bitmapHeight);

        } else {
            this.bitmapHeight = 1113;
            this.bitmapWidth = 436;
            //圖檔大小 and character file
            this.characterBitmap = bm.getBitmap(R.drawable.allcharassmall, bitmapWidth, bitmapHeight);
        }

        //角色陣列圖檔中分割成每張小圖的寬度、高度、1/2寬度
        this.cutWidth = bitmapWidth / 4;
        this.cutHeight = bitmapHeight / 8;
        this.cutWidthHalf = cutWidth / 2;

//        this.y = botLimit - World.HORIZON_HEIGHT - cutHeight;
        this.y = World.horizon - cutHeight; //10.19
    }

    //Invicible mode related
    public void setInvincible(boolean t){   //***
        this.invincibleMode = t;
    }

    public boolean getInvicible(){  //***
        return this.invincibleMode;
    }

    public void setCollisionDelayCount(int num){ //***
        this.collisionDelayCount = num;
    }

    public int getCollisionDelayCount(){    //***
        return this.collisionDelayCount;
    }

    //Chara Life
    public static void resetCharaLife(){
        charaLife = 2;
    }

    public void deductCharaLife(){
        this.charaLife--;
    }

    //setUP Chara according to Stage level
    public void setChara(int gameLevel){
        if(gameLevel == Data.stageFour){
            charaNumber = 3;
        } else if (gameLevel == Data.stageThree){
            charaNumber = 2;
        } else if (gameLevel == Data.stageTwo){
            charaNumber = 1;
        } else if (gameLevel == Data.stageOne){
            charaNumber = 0;
        }
        switchChara();
    }

    public void switchChara(){
        switch (currentStatus){
            case STATUS_RUN_RIGHT:
                this.currentBitmapPosition = (this.currentBitmapPosition+1)%4 + this.charaNumber * 8;
                break;
            case STATUS_RUN_LEFT:
                this.currentBitmapPosition = (this.currentBitmapPosition+1)%4 + 4 + this.charaNumber * 8;
                break;
            case STATUS_STAND:
                this.currentBitmapPosition = (this.currentBitmapPosition+1)%4 + this.charaNumber * 8;
                break;
        }
    }

    public void skullMode(){
        if(this.rightLimit >= 1080) {
            this.bitmapHeight = 2303;
            this.bitmapWidth = 880;
            //圖檔大小 and character file
            this.characterBitmap = bm.getBitmap(R.drawable.allcharas_skull, bitmapWidth, bitmapHeight);
        } else {
            this.bitmapHeight = 1113;
            this.bitmapWidth = 425;
            //圖檔大小 and character file
            characterBitmap = bm.getBitmap(R.drawable.allcharas_skull_small, bitmapWidth, bitmapHeight);
        }
    }

    public void normalMode(){
        if(this.rightLimit >= 1080) {
            this.bitmapHeight = 2303;
            this.bitmapWidth = 872;
            //圖檔大小 and character file
            this.characterBitmap = bm.getBitmap(R.drawable.allcharas, bitmapWidth, bitmapHeight);
        } else {
            this.bitmapHeight = 1113;
            this.bitmapWidth = 436;
            //圖檔大小 and character file
            characterBitmap = bm.getBitmap(R.drawable.allcharassmall, bitmapWidth, bitmapHeight);
        }
    }

    public void setTranslate(boolean bTranslate){
        this.translate = bTranslate;
    }

    public void setDestination(int destination){
        this.destination =  destination;
    }


}
