package angelguardian.guardian.obj;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import angelguardian.guardian.R;

public class Music {
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer worldPlayer;
    public static MediaPlayer storyPlayer;
    public static SoundPool.Builder soundBuilder;
    public static SoundPool soundPool;

    public Music(Context context){
        storyPlayer = MediaPlayer.create(context, R.raw.gamestory_music);//@Leah 11.08
        mediaPlayer = MediaPlayer.create(context, R.raw.main);
//        worldPlayer = MediaPlayer.create(context, R.raw.battle);
        setMusic(context);
        soundBuilder = new SoundPool.Builder();
        soundBuilder.setMaxStreams(3);
        AudioAttributes.Builder audioAttribute = new AudioAttributes.Builder();
        audioAttribute.setUsage(AudioAttributes.USAGE_GAME);
        soundBuilder.setAudioAttributes(audioAttribute.build());
        soundPool = soundBuilder.build();
    }

    public static void playMain(){
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.start();
    }

    public static void startGameMusic(){
        mediaPlayer.pause();
        worldPlayer.setLooping(true);
        worldPlayer.setVolume(1,1);
        worldPlayer.start();
    }
    public static void startStoryMusic(){//@Leah 11.08
        storyPlayer.setLooping(true);
        storyPlayer.setVolume(1,1);
        storyPlayer.start();
    }

    public static void setMusic(Context context){
        int level;
        if(Data.gamelevel != 0){
            level = Data.gamelevel % 4;
        } else {
            level = 1;
        }
        if(level == Data.stageFour){
            worldPlayer = MediaPlayer.create(context, R.raw.word_music4);
        } else if (level == Data.stageThree){
            worldPlayer = MediaPlayer.create(context, R.raw.word_music3);
        } else if (level == Data.stageTwo){
            worldPlayer = MediaPlayer.create(context, R.raw.word_music2);
        } else if (level == Data.stageOne){
            worldPlayer = MediaPlayer.create(context, R.raw.dragonnest);
        }

    }





}
