package angelguardian.guardian;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import angelguardian.guardian.obj.Data;
import angelguardian.guardian.obj.Font;
import angelguardian.guardian.obj.Music;
//import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String NEWPLAYER = "{\"WALLET\":88,\"POWER\":11,\"SPEED\":7,\"COINMULTIPLIER\":1.2,\"GAMELEVEL\":75}";

    //MediaPlayer
    Music music;
    Font font;

    GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        music = new Music(this);

        font = new Font(this);

        gameView = new GameView(this);
        setContentView(gameView);

        //Insert info into SharedPreferences
        String result = getSharedPreferences("DATA", MODE_PRIVATE)
                .getString("jsonObject", NEWPLAYER);
        Log.d("JSONRENEW", result);

        try {
            JSONObject item = new JSONObject(result);
            gameView.setJson(item);
        }catch(JSONException e){

        }

    }

    @Override
    public void onPause(){
        super.onPause();
        if(gameView != null) {
            gameView.setFlag(false); //it will stop updating as flag == false
        }

        if(Music.mediaPlayer != null && Music.mediaPlayer.isPlaying()){
            Music.mediaPlayer.pause();
        }
        if(Music.worldPlayer != null && Music.worldPlayer.isPlaying()){
            Music.worldPlayer.pause();
        }
        if(Data.gamelevel == 0 ){ //@Leah 11.08
            Music.storyPlayer.pause();
        }


        //Save into Sharepreference, key is jsonObject
        SharedPreferences spref = getSharedPreferences("DATA", MODE_PRIVATE);
        spref.edit().putString("jsonObject", gameView.getJson().toString()).commit();
//        Log.d("SHAREDPREFERENCE", "SHAREDPREFERENCE MA: " + gameView.getJson().toString());
    }

    //When you have stopped the game and want to come back
    public void onResume() {
        super.onResume();
        if(gameView != null) {
            gameView.setFlag(true);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(Music.mediaPlayer != null && !Music.mediaPlayer.isPlaying()&& Data.gamelevel != 0){
            Music.mediaPlayer.start();
//            gameView.onMusic(Music.mediaPlayer);
        }

        String result = getSharedPreferences("DATA", MODE_PRIVATE)
                .getString("jsonObject", NEWPLAYER);
//        Log.d("SHAREDPREFERENCE", "GET_SHAREDPREFERENCE result MA: " + result);

        try {
            JSONObject item = new JSONObject(result);
            gameView.setJson(item);
        }catch(JSONException e){
        }
    }
}
