package angelguardian.guardian.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.json.JSONObject;

import angelguardian.guardian.BitmapManager;
import angelguardian.guardian.obj.Data;
import angelguardian.guardian.obj.Menu;
import angelguardian.guardian.obj.Music;
import angelguardian.guardian.obj.SecondChance;
import angelguardian.guardian.obj.World;
import angelguardian.guardian.obj.GameStory;

public class GameController implements View.OnTouchListener{
    public static final int GAME_STATUS_MENU = 0;
    public static final int GAME_STATUS_GAME = 1;
    public static final int GAME_STATUS_2NDCHANCE = 2;
    public static final int GAME_STATUS_STORY = 4;

    public int level;

    private Context context;

    //menu
    private Menu menu;

    //game
    private World world;
    private boolean variableUpdate = true;

    //secondchance
    private SecondChance secondChance;

    //GameStory
    private GameStory gameStory;

    //Data
    private Data data = new Data();;

    private int currentStatus;

    private int botLimit;
    private int rightLimit;

    //MUSIC
    private Music music;

    public GameController(final Context context, JSONObject jsonObject, BitmapManager bm){
        this.context = context; //must have context to connect gameview and world
        data.setJson(jsonObject);
        level = data.getGamelevel();
        if(level == 0){
            currentStatus = GAME_STATUS_STORY;
        }else {
            currentStatus = GAME_STATUS_MENU; //記錄遊戲流程到哪裡
        }
        Music.setMusic(context);

        menu = new Menu(context, bm, new Menu.MenuStatusInterface() {
            @Override
            public void updateStatus(Menu menu, int status) {
                switch(status){
                    case Menu.MENU_STATUS_STOP:
                        world.setStage(data.getGamelevel());
                        world.setGamelevel(data.getGamelevel());
                        variableUpdate = true;
                        world.onMusic();
                        currentStatus = GAME_STATUS_GAME;
                        break;
                    case Menu.MENU_STATUS_START:
                        menu.setWallet(data.getWallet());
                        menu.setPower(data.getPower());
                        menu.setSpeed(data.getSpeed());
                        menu.setCoinMultiplier(data.getCoinMultiplier());
                        menu.setGamelevel(data.getGamelevel());
                        if(Data.gamelevel > 0) {
                            menu.onMusic(Music.mediaPlayer);
                        }
                        currentStatus = GAME_STATUS_MENU;
                        break;
                }
            }
        });

        world = new World(context, bm, new World.WorldStatusInterface() {
            @Override
            public void updateStatus(int status) {
                switch(status){
                    case World.WORLD_STATUS_START:
                        if(variableUpdate == true) {
                            world.setWallet(data.getWallet());
                            world.setPower(data.getPower());
                            world.setSpeed(data.getSpeed());
                            world.setCoinMultiplier(data.getCoinMultiplier());
                            world.setGamelevel(data.getGamelevel());
                            world.setStage(data.getGamelevel());
                            variableUpdate = false;
                        }
                        currentStatus = GAME_STATUS_GAME;

                        break;
                    case World.WORLD_STATUS_STOP:

                        data.setWallet(world.multiCoin());
                        menu.setWallet(data.getWallet());
                        currentStatus = GAME_STATUS_2NDCHANCE; //the page after you have stopped the game
                        break;
                }
            }
        });

        secondChance = new SecondChance(context, bm, new SecondChance.SecondChanceInterface(){
            @Override
            public void updateStatus(int status) {
                switch (status){
                    case SecondChance.GAME_CONTINUE:

                        if(SecondChance.SECONDCHANCE_PRICE > world.getWallet()) {
                            break;
                        }

                        //Arrange wallet money update
                        data.setWallet(World.wallet);
                        data.reduceWallet(SecondChance.SECONDCHANCE_PRICE);

                        menu.setWallet(data.getWallet());
                        world.setWallet(data.getWallet());
                        world.skullOn();

                        currentStatus = GAME_STATUS_GAME;
                        world.setCharaInvincible(true);  //選擇遊戲continue, 人物暫時變成無敵狀態
                        world.setGameContinue();
                        break;

                    case SecondChance.GAME_RESTART:

                        world.setGameContinue();
                        data.setWallet(world.getWallet());
                        menu.setWallet(data.getWallet());
                        world.normalOn();

                        world.restartGame();
                        currentStatus = GAME_STATUS_MENU;

                        Music.mediaPlayer.start();
                        break;

                    case SecondChance.GAME_CLEAR:

                        world.setGameContinue();

                        data.setGamelevel(world.getGamelevel());
                        menu.setGamelevel(data.getGamelevel());
                        world.setStage(data.getGamelevel());
                        world.restartGame();
                        world.normalOn();

                        currentStatus = GAME_STATUS_GAME;
                        Music.setMusic(context);
                        Music.startGameMusic();
                        break;
                }
            }
        });

        if(Data.gamelevel == 0) {
            gameStory = new GameStory(context, bm, this.menu, new GameStory.GameStoryInterface() {
                @Override
                public void updateStatus(GameStory gameStory, int status) {
                    switch (status) {
                        case GameStory.STORY_START:
                            currentStatus = GAME_STATUS_STORY;
                            Music.startStoryMusic();//開始播放story的音樂
                            break;
                        case GameStory.STORY_END:
                            level = 1;
                            data.setGamelevel(level);
                            menu.setGamelevel(level);
                            Music.storyPlayer.pause();//暫停story的音樂@Leah 11.08
                            Music.playMain();//播放menu音樂@Leah 11.08
                            currentStatus = GAME_STATUS_MENU;
                            break;
                    }

                }
            });
        }
    }

    public void run(){
        switch(currentStatus){
            case GAME_STATUS_MENU:
                menu.updateData();
                break;
            case GAME_STATUS_GAME:
                world.updateData();
                break;
            case GAME_STATUS_2NDCHANCE:
                break;
            case GAME_STATUS_STORY:
                gameStory.update();
                break;
        }

    }

    public void onPaint(Canvas canvas, Paint paint){

        switch(currentStatus){
            case GAME_STATUS_MENU:
                world.onPaint(canvas, paint);
                menu.onPaint(canvas, paint);
                break;
            case GAME_STATUS_GAME:
                world.onPaint(canvas, paint);
                break;
            case GAME_STATUS_2NDCHANCE:
                world.onPaint(canvas, paint);
                secondChance.onPaint(canvas, paint);
                break;
            case GAME_STATUS_STORY:
                gameStory.onPaint(canvas, paint);
                break;
        }
    }


    //UPDATE LIMIT
    public void updateLimit(int botLimit, int rightLimit){
        this.botLimit = botLimit;
        this.rightLimit = rightLimit;
        world.updateLimit(botLimit, rightLimit);
        menu.updateLimit(botLimit, rightLimit);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //view -> gameview

        switch(currentStatus){
            case GAME_STATUS_MENU:
                return menu.onTouch(event, data, menu);
            case GAME_STATUS_GAME:
                return world.onTouch(event);
            case GAME_STATUS_2NDCHANCE:
                return secondChance.onTouch(event);
            case GAME_STATUS_STORY:
                return gameStory.onTouch(event);
        }
        return true;
    }


    //Data 中的JSONObject
    public JSONObject getJson(){
        data.setJsonPause();

        return data.getJson();
    }
    public void setJson(JSONObject jsonObject){
        data.setJson(jsonObject);
    }

}
