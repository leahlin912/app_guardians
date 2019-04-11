package angelguardian.guardian;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.json.JSONObject;

import angelguardian.guardian.controller.GameController;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "GameView"; //for debugging
    private static final int FRAME_PER_SECOND = 30;
    private static final int SECOND_PER_FRAME = 1000/FRAME_PER_SECOND;
    private static long currentTime;
    private BitmapManager bm;  //全部物件共用同一個BitmapManager
    Context context; //mainActivity 當前的畫面(Note: activity 包含context)
    SurfaceHolder surfaceHolder; //comes from surfaceview, use holder to control (超空)canvas
    Thread timer; //thing that can allow 2 functions function at same time

    boolean flag = false;

    int screenWidth;
    int screenHeight;

    Canvas mCanvas = null; // canvas 畫布
    Paint paint; // can decide that border, color, line, etc

    private JSONObject jsonObject;

    MediaPlayer mediaPlayer;

//    World world; //help to not touch gameview.

    GameController gameController;

    public GameView(Context context) {
        super(context);
        this.context = context;
        surfaceHolder = this.getHolder(); //.getHolder it is in SurfaceView and returns mSurfaceHolder
        surfaceHolder.addCallback(this);
        setFocusable(true);

        bm = new BitmapManager(context);
    }

    int x = 0;
    protected void onPaint(Canvas canvas){
        if(surfaceHolder == null) //no surface holder, there will not be canvas so, it will just return it back
            return;
        gameController.onPaint(canvas, paint); //here it decides what to draw
    }



    //.surfaceCreated, .surfaceChanged, .surfaceDestroyed are all methods in Callback interface in SurfaceHolder

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
//        paint.setColor(Color.BLUE);
        flag = true;
        if(timer == null){
            timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(flag){
                        //time is in milliseconds
                        //currentTime is the time you do at first time
                        currentTime = SystemClock.uptimeMillis();

                        synchronized (surfaceHolder){
                            try{
                                mCanvas = surfaceHolder.lockCanvas();
                                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clean the mCanvas

                                //在gamecontroller算完再去畫圖
                                gameController.run();  // Here you will start to run your gamecontroller
                                onPaint(mCanvas); //draw a new one

                            }catch (Exception e){
                                //Log.d(TAG, e.getMessage());
                            }finally {
                                if(mCanvas != null)
                                    surfaceHolder.unlockCanvasAndPost(mCanvas);
                            }
                        }

                        //costTime is the amount of time that will take from currentTime
                        // to when it finishes ==>SystemClock.uptimeMillis() ==> 後一秒
                        long costTime = SystemClock.uptimeMillis() - currentTime;
                        try{
//                            if(SECOND_PER_FRAME - costTime > 0)
//                                //if my second frame - (timetaken to draw sth(costTime) ) is higher than 0
//                                // means i have time left till next frame appears, so
//                                //i let it sleep the amount of time left till next frame
//                                Thread.sleep(SECOND_PER_FRAME - costTime);
//                            else{
//                                Thread.sleep(SECOND_PER_FRAME);
//                                //if time taken to finish was higher than 0, i will just leave it to sleep
//                                //till next time
//                            }

                            while(SECOND_PER_FRAME < costTime){
                                costTime -= SECOND_PER_FRAME;
                            }
                            if(SECOND_PER_FRAME - costTime > 0){
                                Thread.sleep(SECOND_PER_FRAME - costTime);
                            }
                        }catch (Exception e){
//                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            });
        }
        timer.start();

        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.screenHeight = height;
        this.screenWidth = width;

        //因為gamecontroller 負責全部 所以最好是由他來接gameview 設定的邊界 這樣下屬知道邊界在哪
        gameController.updateLimit(screenHeight, screenWidth);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(timer != null){
            timer.interrupt();
            timer = null;
        }
    }

    public void init(){
        gameController = new GameController(context, jsonObject, bm); //here it new gamecontroller
        gameController.setJson(jsonObject);


        //We can use  setOnTouchListener because GameView extends from SurfaceView
        // //and this one has View inside and this one has setOnTouchListener
        setOnTouchListener(gameController);
    }


    public void setFlag(boolean flag){
        this.flag = flag;
    }


    //JSON
    public JSONObject getJson(){
//        Log.d("SHAREDPREFERENCE", "SHAREDPREFERENCE GC: " + gameController.getJson().toString());

        return gameController.getJson();
    }
    public void setJson(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }
}
